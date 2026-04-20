package com.jiaruiblog.infrastructure.repository;

import com.jiaruiblog.domain.entity.FileDocument;
import com.jiaruiblog.domain.entity.vo.MonthStatVO;
import org.springframework.data.domain.Sort;

import java.util.Date;
import java.util.List;

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

    FileDocument findById(String fileDocumentId);

    List<FileDocument> findByIdList(List<String> docIdList);

    FileDocument findByMd5(String md5);

    List<FileDocument> findByPage(Integer pageNum, Integer pageSize, Sort sort);

    List<FileDocument> findByPageWithFussySearch(Integer pageNum, Integer pageSize, Sort sort, String keyWord);

    boolean delete(String fileDocumentId);

    boolean deleteByIdList(List<String> idList);

    List<MonthStatVO> stats(Date startDate, Date endDate);
}