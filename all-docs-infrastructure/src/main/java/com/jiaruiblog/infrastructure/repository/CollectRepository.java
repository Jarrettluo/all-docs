package com.jiaruiblog.infrastructure.repository;

import com.jiaruiblog.domain.entity.CollectDocRelationship;

import java.util.List;
import java.util.Optional;

public interface CollectRepository {
    void save(CollectDocRelationship collect);

    void remove(CollectDocRelationship collect);

    Optional<CollectDocRelationship> findByDocIdAndUserId(String docId, String userId);

    long countByDocId(String docId);

    List<CollectDocRelationship> findAllByDocId(String docId);
}