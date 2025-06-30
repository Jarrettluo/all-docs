package com.jiaruiblog.service;


import com.jiaruiblog.entity.DocReview;
import com.jiaruiblog.entity.FileDocument;
import com.jiaruiblog.entity.dto.BasePageDTO;
import com.jiaruiblog.entity.vo.PageVO;
import com.mongodb.client.result.UpdateResult;

import java.util.List;

public interface DocReviewService {


    /**
     * @author luojiarui
     * @Description 用户修改为已读状态
     * @Date 20:47 2022/11/30
     * @Param [reviewId]
     * @return com.jiaruiblog.util.BaseApiResult
     **/
    UpdateResult userRead(List<String> ids, String userId);

    boolean docIdExist(List<String> docIds);

    /**
     * @author luojiarui
     * @Description 拒绝文档
     * @Date 20:54 2022/11/30
     * @Param [docId, reason] 文档的id 和 拒绝的原因
     * @return com.jiaruiblog.util.BaseApiResult
     **/
    void refuse(FileDocument fileDocument, String reason);

    /**
     * @author luojiarui
     * @Description 批量拒绝文档
     * @Date 20:54 2022/11/30
     * @Param [docId] 文档列表的id
     * @return com.jiaruiblog.util.BaseApiResult
     **/
    void refuseBatch(List<FileDocument> fileDocumentList, String reason);

    /**
     * @author luojiarui
     * @Description 管理员同意一批文档
     * @Date 22:04 2022/12/9
     * @Param [fileDocumentList]
     * @return com.jiaruiblog.util.BaseApiResult
     **/
    void approveBatch(List<FileDocument> fileDocumentList);


    /**
     * @author luojiarui
     * @Description 管理员或者普通用户删除评审
     * @Date 20:53 2022/11/30
     * @Param []
     * @return com.jiaruiblog.util.BaseApiResult
     **/
    UpdateResult deleteReviewsBatch(List<String> docIds, String userId);

    /**
     * @author luojiarui
     * @Description 查询评审的日志
     * 区分管理员和普通用户
     * @Date 20:57 2022/11/30
     * @Param [page, user]
     * @return com.jiaruiblog.util.BaseApiResult
     **/
    PageVO<DocReview> queryReviewLog(BasePageDTO page, String userId, Boolean isAdmin);

    void removeReviews(List<String> docIds);
}
