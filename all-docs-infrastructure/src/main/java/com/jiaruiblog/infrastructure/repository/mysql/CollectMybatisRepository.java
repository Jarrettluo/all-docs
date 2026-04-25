package com.jiaruiblog.infrastructure.repository.mysql;

import com.jiaruiblog.domain.entity.po.CollectDocRelationship;
import com.jiaruiblog.infrastructure.repository.CollectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * MyBatis Collect Repository Implementation
 */
@Repository
public class CollectMybatisRepository implements CollectRepository {

    @Autowired
    private CollectMapper collectMapper;

    @Override
    public void save(CollectDocRelationship collect) {
        collectMapper.save(collect);
    }

    @Override
    public void remove(CollectDocRelationship collect) {
        collectMapper.remove(collect);
    }

    @Override
    public Optional<CollectDocRelationship> findByDocIdAndUserId(String docId, String userId) {
        return Optional.ofNullable(collectMapper.findByDocIdAndUserId(docId, userId));
    }

    @Override
    public long countByDocId(String docId) {
        return collectMapper.countByDocId(docId);
    }

    @Override
    public List<CollectDocRelationship> findAllByDocId(String docId) {
        return collectMapper.findAllByDocId(docId);
    }

    @Override
    public List<CollectDocRelationship> findByUserId(String userId) {
        return collectMapper.findByUserId(userId);
    }

    @Override
    public List<CollectDocRelationship> findByDocIdInAndUserId(List<String> docIds, String userId) {
        // This method needs to be implemented in CollectMapper if needed
        return List.of();
    }

    @Override
    public long count() {
        return collectMapper.count();
    }
}
