package com.jiaruiblog.service.impl;

import com.jiaruiblog.entity.CollectDocRelationship;
import com.jiaruiblog.enums.RedisActionEnum;
import com.jiaruiblog.service.CollectService;
import com.jiaruiblog.service.LikeService;
import com.jiaruiblog.task.like.UserLikeDetail;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @ClassName LikeServiceImpl
 * @Description 参考文档： https://bbs.huaweicloud.com/blogs/345948
 * @Author luojiarui
 * @Date 2023/2/2 22:07
 * @Version 1.0
 **/
@Service
public class LikeServiceImpl implements LikeService {

    @Resource
    RedisTemplate redisTemplate;

    @Resource
    CollectService collectService;

    // 对实体进行点赞的类型
    // 0: entityType，1表示点赞；2表示收藏信息
    // 1: 用户信息
    public static final String ENTITY_LIKE_KEY_FORMAT = "like:entity:{0}:{1}";


    @Override
    public void like(String userId, Integer entityType, String entityId) {

        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {

                // 构建被点赞的实体对应redis的key
                String entityLikeKey = MessageFormat.format(ENTITY_LIKE_KEY_FORMAT, entityType, entityId);
                // 构建被点赞的实体对应的作者再redis中的key,用于统计后期某用户总共收获了多少个赞
                // 判断集合中是否有userId这个值
                Boolean isMember = operations.opsForSet().isMember(entityLikeKey, userId);
                // 开启事务
                operations.multi();

                if (Boolean.TRUE.equals(isMember)) {
                    // 移除userId这个值
                    operations.opsForSet().remove(entityLikeKey, userId);
                    // 从核心数据库中删除
                    CollectDocRelationship relationship = new CollectDocRelationship();
                    relationship.setDocId(entityId);
                    relationship.setRedisActionEnum(RedisActionEnum.getActionByCode(entityType));
                    relationship.setUserId(userId);
                    collectService.remove(relationship);
                } else {
                    operations.opsForSet().add(entityLikeKey, userId);
                }
                // 提交事务
                return operations.exec();
            }
        });
    }

    // 查询某实体点赞的数量
    @Override
    public Long findEntityLikeCount(Integer entityType, String entityId) {
        String entityLikeKey = MessageFormat.format(ENTITY_LIKE_KEY_FORMAT, entityType, entityId);
        return redisTemplate.opsForSet().size(entityLikeKey);
    }

    // 查询某人对某实体的点赞状态
    @Override
    public int findEntityLikeStatus(String userId, Integer entityType, String entityId) {
        String entityLikeKey = MessageFormat.format(ENTITY_LIKE_KEY_FORMAT, entityType, entityId);
        return redisTemplate.opsForSet().isMember(entityLikeKey, userId) ? 1 : 0;
    }

    /**
     * @return java.util.List
     * @Author luojiarui
     * @Description 从redis中获取获取点赞和收藏的数据
     * @Date 11:56 2023/4/5
     * @Param []
     **/
    public List<UserLikeDetail> getLikedDataFromRedis() {
        ArrayList<UserLikeDetail> result = new ArrayList<>();
        Set<String> setKeys = redisTemplate.keys("like:entity:*");
        if (CollectionUtils.isEmpty(setKeys)) {
            return result;
        }
        for (String key : setKeys) {
            Set<String> members = redisTemplate.opsForSet().members(key);
            if (CollectionUtils.isEmpty(members)) {
                continue;
            }
            // 分离出 动作类型，实体id
            String[] split = key.split(":");
            String actionType = split[2];
            String entityId = split[3];
            RedisActionEnum redisActionEnum = RedisActionEnum.getActionByCode(Integer.valueOf(actionType));

            // 组装成 UserLike 对象
            for (String member : members) {
                if (member == null || redisActionEnum == null) {
                    redisTemplate.opsForSet().remove(key, member);
                    continue;
                }
                UserLikeDetail userLikeDetail = new UserLikeDetail();
                userLikeDetail.setUserId(member);
                userLikeDetail.setEntityId(entityId);
                userLikeDetail.setAction(redisActionEnum);
                result.add(userLikeDetail);
            }
            // 存到 list 后从 Redis 中删除；Redis中保存了一份点赞的信息
            // redisTemplate.opsForSet().remove(key, members);
        }
        return result;
    }

    @Override
    public void transLikedFromRedis2DB() {
        List<UserLikeDetail> likedDataFromRedis = getLikedDataFromRedis();
        if (CollectionUtils.isEmpty(likedDataFromRedis)) {
            return;
        }
        List<UserLikeDetail> collect = likedDataFromRedis.stream().filter(item ->
                item.getAction().equals(RedisActionEnum.LIKE)
                        || item.getAction().equals(RedisActionEnum.COLLECT))
                .collect(Collectors.toList());

        List<UserLikeDetail> saveFailedList = new ArrayList<>();
        // 仅仅保存点赞和评论的信息
        for (UserLikeDetail userLikeDetail : collect) {
            CollectDocRelationship relationship = userLikeDetailSwitch(userLikeDetail);
            Boolean aBoolean = collectService.insertRelationShip(relationship);
            if (Boolean.FALSE.equals(aBoolean)) {
                saveFailedList.add(userLikeDetail);
            }
        }
        // 从redis中清除保存失败的信息
        for (UserLikeDetail userLikeDetail : saveFailedList) {
            String key = MessageFormat.format(ENTITY_LIKE_KEY_FORMAT, userLikeDetail.getAction().getCode(),
                    userLikeDetail.getEntityId());
            redisTemplate.opsForSet().remove(key, userLikeDetail.getUserId());
        }
    }

    private CollectDocRelationship userLikeDetailSwitch(UserLikeDetail userLikeDetail) {
        CollectDocRelationship relationship = new CollectDocRelationship();
        relationship.setDocId(userLikeDetail.getEntityId());
        relationship.setUserId(userLikeDetail.getUserId());
        relationship.setRedisActionEnum(userLikeDetail.getAction());
        return relationship;
    }

}
