package com.jiaruiblog.service.impl;

import com.jiaruiblog.entity.CollectDocRelationship;
import com.jiaruiblog.enums.RedisActionEnum;
import com.jiaruiblog.service.CollectService;
import com.jiaruiblog.service.LikeService;
import com.jiaruiblog.task.like.UserLikeDetail;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @ClassName LikeServiceImpl
 * @Description 点赞服务实现类，参考文档： https://bbs.huaweicloud.com/blogs/345948
 * @author luojiarui
 * @Date 2023/2/2 22:07
 * @Version 1.0
 **/
@Slf4j
@Service
public class LikeServiceImpl implements LikeService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private CollectService collectService;

    // 对实体进行点赞的类型
    // 0: entityType，1表示点赞；2表示收藏信息
    // 1: 用户信息
    public static final String ENTITY_LIKE_KEY_FORMAT = "like:entity:{0}:{1}";

    @Override
    public void like(String userId, Integer entityType, String entityId) {
        // 参数校验
        if (!StringUtils.hasText(userId) || entityType == null || !StringUtils.hasText(entityId)) {
            log.warn("点赞操作参数无效: userId={}, entityType={}, entityId={}", userId, entityType, entityId);
            throw new IllegalArgumentException("点赞操作参数不能为空");
        }

        // 验证实体类型是否有效
        RedisActionEnum actionEnum = RedisActionEnum.getActionByCode(entityType);
        if (actionEnum == null) {
            log.warn("无效的实体类型: {}", entityType);
            throw new IllegalArgumentException("无效的实体类型: " + entityType);
        }

        try {
            redisTemplate.execute(new SessionCallback() {
                @Override
                public Object execute(RedisOperations operations) throws DataAccessException {
                    // 构建被点赞的实体对应redis的key
                    String entityLikeKey = MessageFormat.format(ENTITY_LIKE_KEY_FORMAT, entityType, entityId);
                    
                    // 判断集合中是否有userId这个值
                    Boolean isMember = operations.opsForSet().isMember(entityLikeKey, userId);
                    
                    // 开启事务
                    operations.multi();

                    if (Boolean.TRUE.equals(isMember)) {
                        // 移除userId这个值（取消点赞/收藏）
                        operations.opsForSet().remove(entityLikeKey, userId);
                        log.debug("用户 {} 取消了对实体 {} 的{}操作", userId, entityId, actionEnum.getDescription());
                        
                        // 从核心数据库中删除
                        CollectDocRelationship relationship = new CollectDocRelationship();
                        relationship.setDocId(entityId);
                        relationship.setRedisActionEnum(actionEnum);
                        relationship.setUserId(userId);
                        collectService.remove(relationship);
                    } else {
                        // 添加userId这个值（点赞/收藏）
                        operations.opsForSet().add(entityLikeKey, userId);
                        log.debug("用户 {} 对实体 {} 进行了{}操作", userId, entityId, actionEnum.getDescription());
                    }
                    
                    // 提交事务
                    return operations.exec();
                }
            });
            
            log.info("用户 {} 对实体 {} 的{}操作执行成功", userId, entityId, actionEnum.getDescription());
        } catch (Exception e) {
            log.error("点赞操作执行失败: userId={}, entityType={}, entityId={}", userId, entityType, entityId, e);
            throw new RuntimeException("点赞操作执行失败", e);
        }
    }

    @Override
    public Long findEntityLikeCount(Integer entityType, String entityId) {
        if (entityType == null || !StringUtils.hasText(entityId)) {
            log.warn("查询点赞数量参数无效: entityType={}, entityId={}", entityType, entityId);
            return 0L;
        }

        try {
            String entityLikeKey = MessageFormat.format(ENTITY_LIKE_KEY_FORMAT, entityType, entityId);
            Long count = redisTemplate.opsForSet().size(entityLikeKey);
            log.debug("实体 {} 的点赞数量: {}", entityId, count);
            return count != null ? count : 0L;
        } catch (Exception e) {
            log.error("查询点赞数量失败: entityType={}, entityId={}", entityType, entityId, e);
            return 0L;
        }
    }

    @Override
    public int findEntityLikeStatus(String userId, Integer entityType, String entityId) {
        if (!StringUtils.hasText(userId) || entityType == null || !StringUtils.hasText(entityId)) {
            log.warn("查询点赞状态参数无效: userId={}, entityType={}, entityId={}", userId, entityType, entityId);
            return 0;
        }

        try {
            String entityLikeKey = MessageFormat.format(ENTITY_LIKE_KEY_FORMAT, entityType, entityId);
            Boolean isMember = redisTemplate.opsForSet().isMember(entityLikeKey, userId);
            int status = Boolean.TRUE.equals(isMember) ? 1 : 0;
            log.debug("用户 {} 对实体 {} 的点赞状态: {}", userId, entityId, status);
            return status;
        } catch (Exception e) {
            log.error("查询点赞状态失败: userId={}, entityType={}, entityId={}", userId, entityType, entityId, e);
            return 0;
        }
    }

    /**
     * 从redis中获取获取点赞和收藏的数据
     * @return 用户点赞详情列表
     */
    public List<UserLikeDetail> getLikedDataFromRedis() {
        List<UserLikeDetail> result = new ArrayList<>();
        
        try {
            Set<String> setKeys = redisTemplate.keys("like:entity:*");
            if (CollectionUtils.isEmpty(setKeys)) {
                log.debug("Redis中没有找到点赞相关的key");
                return result;
            }

            log.info("从Redis中获取到 {} 个点赞相关的key", setKeys.size());
            
            for (String key : setKeys) {
                Set<Object> members = redisTemplate.opsForSet().members(key);
                if (CollectionUtils.isEmpty(members)) {
                    log.debug("Key {} 中没有成员数据", key);
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
                for (Object member : members) {
                    String memberStr = member != null ? member.toString() : null;
                    if (!StringUtils.hasText(memberStr)) {
                        log.warn("发现无效的成员数据，从Redis中移除: key={}, member={}", key, member);
                        redisTemplate.opsForSet().remove(key, member);
                        continue;
                    }
                    
                    UserLikeDetail userLikeDetail = new UserLikeDetail();
                    userLikeDetail.setUserId(memberStr);
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

    @Override
    public void transLikedFromRedis2DB() {
        log.info("开始从Redis同步点赞数据到数据库");
        
        try {
            List<UserLikeDetail> likedDataFromRedis = getLikedDataFromRedis();
            if (CollectionUtils.isEmpty(likedDataFromRedis)) {
                log.info("没有需要同步的点赞数据");
                return;
            }

            // 过滤出点赞和收藏的数据
            List<UserLikeDetail> validData = likedDataFromRedis.stream()
                    .filter(item -> item.getAction() != null && 
                            (item.getAction().equals(RedisActionEnum.LIKE) || 
                             item.getAction().equals(RedisActionEnum.COLLECT)))
                    .collect(Collectors.toList());

            if (CollectionUtils.isEmpty(validData)) {
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
                    } else {
                        log.debug("成功保存点赞关系: userId={}, entityId={}, action={}", 
                                userLikeDetail.getUserId(), userLikeDetail.getEntityId(), userLikeDetail.getAction());
                    }
                } catch (Exception e) {
                    log.error("保存点赞关系时发生异常: userId={}, entityId={}, action={}", 
                            userLikeDetail.getUserId(), userLikeDetail.getEntityId(), userLikeDetail.getAction(), e);
                    saveFailedList.add(userLikeDetail);
                }
            }

            // 从redis中清除保存失败的信息
            if (!CollectionUtils.isEmpty(saveFailedList)) {
                log.warn("有 {} 条数据保存失败，将从Redis中移除", saveFailedList.size());
                for (UserLikeDetail userLikeDetail : saveFailedList) {
                    try {
                        String key = MessageFormat.format(ENTITY_LIKE_KEY_FORMAT, 
                                userLikeDetail.getAction().getCode(), userLikeDetail.getEntityId());
                        redisTemplate.opsForSet().remove(key, userLikeDetail.getUserId());
                        log.debug("从Redis中移除失败的点赞数据: key={}, userId={}", key, userLikeDetail.getUserId());
                    } catch (Exception e) {
                        log.error("从Redis移除失败数据时发生异常: userId={}, entityId={}", 
                                userLikeDetail.getUserId(), userLikeDetail.getEntityId(), e);
                    }
                }
            }

            log.info("Redis到数据库的点赞数据同步完成，成功同步 {} 条，失败 {} 条", 
                    validData.size() - saveFailedList.size(), saveFailedList.size());
                    
        } catch (Exception e) {
            log.error("从Redis同步点赞数据到数据库时发生异常", e);
            throw new RuntimeException("同步点赞数据失败", e);
        }
    }

    /**
     * 将UserLikeDetail转换为CollectDocRelationship
     * @param userLikeDetail 用户点赞详情
     * @return 收藏文档关系对象
     */
    private CollectDocRelationship userLikeDetailSwitch(UserLikeDetail userLikeDetail) {
        if (userLikeDetail == null) {
            throw new IllegalArgumentException("UserLikeDetail不能为空");
        }
        
        CollectDocRelationship relationship = new CollectDocRelationship();
        relationship.setDocId(userLikeDetail.getEntityId());
        relationship.setUserId(userLikeDetail.getUserId());
        relationship.setRedisActionEnum(userLikeDetail.getAction());
        return relationship;
    }
}
