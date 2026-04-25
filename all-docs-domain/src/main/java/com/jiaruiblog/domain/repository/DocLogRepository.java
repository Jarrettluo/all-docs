package com.jiaruiblog.domain.repository;

import com.jiaruiblog.domain.entity.DocLog;

import java.util.List;

/**
 * @ClassName DocLogRepository
 * @Description 文档日志仓储接口
 * @author luojiarui
 * @Date 2022/12/10 10:58
 * @Version 1.0
 **/
public interface DocLogRepository {

    /**
     * 保存日志
     * @param docLog 日志实体
     * @return 保存后的日志
     */
    DocLog save(DocLog docLog);

    /**
     * 根据ID查询日志
     * @param id 日志ID
     * @return 日志实体
     */
    DocLog findById(String id);

    /**
     * 根据文档ID查询日志
     * @param docId 文档ID
     * @return 日志列表
     */
    List<DocLog> findByDocId(String docId);

    /**
     * 根据用户ID查询日志
     * @param userId 用户ID
     * @return 日志列表
     */
    List<DocLog> findByUserId(String userId);

    /**
     * 根据操作类型查询日志
     * @param action 操作类型
     * @return 日志列表
     */
    List<DocLog> findByAction(String action);

    /**
     * 统计日志总数
     * @return 数量
     */
    long count();

    /**
     * 根据ID删除日志
     * @param id 日志ID
     */
    void deleteById(String id);

    /**
     * 批量删除日志
     * @param idList ID列表
     */
    void deleteAllByIdIn(List<String> idList);
}