package com.jiaruiblog.service;

/**
 * @ClassName LikeService
 * @Description TODO
 * @Author luojiarui
 * @Date 2023/2/2 22:07
 * @Version 1.0
 **/
public interface LikeService {

    // 点赞
    void like(String userId, Integer entityType, String entityId);

    // 获取点赞的数量
    Long findEntityLikeCount(Integer entityType, String entityId);

    // 获取当前用户点赞的状态
    int findEntityLikeStatus(String userId, Integer entityType, String entityId);


}
