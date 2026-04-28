package com.jiaruiblog.application.service.impl;

import com.jiaruiblog.application.service.CollectService;
import com.jiaruiblog.application.service.LikeService;
import com.jiaruiblog.application.service.RedisService;
import com.jiaruiblog.common.enums.RedisActionEnum;
import com.jiaruiblog.domain.entity.po.CollectDocRelationship;
import com.jiaruiblog.domain.entity.po.LikeDocRelationship;
import com.jiaruiblog.infrastructure.repository.mysql.LikeDocRelationshipMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Date;

/**
 * @author luojiarui
 **/
@Slf4j
@Service
public class LikeServiceImpl implements LikeService {

    @Resource
    private CollectService collectService;

    @Resource
    private RedisService redisService;

    @Resource
    private LikeDocRelationshipMapper likeDocRelationshipMapper;

    public static final String ENTITY_LIKE_KEY_FORMAT = "like:entity:{0}:{1}";

    @Override
    public boolean like(String userId, Integer entityType, String entityId) {
        if (userId == null || entityType == null || entityId == null || entityId.isEmpty()) {
            return false;
        }
        String entityLikeKey = MessageFormat.format(ENTITY_LIKE_KEY_FORMAT, entityType, entityId);

        // 查询当前点赞状态
        boolean isLiked = redisService.isSetMember(entityLikeKey, userId);

        LikeDocRelationship like = new LikeDocRelationship();
        like.setUserId(userId);
        like.setEntityType(entityType);
        like.setEntityId(entityId);
        like.setCreateDate(new Date());

        if (isLiked) {
            // 已点赞 → 取消点赞
            redisService.deleteSetMember(entityLikeKey, userId);
            redisService.incrementDocScore(entityId, -1);
            remove(like);
            log.debug("用户 {} 取消点赞实体 {}:{}", userId, entityType, entityId);
            return false;
        } else {
            // 未点赞 → 执行点赞
            redisService.setSet(entityLikeKey, userId);
            redisService.incrementDocScore(entityId, 1);
            insertRelationShip(like);
            log.debug("用户 {} 点赞实体 {}:{}", userId, entityType, entityId);
            return true;
        }
    }

    @Override
    public Long findEntityLikeCount(Integer entityType, String entityId) {
        if (entityType == null || entityId == null || entityId.isEmpty()) {
            return 0L;
        }
        try {
            String entityLikeKey = MessageFormat.format(ENTITY_LIKE_KEY_FORMAT, entityType, entityId);
            Long redisCount = redisService.getSetSize(entityLikeKey);
            if (redisCount != null && redisCount > 0) {
                return redisCount;
            }
            // Redis 没有从 DB 获取，使用正确的 entityType 查询数量
            return likeDocRelationshipMapper.countByEntityIdAndEntityType(entityId, entityType);
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
            return redisService.isSetMember(entityLikeKey, userId) ? 1 : 0;
        } catch (Exception e) {
            log.error("查询点赞状态失败: userId={}, entityType={}, entityId={}", userId, entityType, entityId, e);
            return 0;
        }
    }

    @Override
    public void insert(LikeDocRelationship like) {
        if (like == null) {
            return;
        }
        try {
            CollectDocRelationship collect = new CollectDocRelationship();
            collect.setUserId(like.getUserId());
            collect.setDocId(like.getEntityId());
            collect.setRedisActionEnum(RedisActionEnum.getActionByCode(like.getEntityType()));
            collectService.insert(collect);
            log.info("点赞关系保存成功: userId={}, docId={}", like.getUserId(), like.getEntityId());
        } catch (Exception e) {
            log.error("保存点赞关系失败", e);
        }
    }

    @Override
    public Boolean insertRelationShip(LikeDocRelationship like) {
        if (like == null) {
            return false;
        }
        try {
            CollectDocRelationship collect = new CollectDocRelationship();
            collect.setUserId(like.getUserId());
            collect.setDocId(like.getEntityId());
            collect.setRedisActionEnum(RedisActionEnum.getActionByCode(like.getEntityType()));
            return collectService.insertRelationShip(collect);
        } catch (Exception e) {
            log.error("保存点赞关系失败", e);
            return false;
        }
    }

    @Override
    public void remove(LikeDocRelationship like) {
        if (like == null) {
            return;
        }
        try {
            CollectDocRelationship collect = new CollectDocRelationship();
            collect.setUserId(like.getUserId());
            collect.setDocId(like.getEntityId());
            collect.setRedisActionEnum(RedisActionEnum.getActionByCode(like.getEntityType()));
            collectService.remove(collect);
            log.info("点赞关系删除成功: userId={}, docId={}", like.getUserId(), like.getEntityId());
        } catch (Exception e) {
            log.error("删除点赞关系失败", e);
        }
    }

    @Override
    public Long likeNum(String docId) {
        if (docId == null || docId.isEmpty()) {
            return 0L;
        }
        try {
            Long redisCount = findEntityLikeCount(RedisActionEnum.LIKE.getCode(), docId);
            if (redisCount != null && redisCount > 0) {
                return redisCount;
            }
            // Redis 没有从 DB 获取，使用正确的 entityType 查询点赞数量
            return likeDocRelationshipMapper.countByEntityIdAndEntityType(docId, RedisActionEnum.LIKE.getCode());
        } catch (Exception e) {
            log.error("查询点赞数量失败: docId={}", docId, e);
            return 0L;
        }
    }

    @Override
    public void removeRelateByDocId(String docId) {
        if (docId == null || docId.isEmpty()) {
            return;
        }
        try {
            collectService.removeRelateByDocId(docId);
            log.info("删除文档关联的点赞关系: docId={}", docId);
        } catch (Exception e) {
            log.error("删除文档点赞关系失败: docId={}", docId, e);
        }
    }
}
