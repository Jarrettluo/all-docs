package com.jiaruiblog.repository.mongodb;

import com.jiaruiblog.entity.CollectDocRelationship;
import com.jiaruiblog.repository.CollectRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public class CollectRepositoryImpl implements CollectRepository {

    private static final String COLLECTION_NAME = "collectCollection";
    private final MongoTemplate mongoTemplate;

    public CollectRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(CollectDocRelationship collect) {
        mongoTemplate.save(collect, COLLECTION_NAME);
    }

    @Override
    public void remove(CollectDocRelationship collect) {
        Query query = new Query(Criteria.where("docId").is(collect.getDocId())
                .and("userId").is(collect.getUserId()));
        mongoTemplate.remove(query, CollectDocRelationship.class, COLLECTION_NAME);
    }

    @Override
    public Optional<CollectDocRelationship> findByDocIdAndUserId(String docId, String userId) {
        Query query = new Query(Criteria.where("docId").is(docId)
                .and("userId").is(userId));
        return Optional.ofNullable(
            mongoTemplate.findOne(query, CollectDocRelationship.class, COLLECTION_NAME)
        );
    }

    @Override
    public long countByDocId(String docId) {
        Query query = new Query(Criteria.where("docId").is(docId));
        return mongoTemplate.count(query, CollectDocRelationship.class, COLLECTION_NAME);
    }

    @Override
    public List<CollectDocRelationship> findAllByDocId(String docId) {
        Query query = new Query(Criteria.where("docId").is(docId));
        return mongoTemplate.find(query, CollectDocRelationship.class, COLLECTION_NAME);
    }
}