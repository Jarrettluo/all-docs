package com.jiaruiblog.application.service;

import com.jiaruiblog.domain.entity.LikeDocRelationship;

/**
 * @author Jarrett Luo
 * @Date 2022/6/7 11:39
 * @Version 1.0
 */
public interface LikeService {

    /**
     * 增加点赞，取消点赞；增加收藏，取消收藏
     * @param userId 用户id
     * @param entityType 实体类型：1：点赞；2：收藏
     * @param entityId 实体的id
     */
    void like(String userId, Integer entityType, String entityId);

    /**
     * 获取点赞的数量
     * @param entityType 实体类型
     * @param entityId 实体id
     * @return 返回点赞的数量
     */
    Long findEntityLikeCount(Integer entityType, String entityId);

    /**
     * 获取当前用户点赞的状态
     * @param userId 用户id
     * @param entityType 实体类型：1：点赞；2：收藏
     * @param entityId 实体的id
     * @return 返回该用户点赞的状态
     */
    int findEntityLikeStatus(String userId, Integer entityType, String entityId);

    /**
     * @author luojiarui
     * @Description 新增点赞
     * @Date 13:40 2023/4/5
     * @Param [like]
     **/
    void insert(LikeDocRelationship like);

    /**
     * @author luojiarui
     * @Description 保存点赞/收藏信息到数据库中
     * @Date 13:43 2023/4/5
     * @Param [like]
     * @return java.lang.Boolean
     **/
    Boolean insertRelationShip(LikeDocRelationship like);

    /**
     * @author luojiarui
     * @Description 移除点赞
     * @Date 13:40 2023/4/5
     * @Param [like]
     **/
    void remove(LikeDocRelationship like);

    /**
     * @author luojiarui
     * @Description 查询文档点赞数量
     * @Date 13:45 2023/4/5
     * @Param [docId]
     * @return java.lang.Long
     **/
    Long likeNum(String docId);

    /**
     * @author luojiarui
     * @Description 根据文档ID移除所有相关点赞关系
     * @Date 13:45 2023/4/5
     * @Param [docId]
     **/
    void removeRelateByDocId(String docId);

    /**
     * @author luojiarui
     * @Description 将Redis中的点赞数据同步到数据库
     * @Date 13:45 2023/4/5
     **/
    void transLikedFromRedis2DB();
}