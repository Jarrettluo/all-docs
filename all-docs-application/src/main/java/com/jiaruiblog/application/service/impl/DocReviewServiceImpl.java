package com.jiaruiblog.application.service.impl;

import com.jiaruiblog.application.service.DocReviewService;
import com.jiaruiblog.domain.entity.FileDocument;
import com.jiaruiblog.domain.entity.DocReview;
import com.jiaruiblog.domain.entity.dto.BasePageDTO;
import com.jiaruiblog.domain.entity.vo.PageVO;
import com.jiaruiblog.infrastructure.repository.DocReviewRepository;
import com.mongodb.client.result.UpdateResult;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author luojiarui
 **/
@Service
public class DocReviewServiceImpl implements DocReviewService {

    @Resource
    private DocReviewRepository docReviewRepository;

    @Override
    public void insert(FileDocument document) {
    }

    @Override
    public void update(FileDocument document) {
    }

    @Override
    public void remove(FileDocument document) {
    }

    @Override
    public void search(FileDocument document) {
    }

    @Override
    public UpdateResult userRead(List<String> ids, String userId) {
        return null;
    }

    @Override
    public void refuse(FileDocument fileFileDocument, String reason) {
    }

    @Override
    public void refuseBatch(List<FileDocument> fileFileDocumentList, String reason) {
    }

    @Override
    public void approveBatch(List<FileDocument> fileFileDocumentList) {
    }

    @Override
    public boolean docIdExist(List<String> docIds) {
        return false;
    }

    @Override
    public void deleteReviewsBatch(List<String> docIds, String userId) {
    }

    @Override
    public PageVO<DocReview> queryReviewLog(BasePageDTO page, String userId, Boolean isAdmin) {
        return PageVO.<DocReview>builder().build();
    }

    @Override
    public void removeReviews(List<String> docIds) {
    }
}