package com.jiaruiblog.service.impl;

import com.jiaruiblog.entity.DocReview;
import com.jiaruiblog.entity.FileDocument;
import com.jiaruiblog.entity.dto.BasePageDTO;
import com.jiaruiblog.entity.vo.PageVO;
import com.jiaruiblog.service.DocReviewService;
import com.jiaruiblog.service.TaskExecuteService;
import com.mongodb.DuplicateKeyException;
import com.mongodb.client.result.UpdateResult;
import jakarta.annotation.Resource;
import org.apache.commons.compress.utils.Lists;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
/**
 * @ClassName DocReviewServiceImpl
 * @Description 文档评审
 * @author luojiarui
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

//    @Resource
//    private UserServiceImpl userServiceImpl;

    @Resource
    private TaskExecuteService taskExecuteService;

    @Override
    public UpdateResult userRead(List<String> ids, String userId) {
        // 只能读自己的 文档评审意见//.and(USER_ID).is(userId));
        Query query = new Query(Criteria.where("_id").in(ids));
        Update update = new Update();
        // 修改为已读状态
        update.set("readState", true);
        // 修改更新时间
        update.set("updateDate", new Date());
        UpdateResult updateResult = mongoTemplate.updateMulti(query, update, DocReview.class, DOC_REVIEW_COLLECTION);
        return updateResult;
    }

    @Override
    public void refuse(FileDocument fileDocument, String reason) {
        // 删除某个文档
        DocReview docReview = docReviewInstance(fileDocument, reason, false);
        if (docReview == null) {
            return;
        }

        mongoTemplate.save(docReview, DOC_REVIEW_COLLECTION);
    }

    /**
     * @author luojiarui
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
    public void refuseBatch( List<FileDocument> fileDocumentList, String reason) {
        List<DocReview> docReviews = Lists.newArrayList();
        for (FileDocument fileDocument : fileDocumentList) {
            docReviews.add(docReviewInstance(fileDocument, reason, false));
        }
        // 可以进行批量操作，相对效率较save更高
        try {
            mongoTemplate.insert(docReviews, DOC_REVIEW_COLLECTION);
            return;
        } catch (DuplicateKeyException e) {
            return;
        }
    }

    @Override
    public void approveBatch(List<FileDocument> fileDocumentList) {
        List<DocReview> docReviews = Lists.newArrayList();
        for (FileDocument fileDocument : fileDocumentList) {
            updateDocTxt(fileDocument);
            docReviews.add(docReviewInstance(fileDocument, null, true));
        }
        // 可以进行批量操作，相对效率较save更高
        try {
            mongoTemplate.insert(docReviews, DOC_REVIEW_COLLECTION);
            return ;
        } catch (DuplicateKeyException e) {
            return;
        }
    }

    /**
     * @author luojiarui
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
     * @author luojiarui
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

    /***
     * <p>取消用户的限制</p>
     * @param docIds
 * @param userId
     * @return com.jiaruiblog.util.BaseApiResult
     **/
    @Override
    public UpdateResult deleteReviewsBatch(List<String> docIds, String userId) {
        Query query = new Query();
//        User user = userServiceImpl.queryById(userId);
        // 区分user进行操作
//        if (user.getPermissionEnum().equals(PermissionEnum.ADMIN)) {
//            query.addCriteria(Criteria.where("_id").in(docIds));
//            DeleteResult deleteResult = mongoTemplate.remove(query, DocReview.class, DOC_REVIEW_COLLECTION);
//            return ApiResult.success(String.format(RESULT, deleteResult.getDeletedCount()));
//        }
//        query.addCriteria(Criteria.where(USER_ID).is(user.getId()).and("_id").in(docIds));
        Update update = new Update();
        update.set("userRemove", true);
        update.set("updateDate", new Date());
        UpdateResult updateResult = mongoTemplate.updateMulti(query, update, DocReview.class, DOC_REVIEW_COLLECTION);
        return updateResult;
    }

    @Override
    public PageVO<DocReview> queryReviewLog(BasePageDTO page, String userId, Boolean isAdmin) {

        // 根据不同的user进行区分，如果不是管理员，则必须输入用户id
        Query query = new Query();
        if (!isAdmin && userId != null) {
            query.addCriteria(Criteria.where(USER_ID).is(userId));
        }
        long count = mongoTemplate.count(query, DocReview.class, DOC_REVIEW_COLLECTION);

        query.with(Sort.by(Sort.Direction.DESC, "createDate"));
        query.skip((long) (page.getPage()-1) * page.getRows());
        query.limit(page.getRows());

        // 还需要进行分页
        List<DocReview> docReviews = mongoTemplate.find(query, DocReview.class, DOC_REVIEW_COLLECTION);

        return PageVO.<DocReview>builder()
                .total(count)
                .list(docReviews)
                .pageNum( page.getPage())
                .pageSize(page.getRows())
                .build();

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
