package com.jiaruiblog.service;

import com.jiaruiblog.entity.Comment;
import com.jiaruiblog.entity.dto.BasePageDTO;
import com.jiaruiblog.entity.dto.CommentListDTO;
import com.jiaruiblog.util.BaseApiResult;

import java.util.List;

/**
 * @author jiarui.luo
 */
public interface ICommentService {

    /**
     * insert
     * @param comment Comment
     * @return result
     */
    ApiResult<Object> insert(Comment comment);

    /**
     * update
     * @param comment Comment
     * @return result
     */
    ApiResult<Object> update(Comment comment);

    /**
     * remove
     * @param comment Comment
     * @param userId userId
     * @return result
     */
    ApiResult<Object> remove(Comment comment, String userId);

    /**
     * @author luojiarui
     * @Description 批量删除评论列表
     * @Date 20:49 2023/2/12
     * @Param [commentIdList]
     * @return com.jiaruiblog.util.BaseApiResult
     **/
    ApiResult<Object> removeBatch(List<String> commentIdList);

    /**
     * queryById
     * @param comment CommentListDTO
     * @return result
     */
    ApiResult<Object> queryById(CommentListDTO comment);

    /**
     * search
     * @param comment Comment
     * @return result
     */
    ApiResult<Object> search(Comment comment);

    /**
     * @author luojiarui
     * @Description 查询评论列表
     * @Date 14:44 2022/12/10
     * @Param [pageDTO, userId]
     * @return com.jiaruiblog.util.BaseApiResult
     **/
    ApiResult<Object> queryAllComments(BasePageDTO pageDTO, String userId, Boolean isAdmin);

    long countAllFile();

    List<String> fuzzySearchDoc(String keyWord);

    void removeByDocId(String docId);

    Long commentNum(String docId);

}
