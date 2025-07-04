package com.jiaruiblog.service.impl;

import com.jiaruiblog.entity.DocReview;
import com.jiaruiblog.entity.FileDocument;
import com.jiaruiblog.entity.dto.BasePageDTO;
import com.jiaruiblog.entity.vo.PageVO;
import com.jiaruiblog.repository.DocReviewRepository;
import com.jiaruiblog.service.DocReviewService;
import com.jiaruiblog.service.TaskExecuteService;
import com.mongodb.client.result.UpdateResult;
import jakarta.annotation.Resource;
import org.apache.commons.compress.utils.Lists;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author luojiarui
 **/
@Service
public class DocReviewServiceImpl implements DocReviewService {

    @Resource
    private TaskExecuteService taskExecuteService;


    @Resource
    private DocReviewRepository docReviewRepository;


    @Override
    public UpdateResult userRead(List<String> ids, String userId) {
        Query query = new Query(Criteria.where("_id").in(ids));
        Update update = new Update();
        update.set("readState", true);
        update.set("updateDate", new Date());
        return docReviewRepository.updateMulti(query, update);
    }

    @Override
    public void refuse(FileDocument fileDocument, String reason) {
        DocReview docReview = docReviewInstance(fileDocument, reason, false);
        if (docReview != null) {
            docReviewRepository.save(docReview);
        }
    }

    /**
     * @return com.jiaruiblog.entity.DocReview
     * @author luojiarui
     **/
    private DocReview docReviewInstance(FileDocument fileDocument, String reason, boolean approve) {
        if (!StringUtils.hasText(fileDocument.getId())) {
            return null;
        }
        DocReview docReview = new DocReview();
        docReview.setDocId(fileDocument.getId());
        docReview.setDocName(fileDocument.getName());
        docReview.setUserId(fileDocument.getUserId());
        docReview.setUserName(fileDocument.getUserName());
        docReview.setCheckState(approve);
        docReview.setReadState(false);
        docReview.setUserRemove(false);
        docReview.setReviewLog(reason);
        docReview.setCreateDate(new Date());
        docReview.setUpdateDate(new Date());
        return docReview;
    }


    @Override
    public void refuseBatch(List<FileDocument> fileDocumentList, String reason) {
        List<DocReview> docReviews = fileDocumentList.stream()
                .map(file -> docReviewInstance(file, reason, false))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        docReviewRepository.saveAll(docReviews);
    }

    @Override
    public void approveBatch(List<FileDocument> fileDocumentList) {
        List<DocReview> docReviews = Lists.newArrayList();
        for (FileDocument fileDocument : fileDocumentList) {
            updateDocTxt(fileDocument);
            docReviews.add(docReviewInstance(fileDocument, null, true));
        }
        // 可以进行批量操作，相对效率较save更高
        docReviewRepository.saveAll(docReviews);
    }

    /**
     * @author luojiarui
     * 管理员审核通过以后，对文档进行文本提取等工作
     **/
    private void updateDocTxt(FileDocument fileDocument) {
        String originFileName = fileDocument.getName();
        //获取文件后缀名
        String suffix = originFileName.substring(originFileName.lastIndexOf(".") + 1);
        switch (suffix) {
            case "pdf":
            case "docx":
            case "ppt":
            case "pptx":
            case "xlsx":
            case "html":
            case "md":
            case "txt":
                taskExecuteService.execute(fileDocument);
                break;
            default:
                break;
        }
    }

    /**
     * @return boolean
     * @author luojiarui
     * 判断这个文档是否已经存在于评审列表中
     **/
    @Override
    public boolean docIdExist(List<String> docIds) {
        return docReviewRepository.existsByDocIdIn(docIds);
    }

    /***
     * <p>取消用户的限制</p>
     * @param docIds 文档id列表
     * @param userId 用户id
     **/
    @Override
    public void deleteReviewsBatch(List<String> docIds, String userId) {
        docReviewRepository.deleteByIdList(docIds);
    }

    @Override
    public PageVO<DocReview> queryReviewLog(BasePageDTO page, String userId, Boolean isAdmin) {
        long count = docReviewRepository.countByUserId(userId, isAdmin);
        List<DocReview> docReviews = docReviewRepository.findByPage(page.getPage(), page.getRows(), userId, isAdmin);
        return PageVO.<DocReview>builder()
                .total(count)
                .list(docReviews)
                .pageNum(page.getPage())
                .pageSize(page.getRows())
                .build();
    }


    @Override
    public void removeReviews(List<String> docIds) {
        if (CollectionUtils.isEmpty(docIds)) {
            return;
        }
        docReviewRepository.deleteByIdList(docIds);
    }
}
