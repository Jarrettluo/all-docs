package com.jiaruiblog.repository;

import com.jiaruiblog.entity.DocReview;
import com.mongodb.client.result.UpdateResult;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;

public interface DocReviewRepository {

    DocReview save(DocReview docReview);

    void saveAll(List<DocReview> docReviews);

    long countByQuery(Query query);

    List<DocReview> findByQuery(Query query);

    UpdateResult updateMulti(Query query, Update update);

    long deleteByQuery(Query query);

    boolean existsByDocIdIn(List<String> docIds);
}