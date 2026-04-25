package com.jiaruiblog.domain.repository;

import com.jiaruiblog.domain.entity.Comment;

import java.util.List;

/**
 * @ClassName CommentRepository
 * @Description 评论仓储接口
 * @author luojiarui
 * @Date 2022/6/4 10:31 上午
 * @Version 1.0
 **/
public interface CommentRepository {

    /**
     * 保存评论
     * @param comment 评论实体
     * @return 保存后的评论
     */
    Comment save(Comment comment);

    /**
     * 根据ID查询评论
     * @param id 评论ID
     * @return 评论实体
     */
    Comment findById(String id);

    /**
     * 根据文档ID查询评论
     * @param docId 文档ID
     * @return 评论列表
     */
    List<Comment> findByDocId(String docId);

    /**
     * 根据用户ID查询评论
     * @param userId 用户ID
     * @return 评论列表
     */
    List<Comment> findByUserId(String userId);

    /**
     * 统计文档评论数
     * @param docId 文档ID
     * @return 评论数量
     */
    long countByDocId(String docId);

    /**
     * 删除评论
     * @param id 评论ID
     */
    void deleteById(String id);

    /**
     * 删除文档的所有评论
     * @param docId 文档ID
     */
    void deleteByDocId(String docId);
}