package com.jiaruiblog.repository;

import com.jiaruiblog.entity.DocLog;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;
import java.util.Optional;

public interface DocLogRepository {

    DocLog save(DocLog docLog);

    Optional<DocLog> findById(String id);

    List<DocLog> findByDocId(String docId);

    List<DocLog> findByUserId(String userId);

    List<DocLog> findByAction(String action);

    long count();

    void deleteById(String id);

    void deleteAllByIdIn(List<String> ids);

    List<DocLog> findByQuery(Query query);

    long countByQuery(Query query);
}