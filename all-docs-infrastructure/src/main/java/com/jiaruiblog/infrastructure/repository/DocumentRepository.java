package com.jiaruiblog.infrastructure.repository;

import com.jiaruiblog.domain.entity.po.FileDocument;
import com.jiaruiblog.domain.entity.vo.MonthStatVO;
import org.springframework.data.domain.Sort;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p></p>
 * edit at 2025/7/4 20:56
 *
 * @author Jarrett Luo
 * @version 1.0
 */
public interface DocumentRepository {

    void save(FileDocument fileDocument);

    void update(FileDocument fileDocument);

    long count();

    long countWithFilter(String filterWord);

    FileDocument findById(String fileDocumentId);

    List<FileDocument> findByIdList(List<String> docIdList);

    FileDocument findByMd5(String md5);

    List<FileDocument> findByPage(Integer pageNum, Integer pageSize, Sort sort);

    List<FileDocument> findByPageWithFussySearch(Integer pageNum, Integer pageSize, Sort sort, String keyWord);

    List<FileDocument> findByPageWithFilter(Integer pageNum, Integer pageSize, Sort sort, String filterWord);

    List<FileDocument> findByUserId(String userId, Integer pageNum, Integer pageSize, Sort sort);

    List<FileDocument> findByUserIdAndNameContaining(String userId, String name, Integer pageNum, Integer pageSize, Sort sort);

    boolean delete(String fileDocumentId);

    boolean deleteByIdList(List<String> idList);

    List<MonthStatVO> stats(Date startDate, Date endDate);

    List<MonthStatVO> trend(Date startDate, Date endDate);

    List<FileDocument> findByPageByTag(String tagId, int pageNum, int pageSize);

    List<FileDocument> findByPageByCategory(String categoryId, int pageNum, int pageSize);

    long countByTagId(String tagId);

    long countByCategoryId(String categoryId);

    List<Map<String, Object>> countByDocType();

    List<Map<String, Object>> countByCategory();
}