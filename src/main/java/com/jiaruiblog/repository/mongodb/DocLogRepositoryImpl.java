package com.jiaruiblog.repository.mongodb;

import com.jiaruiblog.entity.DocLog;
import com.jiaruiblog.repository.DocLogRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class DocLogRepositoryImpl implements DocLogRepository {

    private static final String COLLECTION_NAME = "docLog";

    private final MongoTemplate mongoTemplate;

    public DocLogRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public DocLog save(DocLog docLog) {
        return mongoTemplate.save(docLog, COLLECTION_NAME);
    }

    @Override
    public Optional<DocLog> findById(String id) {
        return Optional.ofNullable(mongoTemplate.findById(id, DocLog.class, COLLECTION_NAME));
    }

    @Override
    public List<DocLog> findByDocId(String docId) {
        Query query = new Query(Criteria.where("docId").is(docId));
        return mongoTemplate.find(query, DocLog.class, COLLECTION_NAME);
    }

    @Override
    public List<DocLog> findByUserId(String userId) {
        Query query = new Query(Criteria.where("userId").is(userId));
        return mongoTemplate.find(query, DocLog.class, COLLECTION_NAME);
    }

    @Override
    public List<DocLog> findByAction(String action) {
        Query query = new Query(Criteria.where("action").is(action));
        return mongoTemplate.find(query, DocLog.class, COLLECTION_NAME);
    }

    @Override
    public long count() {
        return mongoTemplate.getCollection(COLLECTION_NAME).estimatedDocumentCount();
    }

    @Override
    public void deleteById(String id) {
        Query query = new Query(Criteria.where("_id").is(id));
        mongoTemplate.remove(query, DocLog.class, COLLECTION_NAME);
    }

    @Override
    public void deleteAllByIdIn(List<String> ids) {
        Query query = new Query(Criteria.where("_id").in(ids));
        mongoTemplate.remove(query, DocLog.class, COLLECTION_NAME);
    }

    @Override
    public List<DocLog> findByQuery(Query query) {
        return mongoTemplate.find(query, DocLog.class, COLLECTION_NAME);
    }

    @Override
    public long countByQuery(Query query) {
        return mongoTemplate.count(query, DocLog.class, COLLECTION_NAME);
    }
}