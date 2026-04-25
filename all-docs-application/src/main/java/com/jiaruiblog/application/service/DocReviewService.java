package com.jiaruiblog.application.service;

import com.jiaruiblog.domain.entity.po.DocReview;
import com.jiaruiblog.domain.entity.po.FileDocument;
import com.jiaruiblog.domain.entity.dto.BasePageDTO;
import com.jiaruiblog.domain.entity.vo.PageVO;

import java.util.List;

/**
 * @author Jarrett Luo
 * @Date 2022/6/7 11:39
 * @Version 1.0
 */
public interface DocReviewService {

    void insert(FileDocument document);

    void update(FileDocument document);

    void remove(FileDocument document);

    void search(FileDocument document);

    void userRead(List<String> ids, String userId);

    void refuse(FileDocument fileDocument, String reason);

    void refuseBatch(List<FileDocument> fileDocumentList, String reason);

    void approveBatch(List<FileDocument> fileDocumentList);

    boolean docIdExist(List<String> docIds);

    void deleteReviewsBatch(List<String> docIds, String userId);

    PageVO<DocReview> queryReviewLog(BasePageDTO page, String userId, Boolean isAdmin);

    void removeReviews(List<String> docIds);
}