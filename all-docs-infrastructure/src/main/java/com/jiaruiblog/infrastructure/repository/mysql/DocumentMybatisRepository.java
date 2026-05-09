package com.jiaruiblog.infrastructure.repository.mysql;

import com.jiaruiblog.domain.entity.po.FileDocument;
import com.jiaruiblog.domain.entity.vo.MonthStatVO;
import com.jiaruiblog.infrastructure.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * MyBatis Document Repository Implementation
 *
 * @author Jarrett Luo
 * @version 1.0
 */
@Repository
public class DocumentMybatisRepository implements DocumentRepository {

    @Autowired
    private DocumentMapper documentMapper;

    @Override
    public void save(FileDocument fileDocument) {
        documentMapper.save(fileDocument);
    }

    @Override
    public void update(FileDocument fileDocument) {
        documentMapper.update(fileDocument);
    }

    @Override
    public long count() {
        return documentMapper.count();
    }

    @Override
    public long countWithFilter(String filterWord) {
        return documentMapper.countWithFilter(filterWord);
    }

    @Override
    public FileDocument findById(String fileDocumentId) {
        return documentMapper.findById(fileDocumentId);
    }

    @Override
    public List<FileDocument> findByIdList(List<String> docIdList) {
        return documentMapper.findByIdList(docIdList);
    }

    @Override
    public FileDocument findByMd5(String md5) {
        return documentMapper.findByMd5(md5);
    }

    @Override
    public List<FileDocument> findByPage(Integer pageNum, Integer pageSize, Sort sort) {
        long offset = (long) pageNum * pageSize;
        return documentMapper.findByPage(offset, pageSize);
    }

    @Override
    public List<FileDocument> findByPageWithFussySearch(Integer pageNum, Integer pageSize, Sort sort, String keyWord) {
        long offset = (long) pageNum * pageSize;
        return documentMapper.findByPageWithFussySearch(offset, pageSize, keyWord);
    }

    @Override
    public List<FileDocument> findByPageWithFilter(Integer pageNum, Integer pageSize, Sort sort, String filterWord) {
        long offset = (long) pageNum * pageSize;
        return documentMapper.findByPageWithFilter(offset, pageSize, filterWord);
    }

    @Override
    public List<FileDocument> findByUserId(String userId, Integer pageNum, Integer pageSize, Sort sort) {
        long offset = (long) pageNum * pageSize;
        return documentMapper.findByUserId(userId, offset, pageSize);
    }

    @Override
    public List<FileDocument> findByUserIdAndNameContaining(String userId, String name, Integer pageNum, Integer pageSize, Sort sort) {
        long offset = (long) pageNum * pageSize;
        return documentMapper.findByUserIdAndNameContaining(userId, name, offset, pageSize);
    }

    @Override
    public boolean delete(String fileDocumentId) {
        return documentMapper.delete(fileDocumentId);
    }

    @Override
    public boolean deleteByIdList(List<String> idList) {
        return documentMapper.deleteByIdList(idList);
    }

    @Override
    public List<MonthStatVO> stats(Date startDate, Date endDate) {
        return documentMapper.stats(startDate, endDate);
    }

    @Override
    public List<MonthStatVO> trend(Date startDate, Date endDate) {
        return documentMapper.trend(startDate, endDate);
    }

    @Override
    public List<FileDocument> findByPageByTag(String tagId, int pageNum, int pageSize) {
        if (pageNum < 1) {
            pageNum = 1;
        }
        long offset = (long) (pageNum - 1) * pageSize;
        return documentMapper.findByPageByTag(tagId, offset, pageSize);
    }

    @Override
    public List<FileDocument> findByPageByCategory(String categoryId, int pageNum, int pageSize) {
        if (pageNum < 1) {
            pageNum = 1;
        }
        long offset = (long) (pageNum - 1) * pageSize;
        return documentMapper.findByPageByCategory(categoryId, offset, pageSize);
    }

    @Override
    public long countByTagId(String tagId) {
        return documentMapper.countByTagId(tagId);
    }

    @Override
    public long countByCategoryId(String categoryId) {
        return documentMapper.countByCategoryId(categoryId);
    }

    @Override
    public List<Map<String, Object>> countByDocType() {
        return documentMapper.countByDocType();
    }

    @Override
    public List<Map<String, Object>> countByCategory() {
        return documentMapper.countByCategory();
    }
}