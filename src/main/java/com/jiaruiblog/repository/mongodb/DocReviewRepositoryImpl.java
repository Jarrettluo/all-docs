package com.jiaruiblog.repository.mongodb;

import com.jiaruiblog.entity.DocReview;
import com.jiaruiblog.repository.DocReviewRepository;
import com.mongodb.client.result.UpdateResult;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DocReviewRepositoryImpl implements DocReviewRepository {

    private static final String COLLECTION_NAME = "docReview";

    private final MongoTemplate mongoTemplate;

    public static final String USER_ID = "userId";
    public static final String DOC_ID = "docId";


    public DocReviewRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public DocReview save(DocReview docReview) {
        return mongoTemplate.save(docReview, COLLECTION_NAME);
    }

    @Override
    public void saveAll(List<DocReview> docReviews) {
        mongoTemplate.insert(docReviews, COLLECTION_NAME);
    }

    @Override
    public long countByUserId(String userId) {
        Query query = new Query(Criteria.where(USER_ID).is(userId));
        return mongoTemplate.count(query, DocReview.class, COLLECTION_NAME);
    }

    @Override
    public long countByUserId(String userId, boolean isAdmin) {
        Query query = new Query();
        if (!isAdmin && userId != null) {
            query.addCriteria(Criteria.where(USER_ID).is(userId));
        }
        return mongoTemplate.count(query, DocReview.class, COLLECTION_NAME);
    }

    @Override
    public List<DocReview> findByPage(Integer pageNum, Integer pageRows, String userId, boolean isAdmin) {
        Query query = new Query();
        if (!isAdmin && userId != null) {
            query.addCriteria(Criteria.where(USER_ID).is(userId));
        }
        
        // 设置分页
        Pageable pageable = PageRequest.of(pageNum - 1, pageRows);
        query.with(pageable);
        
        // 按创建时间倒序排列
        query.with(Sort.by(Sort.Direction.DESC, "createDate"));
        
        return mongoTemplate.find(query, DocReview.class, COLLECTION_NAME);
    }

    @Override
    public long countByQuery(Query query) {
        return mongoTemplate.count(query, DocReview.class, COLLECTION_NAME);
    }

    @Override
    public List<DocReview> findByQuery(Query query) {
        return mongoTemplate.find(query, DocReview.class, COLLECTION_NAME);
    }

    @Override
    public UpdateResult updateMulti(Query query, Update update) {
        return mongoTemplate.updateMulti(query, update, DocReview.class, COLLECTION_NAME);
    }

    @Override
    public long deleteByQuery(Query query) {
        return mongoTemplate.remove(query, DocReview.class, COLLECTION_NAME).getDeletedCount();
    }

    @Override
    public void deleteByIdList(List<String> docIds) {
        Query query = new Query(Criteria.where(DOC_ID).in(docIds));
        mongoTemplate.remove(query, DocReview.class, COLLECTION_NAME);
    }

    @Override
    public boolean existsByDocIdIn(List<String> docIds) {
        Query query = new Query(Criteria.where("docId").in(docIds));
        return mongoTemplate.exists(query, DocReview.class, COLLECTION_NAME);
    }
}