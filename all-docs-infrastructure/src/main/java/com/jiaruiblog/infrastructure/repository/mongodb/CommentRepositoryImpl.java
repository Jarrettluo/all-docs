package com.jiaruiblog.infrastructure.repository.mongodb;

import com.jiaruiblog.domain.entity.Comment;
import com.jiaruiblog.infrastructure.repository.CommentRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CommentRepositoryImpl implements CommentRepository {

    private static final String COLLECTION_NAME = "commentCollection";

    private final MongoTemplate mongoTemplate;

    public CommentRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Comment save(Comment comment) {
        return mongoTemplate.save(comment, COLLECTION_NAME);
    }

    @Override
    public Optional<Comment> findById(String id) {
        return Optional.ofNullable(mongoTemplate.findById(id, Comment.class, COLLECTION_NAME));
    }

    @Override
    public List<Comment> findByDocId(String docId) {
        Query query = new Query(Criteria.where("docId").is(docId));
        return mongoTemplate.find(query, Comment.class, COLLECTION_NAME);
    }

    @Override
    public List<Comment> findByUserId(String userId) {
        Query query = new Query(Criteria.where("userId").is(userId));
        return mongoTemplate.find(query, Comment.class, COLLECTION_NAME);
    }

    @Override
    public List<Comment> findByContentContaining(String keyword) {
        Query query = new Query(Criteria.where("content").regex(keyword, "i"));
        return mongoTemplate.find(query, Comment.class, COLLECTION_NAME);
    }

    @Override
    public long countByDocId(String docId) {
        Query query = new Query(Criteria.where("docId").is(docId));
        return mongoTemplate.count(query, Comment.class, COLLECTION_NAME);
    }

    @Override
    public void deleteById(String id) {
        Query query = new Query(Criteria.where("_id").is(id));
        mongoTemplate.remove(query, Comment.class, COLLECTION_NAME);
    }

    @Override
    public void deleteByDocId(String docId) {
        Query query = new Query(Criteria.where("docId").is(docId));
        mongoTemplate.remove(query, Comment.class, COLLECTION_NAME);
    }

    @Override
    public void deleteAllByIdIn(List<String> ids) {
        Query query = new Query(Criteria.where("_id").in(ids));
        mongoTemplate.remove(query, Comment.class, COLLECTION_NAME);
    }

    @Override
    public long count() {
        return mongoTemplate.count(new Query(), Comment.class, COLLECTION_NAME);
    }

    @Override
    public long countByQuery(Query query) {
        return mongoTemplate.count(query, Comment.class, COLLECTION_NAME);
    }

    @Override
    public List<Comment> findByQuery(Query query) {
        return mongoTemplate.find(query, Comment.class, COLLECTION_NAME);
    }
}