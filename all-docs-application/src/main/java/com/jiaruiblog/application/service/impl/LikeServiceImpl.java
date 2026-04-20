package com.jiaruiblog.application.service.impl;

import com.jiaruiblog.application.service.LikeService;
import com.jiaruiblog.application.task.like.UserLikeDetail;
import com.jiaruiblog.domain.entity.CollectDocRelationship;
import com.jiaruiblog.domain.entity.LikeDocRelationship;
import com.jiaruiblog.application.service.CollectService;
import com.jiaruiblog.enums.RedisActionEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author luojiarui
 **/
@Slf4j
@Service
public class LikeServiceImpl implements LikeService {

    @Resource
    private CollectService collectService;

    @Resource
    private StringRedisTemplate redisTemplate;

    // 对实体进行点赞的类型
    // 0: entityType，1表示点赞；2表示收藏信息
    // 1: 用户信息
    public static final String ENTITY_LIKE_KEY_FORMAT = "like:entity:{0}:{1}";

    @Override
    public void like(String userId, Integer entityType, String entityId) {
        // TODO: Implement with Redis
    }

    @Override
    public Long findEntityLikeCount(Integer entityType, String entityId) {
        if (entityType == null || entityId == null || entityId.isEmpty()) {
            return 0L;
        }
        try {
            String entityLikeKey = MessageFormat.format(ENTITY_LIKE_KEY_FORMAT, entityType, entityId);
            Long count = redisTemplate.opsForSet().size(entityLikeKey);
            return count != null ? count : 0L;
        } catch (Exception e) {
            log.error("查询点赞数量失败: entityType={}, entityId={}", entityType, entityId, e);
            return 0L;
        }
    }

    @Override
    public int findEntityLikeStatus(String userId, Integer entityType, String entityId) {
        if (userId == null || userId.isEmpty() || entityType == null || entityId == null || entityId.isEmpty()) {
            return 0;
        }
        try {
            String entityLikeKey = MessageFormat.format(ENTITY_LIKE_KEY_FORMAT, entityType, entityId);
            Boolean isMember = redisTemplate.opsForSet().isMember(entityLikeKey, userId);
            return Boolean.TRUE.equals(isMember) ? 1 : 0;
        } catch (Exception e) {
            log.error("查询点赞状态失败: userId={}, entityType={}, entityId={}", userId, entityType, entityId, e);
            return 0;
        }
    }

    @Override
    public void insert(LikeDocRelationship like) {
    }

    @Override
    public Boolean insertRelationShip(LikeDocRelationship like) {
        return false;
    }

    @Override
    public void remove(LikeDocRelationship like) {
    }

    @Override
    public Long likeNum(String docId) {
        return 0L;
    }

    @Override
    public void removeRelateByDocId(String docId) {
    }

