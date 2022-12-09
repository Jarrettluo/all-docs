package com.jiaruiblog.service.impl;

import com.google.common.collect.Maps;
import com.jiaruiblog.entity.BasePageDTO;
import com.jiaruiblog.entity.DocReview;
import com.jiaruiblog.entity.FileDocument;
import com.jiaruiblog.entity.User;
import com.jiaruiblog.service.DocReviewService;
import com.jiaruiblog.util.BaseApiResult;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @ClassName DocReviewServiceImpl
 * @Description TODO
 * @Author luojiarui
 * @Date 2022/11/30 21:02
 * @Version 1.0
 **/
@Service
public class DocReviewServiceImpl implements DocReviewService {

    public static final String DOC_REVIEW_COLLECTION = "docReview";

    public static final String RESULT = "操作成功了{0}";

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public BaseApiResult queryReviewsByPage(BasePageDTO page) {
        return BaseApiResult.success();
    }


    @Override
    public BaseApiResult userRead(List<String> ids) {
        // 只能读自己的
        Query query = new Query(Criteria.where("DOC_ID").in(ids))
                .with(Sort.by(Sort.Direction.DESC, "createDate"));
        Update update = new Update();
        update.set("content", "comment.getContent()");
        update.set("updateDate", new Date());

        UpdateResult updateResult = mongoTemplate.updateMulti(query, update, DOC_REVIEW_COLLECTION);
        return BaseApiResult.success(String.format(RESULT, updateResult.getModifiedCount()));
    }

    @Override
    public BaseApiResult refuse(String docId, String reason) {
        // 校验某个文档是否存在
        // 删除某个文档
        DocReview docReview = new DocReview();
        docReview.setDocId(docId);
        docReview.setDocName("reason");
        docReview.setUserId("");
        docReview.setUserName("");
        docReview.setCheckState(false);
        docReview.setReadState(false);
        docReview.setUserRemove(false);
        docReview.setReviewLog(reason);
        docReview.setCreateDate(new Date());
        docReview.setUpdateDate(new Date());
        mongoTemplate.save(docReview, DOC_REVIEW_COLLECTION);
        return BaseApiResult.success();
    }

    @Override
    public BaseApiResult refuseBatch(List<String> docIds, String reason) {

        List<FileDocument> fileDocuments = Lists.newArrayList();
        for (FileDocument fileDocument : fileDocuments) {
            DocReview docReview = new DocReview();
            docReview.setDocId(fileDocument.getId());
            docReview.setDocName(fileDocument.getId());
            docReview.setUserId("");
            docReview.setUserName("");
            docReview.setCheckState(false);
            docReview.setReadState(false);
            docReview.setUserRemove(false);
            docReview.setReviewLog(reason);
            docReview.setCreateDate(new Date());
            docReview.setUpdateDate(new Date());
        }
        return BaseApiResult.success();
    }

    @Override
    public BaseApiResult approveBatch(List<String> docIds) {

        return BaseApiResult.success();
    }


    @Override
    public BaseApiResult deleteReviewsBatch(List<String> docIds) {
        Query query = new Query();
        // 区分user进行操作
        DeleteResult deleteResult = mongoTemplate.remove(query, DocReview.class, DOC_REVIEW_COLLECTION);
        return BaseApiResult.success(String.format(RESULT, deleteResult.getDeletedCount()));
    }

    @Override
    public BaseApiResult queryReviewLog(BasePageDTO page, User user) {
        // 根据不同的user进行区分
        Query query = new Query();
        query.skip((long) page.getPage() * page.getRows());
        query.limit(page.getRows());

        // 还需要进行分页
        List<DocReview> docReviews = mongoTemplate.find(query, DocReview.class, DOC_REVIEW_COLLECTION);
        long count = mongoTemplate.count(query, DocReview.class, DOC_REVIEW_COLLECTION);
        Map<String, Object> result = Maps.newHashMap();
        result.put("total", count);
        result.put("data", docReviews);
        return BaseApiResult.success(result);
    }

    @Override
    public BaseApiResult queryDocLogs(BasePageDTO page, User user) {
        // 根据不同的用户进行查询
        Query query = new Query();

        query.skip((long) page.getPage() * page.getRows());
        query.limit(page.getRows());

        mongoTemplate.find(query, DocReview.class);
        return null;
    }

    @Override
    public BaseApiResult deleteDocLogBatch(List<String> logIds) {
        return BaseApiResult.success();
    }
}
