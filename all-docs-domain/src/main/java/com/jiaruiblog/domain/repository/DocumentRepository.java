package com.jiaruiblog.domain.repository;

import com.jiaruiblog.domain.entity.FileDocument;
import com.jiaruiblog.domain.entity.vo.MonthStatVO;

import java.util.Date;
import java.util.List;

/**
 * @ClassName DocumentRepository
 * @Description 文档仓储接口
 * @author luojiarui
 * @Date 2022/6/4 10:28 上午
 * @Version 1.0
 **/
public interface DocumentRepository {

    /**
     * 保存文档
     * @param fileDocument 文档实体
     */
    void save(FileDocument fileDocument);

    /**
     * 更新文档
     * @param fileDocument 文档实体
     */
    void update(FileDocument fileDocument);

    /**
     * 根据ID查询文档
     * @param id 文档ID
     * @return 文档实体
     */
    FileDocument findById(String id);

    /**
     * 根据ID列表查询文档
     * @param idList ID列表
     * @return 文档列表
     */
    List<FileDocument> findByIdList(List<String> idList);

    /**
     * 根据MD5查询文档
     * @param md5 MD5值
     * @return 文档实体
     */
    FileDocument findByMd5(String md5);

    /**
     * 分页查询文档
     * @param offset 偏移量
     * @param limit 每页数量
     * @return 文档列表
     */
    List<FileDocument> findByPage(long offset, int limit);

    /**
     * 模糊搜索分页查询
     * @param offset 偏移量
     * @param limit 每页数量
     * @param keyWord 关键字
     * @return 文档列表
     */
    List<FileDocument> findByPageWithFussySearch(long offset, int limit, String keyWord);

    /**
     * 根据用户ID分页查询
     * @param userId 用户ID
     * @param offset 偏移量
     * @param limit 每页数量
     * @return 文档列表
     */
    List<FileDocument> findByUserId(String userId, long offset, int limit);

    /**
     * 根据用户ID和名称模糊查询
     * @param userId 用户ID
     * @param name 文档名称
     * @param offset 偏移量
     * @param limit 每页数量
     * @return 文档列表
     */
    List<FileDocument> findByUserIdAndNameContaining(String userId, String name, long offset, int limit);

    /**
     * 删除文档
     * @param id 文档ID
     * @return 是否成功
     */
    boolean delete(String id);

    /**
     * 批量删除文档
     * @param idList ID列表
     * @return 是否成功
     */
    boolean deleteByIdList(List<String> idList);

    /**
     * 统计文档数量
     * @return 文档数量
     */
    long count();

    /**
     * 按日期范围统计
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 月度统计列表
     */
    List<MonthStatVO> stats(Date startDate, Date endDate);

    /**
     * 按日期范围查询趋势
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 月度统计列表
     */
    List<MonthStatVO> trend(Date startDate, Date endDate);
}