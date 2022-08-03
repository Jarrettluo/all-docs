package com.jiaruiblog.service;

import com.jiaruiblog.entity.Comment;
import com.jiaruiblog.utils.ApiResult;

public interface ICommentService {

    ApiResult insert(Comment comment);
    ApiResult update(Comment comment);
    ApiResult remove(Comment comment, String userId);
    ApiResult queryById(Comment comment);
    ApiResult search(Comment comment);

}
