package com.jiaruiblog.domain.repository;

import com.jiaruiblog.domain.entity.DocReview;

import java.util.List;

/**
 * @ClassName DocReviewRepository
 * @Description 文档审核仓储接口
 * @author luojiarui
 * @Date 2022/11/25 15:39
 * @Version 1.0
 **/
public interface DocReviewRepository {

    /**
     * 保存审核记录
     * @param docReview 审核实体
     * @return 保存后的审核记录
     */
    DocReview save(DocReview docReview);

    /**
     * 批量保存审核记录
     * @param docReviews 审核实体列表
     */
    void saveAll(List<DocReview> docReviews);

    /**
     * 根据用户ID统计审核数量
     * @param userId 用户ID
     * @return 审核数量
     */
    long countByUserId(String userId);

    /**
     * 分页查询审核记录
     * @param offset 偏移量
     * @param limit 每页数量
     * @param userId 用户ID
     * @return 审核列表
     */
    List<DocReview> findByPage(int offset, int limit, String userId);

    /**
     * 根据ID列表删除审核记录
     * @param idList ID列表
     */
    void deleteByIdList(List<String> idList);

    /**
     * 检查文档是否存在于审核列表中
     * @param docIdList 文档ID列表
     * @return 是否存在
     */
    boolean existsByDocIdIn(List<String> docIdList);
}