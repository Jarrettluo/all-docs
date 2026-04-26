package com.jiaruiblog.application.service.impl;

import com.jiaruiblog.application.service.DocReviewService;
import com.jiaruiblog.application.service.TaskExecuteService;
import com.jiaruiblog.common.enums.DocStateEnum;
import com.jiaruiblog.domain.entity.po.DocReview;
import com.jiaruiblog.domain.entity.po.FileDocument;
import com.jiaruiblog.domain.entity.dto.BasePageDTO;
import com.jiaruiblog.domain.entity.vo.PageVO;
import com.jiaruiblog.infrastructure.repository.DocReviewRepository;
import com.jiaruiblog.infrastructure.repository.DocumentRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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

    @Resource
    private TaskExecuteService taskExecuteService;

    @Override
    public void insert(FileDocument document) {
        if (document == null || document.getId() == null) {
            return;
        }
        DocReview review = new DocReview();
        review.setId(UUID.randomUUID().toString());
        review.setDocId(document.getId());
        review.setDocName(document.getName());
        review.setUserId(document.getUserId());
        review.setUserName(document.getUserName());
        review.setCreateDate(new Date());
        docReviewRepository.save(review);
        log.info("文档审核记录创建：docId={}", document.getId());
    }

    @Override
    public void update(FileDocument document) {
        if (document == null || document.getId() == null) {
            return;
        }
        // Update document state directly via repository
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
        // Note: Simplified - would need findByDocId method
        log.debug("查询文档审核记录：docId={}", document.getId());
    }

    @Override
    public void userRead(List<String> ids, String userId) {
        if (ids == null || ids.isEmpty() || userId == null) {
            return;
        }
        // Note: Simplified - would need update method in repository
        log.info("标记已读：userId={}, docIds={}", userId, ids);
    }

    @Override
    @Transactional
    public void refuse(FileDocument fileDocument, String reason) {
        if (fileDocument == null || fileDocument.getId() == null) {
            return;
        }
        // Update document state
        fileDocument.setReviewing(false);
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
    @Transactional
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
        // Update document state
        doc.setReviewing(false);
        doc.setDocState(DocStateEnum.WAIT);
        documentRepository.update(doc);
        taskExecuteService.execute(doc);
        log.info("文档审核通过，已触发解析任务：docId={}", doc.getId());
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