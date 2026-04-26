package com.jiaruiblog.infrastructure.repository.mysql;

import com.jiaruiblog.domain.entity.po.DocLog;
import com.jiaruiblog.infrastructure.repository.DocLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * MyBatis DocLog Repository Implementation
 *
 * @author luojiarui
 */
@Repository
public class DocLogMybatisRepository implements DocLogRepository {

    @Autowired
    private DocLogMapper docLogMapper;

    @Override
    public DocLog save(DocLog docLog) {
        return docLogMapper.save(docLog);
    }

    @Override
    public Optional<DocLog> findById(String id) {
        return Optional.ofNullable(docLogMapper.findById(id));
    }

    @Override
    public List<DocLog> findByDocId(String docId) {
        return docLogMapper.findByDocId(docId);
    }

    @Override
    public List<DocLog> findByUserId(String userId) {
        return docLogMapper.findByUserId(userId);
    }

    @Override
    public List<DocLog> findByAction(String action) {
        return docLogMapper.findByAction(action);
    }

    @Override
    public long count() {
        return docLogMapper.count();
    }

    @Override
    public void deleteById(String id) {
        docLogMapper.deleteById(id);
    }

    @Override
    public void deleteAllByIdIn(List<String> ids) {
        docLogMapper.deleteAllByIdIn(ids);
    }
}
