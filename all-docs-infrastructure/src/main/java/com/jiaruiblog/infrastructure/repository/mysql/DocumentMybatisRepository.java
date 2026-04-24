package com.jiaruiblog.infrastructure.repository.mysql;

import com.jiaruiblog.domain.entity.FileDocument;
import com.jiaruiblog.domain.entity.vo.MonthStatVO;
import com.jiaruiblog.infrastructure.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

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
}