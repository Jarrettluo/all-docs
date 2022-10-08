package com.jiaruiblog.service;

import com.jiaruiblog.entity.Comment;
import com.jiaruiblog.entity.dto.CommentListDTO;
import com.jiaruiblog.util.BaseApiResult;

/**
 * @author jiarui.luo
 */
public interface ICommentService {

    /**
     * insert
     * @param comment Comment
     * @return result
     */
    BaseApiResult insert(Comment comment);

    /**
     * update
     * @param comment Comment
     * @return result
     */
    BaseApiResult update(Comment comment);

    /**
     * remove
     * @param comment Comment
     * @param userId userId
     * @return result
     */
    BaseApiResult remove(Comment comment, String userId);

    /**
     * queryById
     * @param comment CommentListDTO
     * @return result
     */
    BaseApiResult queryById(CommentListDTO comment);

    /**
     * search
     * @param comment Comment
     * @return result
     */
    BaseApiResult search(Comment comment);

}
