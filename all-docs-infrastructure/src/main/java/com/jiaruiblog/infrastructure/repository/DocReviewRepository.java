package com.jiaruiblog.infrastructure.repository;

import com.jiaruiblog.domain.entity.DocReview;

import java.util.List;

public interface DocReviewRepository {

    DocReview save(DocReview docReview);

    void saveAll(List<DocReview> docReviews);

    long countByUserId(String userId);

    long countByUserId(String userId, boolean isAdmin);

    List<DocReview> findByPage(Integer pageNum, Integer pageRows, String userId, boolean isAdmin);

    long deleteByQuery();

    void deleteByIdList(List<String> docIds);

    boolean existsByDocIdIn(List<String> docIds);
}