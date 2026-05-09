package com.jiaruiblog.infrastructure.repository;

import com.jiaruiblog.domain.entity.po.DocLog;

import java.util.List;
import java.util.Optional;

public interface DocLogRepository {

    int save(DocLog docLog);

    Optional<DocLog> findById(String id);

    List<DocLog> findByDocId(String docId);

    List<DocLog> findByUserId(String userId);

    List<DocLog> findAll();

    List<DocLog> findByAction(String action);

    long count();

    void deleteById(String id);

    void deleteAllByIdIn(List<String> ids);
}