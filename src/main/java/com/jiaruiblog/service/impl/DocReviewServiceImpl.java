package com.jiaruiblog.service.impl;

import com.google.common.collect.Maps;
import com.jiaruiblog.auth.PermissionEnum;
import com.jiaruiblog.common.MessageConstant;
import com.jiaruiblog.entity.DocReview;
import com.jiaruiblog.entity.FileDocument;
import com.jiaruiblog.entity.User;
import com.jiaruiblog.entity.dto.BasePageDTO;
import com.jiaruiblog.service.DocReviewService;
import com.jiaruiblog.service.TaskExecuteService;
import com.jiaruiblog.util.BaseApiResult;
import com.mongodb.DuplicateKeyException;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.apache.commons.compress.utils.Lists;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @ClassName DocReviewServiceImpl
 * @Description 文档评审
 * @Author luojiarui
 * @Date 2022/11/30 21:02
 * @Version 1.0
 **/
@Service
public class DocReviewServiceImpl implements DocReviewService {

    public static final String DOC_REVIEW_COLLECTION = "docReview";

    public static final String RESULT = "操作成功了 %d 项目";
    public static final String USER_ID = "userId";
    public static final String DOC_ID = "docId";

    @Resource
    MongoTemplate mongoTemplate;

    @Resource
    private UserServiceImpl userServiceImpl;

    @Resource
    private TaskExecuteService taskExecuteService;

    @Override
    public BaseApiResult userRead(List<String> ids, String userId) {
        // 只能读自己的 文档评审意见//.and(USER_ID).is(userId));
        Query query = new Query(Criteria.where("_id").in(ids));
        Update update = new Update();
        // 修改为已读状态
        update.set("readState", true);
        // 修改更新时间
        update.set("updateDate", new Date());
        UpdateResult updateResult = mongoTemplate.updateMulti(query, update, DocReview.class, DOC_REVIEW_COLLECTION);
        return BaseApiResult.success(String.format(RESULT, updateResult.getModifiedCount()));
    }

    @Override
    public BaseApiResult refuse(FileDocument fileDocument, String reason) {
        // 删除某个文档
        DocReview docReview = docReviewInstance(fileDocument, reason, false);
        if (docReview == null) {
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.OPERATE_FAILED);
        }

        mongoTemplate.save(docReview, DOC_REVIEW_COLLECTION);
        return BaseApiResult.success();
    }

    /**
     * @Author luojiarui
     * @Description 创建一条文档评审的实例
     * @Date 10:24 2022/12/10
     * @Param [fileDocument, reason, approve]
     * @return com.jiaruiblog.entity.DocReview
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
    public BaseApiResult refuseBatch( List<FileDocument> fileDocumentList, String reason) {
        List<DocReview> docReviews = Lists.newArrayList();
        for (FileDocument fileDocument : fileDocumentList) {
            docReviews.add(docReviewInstance(fileDocument, reason, false));
        }
        // 可以进行批量操作，相对效率较save更高
        try {
            mongoTemplate.insert(docReviews, DOC_REVIEW_COLLECTION);
            return BaseApiResult.success();
        } catch (DuplicateKeyException e) {
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.OPERATE_FAILED);
        }
    }

    @Override
    public BaseApiResult approveBatch(List<FileDocument> fileDocumentList) {
        List<DocReview> docReviews = Lists.newArrayList();
        for (FileDocument fileDocument : fileDocumentList) {
            updateDocTxt(fileDocument);
            docReviews.add(docReviewInstance(fileDocument, null, true));
        }
        // 可以进行批量操作，相对效率较save更高
        try {
            mongoTemplate.insert(docReviews, DOC_REVIEW_COLLECTION);
            return BaseApiResult.success();
        } catch (DuplicateKeyException e) {
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE,MessageConstant.OPERATE_FAILED);
        }
    }

    /**
     * @Author luojiarui
     * @Description 管理员审核通过以后，对文档进行文本提取等工作
     * @Date 22:51 2023/3/9
     * @Param [fileDocument]
     **/
    private void updateDocTxt(FileDocument fileDocument) {
        String originFileName = fileDocument.getName();
        //获取文件后缀名
        String suffix = originFileName.substring(originFileName.lastIndexOf(".") + 1);
        switch (suffix) {
            case "pdf":
            case "docx":
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
     * @Author luojiarui
     * @Description 判断这个文档是否已经存在于评审列表中
     * @Date 11:26 2022/12/10
     * @Param [docIds]
     * @return boolean
     **/
    @Override
    public boolean docIdExist(List<String> docIds) {
        Query query = new Query(Criteria.where("docId").in(docIds));
        return mongoTemplate.count(query, DocReview.class, DOC_REVIEW_COLLECTION) > 0;
    }

    @Override
    public BaseApiResult deleteReviewsBatch(List<String> docIds, String userId) {
        Query query = new Query();
        User user = userServiceImpl.queryById(userId);
        // 区分user进行操作
        if (user.getPermissionEnum().equals(PermissionEnum.ADMIN)) {
            query.addCriteria(Criteria.where("_id").in(docIds));
            DeleteResult deleteResult = mongoTemplate.remove(query, DocReview.class, DOC_REVIEW_COLLECTION);
            return BaseApiResult.success(String.format(RESULT, deleteResult.getDeletedCount()));
        }
        query.addCriteria(Criteria.where(USER_ID).is(user.getId()).and("_id").in(docIds));
        Update update = new Update();
        update.set("userRemove", true);
        update.set("updateDate", new Date());
        UpdateResult updateResult = mongoTemplate.updateMulti(query, update, DocReview.class, DOC_REVIEW_COLLECTION);
        return BaseApiResult.success(String.format(RESULT, updateResult.getModifiedCount()));
    }

    @Override
    public BaseApiResult queryReviewLog(BasePageDTO page, String userId, Boolean isAdmin) {

        // 根据不同的user进行区分，如果不是管理员，则必须输入用户id
        Query query = new Query();
        if (!isAdmin && userId != null) {
            query.addCriteria(Criteria.where(USER_ID).is(userId));
        }
        long count = mongoTemplate.count(query, DocReview.class, DOC_REVIEW_COLLECTION);
        if (count < 1) {
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.DATA_IS_NULL);
        }

        query.with(Sort.by(Sort.Direction.DESC, "createDate"));
        query.skip((long) (page.getPage()-1) * page.getRows());
        query.limit(page.getRows());

        // 还需要进行分页
        List<DocReview> docReviews = mongoTemplate.find(query, DocReview.class, DOC_REVIEW_COLLECTION);
        Map<String, Object> result = Maps.newHashMap();
        result.put("total", count);
        result.put("data", docReviews);
        result.put("pageNum", page.getPage());
        result.put("pageSize", page.getRows());
        return BaseApiResult.success(result);
    }

    @Override
    public void removeReviews(List<String> docIds){
        if (CollectionUtils.isEmpty(docIds)) {
            return;
        }
        Query query = new Query(Criteria.where(DOC_ID).in(docIds));
        mongoTemplate.remove(query, DocReview.class, DOC_REVIEW_COLLECTION);
    }
}
