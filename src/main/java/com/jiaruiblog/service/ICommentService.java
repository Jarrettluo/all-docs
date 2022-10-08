package com.jiaruiblog.service;

import com.jiaruiblog.entity.Comment;
import com.jiaruiblog.entity.dto.CommentListDTO;
import com.jiaruiblog.util.BaseApiResult;

/**
 * @author jiarui.luo
 */
public interface ICommentService {

    BaseApiResult insert(Comment comment);
    BaseApiResult update(Comment comment);
    BaseApiResult remove(Comment comment, String userId);
    BaseApiResult queryById(CommentListDTO comment);
    BaseApiResult search(Comment comment);

}
