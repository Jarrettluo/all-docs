package com.jiaruiblog.domain.repository;

import com.jiaruiblog.domain.entity.CollectDocRelationship;

import java.util.List;

/**
 * @ClassName CollectRepository
 * @Description 收藏仓储接口
 * @author luojiarui
 * @Date 2022/6/4 10:30 上午
 * @Version 1.0
 **/
public interface CollectRepository {

    /**
     * 保存收藏关系
     * @param collect 收藏关系实体
     */
    void save(CollectDocRelationship collect);

    /**
     * 删除收藏关系
     * @param collect 收藏关系实体
     */
    void remove(CollectDocRelationship collect);

    /**
     * 根据文档ID和用户ID查询收藏关系
     * @param docId 文档ID
     * @param userId 用户ID
     * @return 收藏关系实体
     */
    CollectDocRelationship findByDocIdAndUserId(String docId, String userId);

    /**
     * 统计文档收藏数
     * @param docId 文档ID
     * @return 收藏数量
     */
    long countByDocId(String docId);

    /**
     * 查询文档的所有收藏关系
     * @param docId 文档ID
     * @return 收藏关系列表
     */
    List<CollectDocRelationship> findAllByDocId(String docId);

    /**
     * 查询用户的收藏列表
     * @param userId 用户ID
     * @return 收藏关系列表
     */
    List<CollectDocRelationship> findByUserId(String userId);

    /**
     * 统计收藏总数
     * @return 数量
     */
    long count();
}