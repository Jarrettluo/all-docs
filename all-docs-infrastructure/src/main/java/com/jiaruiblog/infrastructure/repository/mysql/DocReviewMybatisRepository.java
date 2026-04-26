package com.jiaruiblog.infrastructure.repository.mysql;

import com.jiaruiblog.domain.entity.po.DocReview;
import com.jiaruiblog.infrastructure.repository.DocReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * MyBatis DocReview Repository Implementation
 *
 * @author luojiarui
 */
@Repository
public class DocReviewMybatisRepository implements DocReviewRepository {

    @Autowired
    private DocReviewMapper docReviewMapper;

    @Override
    public DocReview save(DocReview docReview) {
        docReviewMapper.save(docReview);
        return docReview;
    }

    @Override
    public void saveAll(List<DocReview> docReviews) {
        docReviewMapper.saveAll(docReviews);
    }

    @Override
    public long countByUserId(String userId) {
        return docReviewMapper.countByUserId(userId);
    }

    @Override
    public long countByUserId(String userId, boolean isAdmin) {
        return docReviewMapper.countByUserIdWithAdmin(userId, isAdmin);
    }

    @Override
    public List<DocReview> findByPage(Integer pageNum, Integer pageRows, String userId, boolean isAdmin) {
        int offset = (pageNum != null && pageNum >= 0) ? pageNum : 0;
        int limit = (pageRows != null && pageRows > 0) ? pageRows : 10;
        return docReviewMapper.findByPageWithAdmin(offset, limit, userId, isAdmin);
    }

    @Override
    public long deleteByQuery() {
        return docReviewMapper.deleteByQuery();
    }

    @Override
    public void deleteByIdList(List<String> docIds) {
        docReviewMapper.deleteByIdList(docIds);
    }

    @Override
    public boolean existsByDocIdIn(List<String> docIds) {
        return docReviewMapper.existsByDocIdIn(docIds);
    }
}
