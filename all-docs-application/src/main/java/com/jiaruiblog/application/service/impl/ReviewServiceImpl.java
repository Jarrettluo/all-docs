package com.jiaruiblog.application.service.impl;

import com.jiaruiblog.application.service.ReviewService;
import com.jiaruiblog.domain.entity.DocReview;
import com.jiaruiblog.infrastructure.repository.DocReviewRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author luojiarui
 **/
@Slf4j
@Service
public class ReviewServiceImpl implements ReviewService {

    @Resource
    private DocReviewRepository reviewRepository;

    /**
     * Save a review record
     * @param review the review to save
     */
    public void saveReview(DocReview review) {
        if (review == null) {
            log.warn("Attempted to save null review");
            return;
        }
        review.setCreateDate(new Date());
        reviewRepository.save(review);
        log.info("Review saved: docId={}, userId={}", review.getDocId(), review.getUserId());
    }

    /**
     * Save multiple review records
     * @param reviews list of reviews to save
     */
    public void saveReviews(List<DocReview> reviews) {
        if (reviews == null || reviews.isEmpty()) {
            return;
        }
        for (DocReview review : reviews) {
            if (review.getCreateDate() == null) {
                review.setCreateDate(new Date());
            }
        }
        reviewRepository.saveAll(reviews);
        log.info("Saved {} reviews", reviews.size());
    }

    /**
     * Find reviews by document ID
     * @param docId the document ID
     * @return list of reviews for the document
     */
    public List<DocReview> findByDocId(String docId) {
        if (docId == null || docId.isEmpty()) {
            return List.of();
        }
        Query query = new Query(Criteria.where("docId").is(docId));
        return reviewRepository.findByQuery(query);
    }

    /**
     * Find reviews by user ID with pagination
     * @param userId the user ID
     * @param pageNum page number
     * @param pageRows number of rows per page
     * @return list of reviews
     */
    public List<DocReview> findByUserId(String userId, Integer pageNum, Integer pageRows) {
        if (userId == null || userId.isEmpty()) {
            return List.of();
        }
        int page = pageNum != null ? pageNum : 1;
        int rows = pageRows != null ? pageRows : 10;
        return reviewRepository.findByPage(page - 1, rows, userId, false);
    }

    /**
     * Count reviews by user ID
     * @param userId the user ID
     * @return count of reviews
     */
    public long countByUserId(String userId) {
        if (userId == null || userId.isEmpty()) {
            return 0;
        }
        return reviewRepository.countByUserId(userId);
    }

    /**
     * Delete reviews by document IDs
     * @param docIds list of document IDs
     */
    public void deleteByDocIds(List<String> docIds) {
        if (docIds == null || docIds.isEmpty()) {
            return;
        }
        reviewRepository.deleteByIdList(docIds);
        log.info("Deleted reviews for docIds: {}", docIds);
    }

    /**
     * Check if reviews exist for given document IDs
     * @param docIds list of document IDs
     * @return true if any reviews exist
     */
    public boolean existsByDocIds(List<String> docIds) {
        if (docIds == null || docIds.isEmpty()) {
            return false;
        }
        return reviewRepository.existsByDocIdIn(docIds);
    }

    /**
     * Mark reviews as read for a user
     * @param docIds document IDs to mark as read
     * @param userId user ID
     */
    public void markAsRead(List<String> docIds, String userId) {
        if (docIds == null || docIds.isEmpty()) {
            return;
        }
        Query query = new Query(Criteria.where("docId").in(docIds));
        Update update = new Update()
                .set("readState", true)
                .set("updateDate", new Date());
        reviewRepository.updateMulti(query, update);
        log.info("Marked reviews as read: docIds={}, userId={}", docIds, userId);
    }

    /**
     * Update review check state
     * @param docId document ID
     * @param checkState new check state
     */
    public void updateCheckState(String docId, boolean checkState) {
        if (docId == null || docId.isEmpty()) {
            return;
        }
        Query query = new Query(Criteria.where("docId").is(docId));
        Update update = new Update()
                .set("checkState", checkState)
                .set("updateDate", new Date());
        reviewRepository.updateMulti(query, update);
        log.info("Updated check state: docId={}, checkState={}", docId, checkState);
    }

    /**
     * Delete all reviews for admin
     * @param docIds document IDs
     */
    public void adminDelete(List<String> docIds) {
        if (docIds == null || docIds.isEmpty()) {
            return;
        }
        Query query = new Query(Criteria.where("docId").in(docIds));
        reviewRepository.deleteByQuery(query);
        log.info("Admin deleted reviews for docIds: {}", docIds);
    }
}