    @Override
    public void transLikedFromRedis2DB() {
        log.info("开始从Redis同步点赞数据到数据库");

        try {
            List<UserLikeDetail> likedDataFromRedis = getLikedDataFromRedis();
            if (likedDataFromRedis.isEmpty()) {
                log.info("没有需要同步的点赞数据");
                return;
            }

            // 过滤出点赞和收藏的数据
            List<UserLikeDetail> validData = likedDataFromRedis.stream()
                    .filter(item -> item.getAction() != null &&
                            (item.getAction().equals(RedisActionEnum.LIKE) ||
                             item.getAction().equals(RedisActionEnum.COLLECT)))
                    .toList();

            if (validData.isEmpty()) {
                log.info("没有有效的点赞或收藏数据需要同步");
                return;
            }

            log.info("开始同步 {} 条点赞/收藏数据到数据库", validData.size());

            List<UserLikeDetail> saveFailedList = new ArrayList<>();

            // 批量保存点赞和收藏信息
            for (UserLikeDetail userLikeDetail : validData) {
                try {
                    CollectDocRelationship relationship = userLikeDetailSwitch(userLikeDetail);
                    Boolean success = collectService.insertRelationShip(relationship);

                    if (Boolean.FALSE.equals(success)) {
                        log.warn("保存点赞关系失败: userId={}, entityId={}, action={}",
                                userLikeDetail.getUserId(), userLikeDetail.getEntityId(), userLikeDetail.getAction());
                        saveFailedList.add(userLikeDetail);
                    }
                } catch (Exception e) {
                    log.error("保存点赞关系时发生异常: userId={}, entityId={}, action={}",
                            userLikeDetail.getUserId(), userLikeDetail.getEntityId(), userLikeDetail.getAction(), e);
                    saveFailedList.add(userLikeDetail);
                }
            }

            // 从redis中清除保存失败的信息
            for (UserLikeDetail userLikeDetail : saveFailedList) {
                try {
                    String key = MessageFormat.format(ENTITY_LIKE_KEY_FORMAT,
                            userLikeDetail.getAction().getCode(), userLikeDetail.getEntityId());
                    redisTemplate.opsForSet().remove(key, userLikeDetail.getUserId());
                } catch (Exception e) {
                    log.error("从Redis移除失败数据时发生异常: userId={}, entityId={}",
                            userLikeDetail.getUserId(), userLikeDetail.getEntityId(), e);
                }
            }

            log.info("Redis到数据库的点赞数据同步完成，成功同步 {} 条，失败 {} 条",
                    validData.size() - saveFailedList.size(), saveFailedList.size());

        } catch (Exception e) {
            log.error("从Redis同步点赞数据到数据库时发生异常", e);
        }
    }

    /**
     * 从redis中获取获取点赞和收藏的数据
     * @return 用户点赞详情列表
     */
    private List<UserLikeDetail> getLikedDataFromRedis() {
        List<UserLikeDetail> result = new ArrayList<>();

        try {
            Set<String> setKeys = redisTemplate.keys("like:entity:*");
            if (setKeys == null || setKeys.isEmpty()) {
                log.debug("Redis中没有找到点赞相关的key");
                return result;
            }

            log.info("从Redis中获取到 {} 个点赞相关的key", setKeys.size());

            for (String key : setKeys) {
                Set<String> members = redisTemplate.opsForSet().members(key);
                if (members == null || members.isEmpty()) {
                    continue;
                }

                // 分离出动作类型，实体id
                String[] split = key.split(":");
                if (split.length < 4) {
                    log.warn("Redis key格式不正确: {}", key);
                    continue;
                }

                String actionType = split[2];
                String entityId = split[3];
                RedisActionEnum redisActionEnum = RedisActionEnum.getActionByCode(Integer.valueOf(actionType));

                if (redisActionEnum == null) {
                    log.warn("无效的动作类型: {}", actionType);
                    continue;
                }

                // 组装成 UserLikeDetail 对象
                for (String member : members) {
                    if (member == null || member.isEmpty()) {
                        continue;
                    }

                    UserLikeDetail userLikeDetail = new UserLikeDetail();
                    userLikeDetail.setUserId(member);
                    userLikeDetail.setEntityId(entityId);
                    userLikeDetail.setAction(redisActionEnum);
                    result.add(userLikeDetail);
                }
            }

            log.info("从Redis中获取到 {} 条点赞数据", result.size());
            return result;
        } catch (Exception e) {
            log.error("从Redis获取点赞数据失败", e);
            return result;
        }
    }

    /**
     * 将UserLikeDetail转换为CollectDocRelationship
     * @param userLikeDetail 用户点赞详情
     * @return 收藏文档关系对象
     */
    private CollectDocRelationship userLikeDetailSwitch(UserLikeDetail userLikeDetail) {
        if (userLikeDetail == null) {
            return null;
        }

        CollectDocRelationship relationship = new CollectDocRelationship();
        relationship.setDocId(userLikeDetail.getEntityId());
        relationship.setUserId(userLikeDetail.getUserId());
        relationship.setRedisActionEnum(userLikeDetail.getAction());
        return relationship;
    }
}