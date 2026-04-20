package com.jiaruiblog.infrastructure.repository;

import com.jiaruiblog.domain.entity.DocReview;
import com.mongodb.client.result.UpdateResult;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;

public interface DocReviewRepository {

    DocReview save(DocReview docReview);

    void saveAll(List<DocReview> docReviews);

    long countByUserId(String userId);

    long countByUserId(String userId, boolean isAdmin);

    List<DocReview> findByPage(Integer pageNum, Integer pageRows, String userId, boolean isAdmin);

    long countByQuery(Query query);

    List<DocReview> findByQuery(Query query);

    UpdateResult updateMulti(Query query, Update update);

    long deleteByQuery(Query query);

    void deleteByIdList(List<String> docIds);

    boolean existsByDocIdIn(List<String> docIds);
}