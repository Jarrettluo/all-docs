package com.jiaruiblog.application.service.impl;

import com.jiaruiblog.application.service.DocReviewService;
import com.jiaruiblog.common.enums.DocStateEnum;
import com.jiaruiblog.domain.entity.DocReview;
import com.jiaruiblog.domain.entity.FileDocument;
import com.jiaruiblog.domain.entity.dto.BasePageDTO;
import com.jiaruiblog.domain.entity.vo.PageVO;
import com.jiaruiblog.infrastructure.repository.DocReviewRepository;
import com.jiaruiblog.infrastructure.repository.DocumentRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author luojiarui
 **/
@Slf4j
@Service
public class DocReviewServiceImpl implements DocReviewService {

    @Resource
    private DocReviewRepository docReviewRepository;

    @Resource
    private DocumentRepository documentRepository;

    @Override
    public void insert(FileDocument document) {
        if (document == null || document.getId() == null) {
            return;
        }
        DocReview review = new DocReview();
        review.setDocId(document.getId());
        review.setUserId(document.getUserId());
        review.setCreateDate(new Date());
        review.setState(DocStateEnum.WAITE.getCode());
        docReviewRepository.save(review);
        log.info("文档审核记录创建：docId={}", document.getId());
    }

    @Override
    public void update(FileDocument document) {
        if (document == null || document.getId() == null) {
            return;
        }
        Query query = new Query(Criteria.where("docId").is(document.getId()));
        Update update = new Update()
                .set("state", document.getDocState())
                .set("updateDate", new Date());
        docReviewRepository.updateMulti(query, update);

        // 更新文档状态
        documentRepository.update(document);
        log.info("文档审核记录更新：docId={}, state={}", document.getId(), document.getDocState());
    }

    @Override
    public void remove(FileDocument document) {
        if (document == null || document.getId() == null) {
            return;
        }
        List<String> docIds = new ArrayList<>();
        docIds.add(document.getId());
        docReviewRepository.deleteByIdList(docIds);
        log.info("文档审核记录删除：docId={}", document.getId());
    }

    @Override
    public void search(FileDocument document) {
        if (document == null || document.getId() == null) {
            return;
        }
        Query query = new Query(Criteria.where("docId").is(document.getId()));
        List<DocReview> reviews = docReviewRepository.findByQuery(query);
        log.debug("查询文档审核记录：docId={}, count={}", document.getId(), reviews != null ? reviews.size() : 0);
    }

    @Override
    public com.mongodb.client.result.UpdateResult userRead(List<String> ids, String userId) {
        if (ids == null || ids.isEmpty() || userId == null) {
            return null;
        }
        try {
            Query query = new Query(Criteria.where("docId").in(ids));
            Update update = new Update().set("read", true).set("readDate", new Date());
            return docReviewRepository.updateMulti(query, update);
        } catch (Exception e) {
            log.error("标记已读失败：userId={}, docIds={}", userId, ids, e);
            return null;
        }
    }

    @Override
    public void refuse(FileDocument fileDocument, String reason) {
        if (fileDocument == null || fileDocument.getId() == null) {
            return;
        }
        Query query = new Query(Criteria.where("docId").is(fileDocument.getId()));
        Update update = new Update()
                .set("state", DocStateEnum.FAIL.getCode())
                .set("updateDate", new Date())
                .set("errorMsg", reason);
        docReviewRepository.updateMulti(query, update);

        // 更新文档状态
        fileDocument.setDocState(DocStateEnum.FAIL);
        fileDocument.setErrorMsg(reason);
        documentRepository.update(fileDocument);
        log.info("文档审核拒绝：docId={}, reason={}", fileDocument.getId(), reason);
    }

    @Override
    public void refuseBatch(List<FileDocument> fileFileDocumentList, String reason) {
        if (fileFileDocumentList == null || fileFileDocumentList.isEmpty()) {
            return;
        }
        for (FileDocument doc : fileFileDocumentList) {
            refuse(doc, reason);
        }
        log.info("批量拒绝文档审核：count={}, reason={}", fileFileDocumentList.size(), reason);
    }

    @Override
    public void approveBatch(List<FileDocument> fileFileDocumentList) {
        if (fileFileDocumentList == null || fileFileDocumentList.isEmpty()) {
            return;
        }
        for (FileDocument doc : fileFileDocumentList) {
            approve(doc);
        }
        log.info("批量通过文档审核：count={}", fileFileDocumentList.size());
    }

    private void approve(FileDocument doc) {
        if (doc == null || doc.getId() == null) {
            return;
        }
        Query query = new Query(Criteria.where("docId").is(doc.getId()));
        Update update = new Update()
                .set("state", DocStateEnum.SUCCESS.getCode())
                .set("updateDate", new Date());
        docReviewRepository.updateMulti(query, update);

        // 更新文档状态
        doc.setDocState(DocStateEnum.SUCCESS);
        documentRepository.update(doc);
        log.info("文档审核通过：docId={}", doc.getId());
    }

    @Override
    public boolean docIdExist(List<String> docIds) {
        if (docIds == null || docIds.isEmpty()) {
            return false;
        }
        return docReviewRepository.existsByDocIdIn(docIds);
    }

    @Override
    public void deleteReviewsBatch(List<String> docIds, String userId) {
        if (docIds == null || docIds.isEmpty()) {
            return;
        }
        docReviewRepository.deleteByIdList(docIds);
        log.info("批量删除审核记录：docIds={}", docIds);
    }

    @Override
    public PageVO<DocReview> queryReviewLog(BasePageDTO page, String userId, Boolean isAdmin) {
        PageVO<DocReview> pageVO = new PageVO<>();
        int pageNum = 1;
        int pageSize = 10;
        if (page != null) {
            if (page.getPage() != null) {
                pageNum = page.getPage();
            }
            if (page.getRows() != null) {
                pageSize = page.getRows();
            }
        }

        List<DocReview> reviews = docReviewRepository.findByPage(pageNum - 1, pageSize, userId, isAdmin != null && isAdmin);

        // 过滤掉不属于该用户的记录
        List<DocReview> filteredList = new ArrayList<>();
        if (reviews != null) {
            for (DocReview review : reviews) {
                if (isAdmin != null && isAdmin) {
                    filteredList.add(review);
                } else if (userId == null || userId.equals(review.getUserId())) {
                    filteredList.add(review);
                }
            }
        }

        pageVO.setList(filteredList);
        pageVO.setPageNum(pageNum);
        pageVO.setPageSize(pageSize);
        pageVO.setTotal(docReviewRepository.countByUserId(userId, isAdmin != null && isAdmin));
        return pageVO;
    }

    @Override
    public void removeReviews(List<String> docIds) {
        if (docIds == null || docIds.isEmpty()) {
            return;
        }
        docReviewRepository.deleteByIdList(docIds);
        log.info("删除审核记录：docIds={}", docIds);
    }
}