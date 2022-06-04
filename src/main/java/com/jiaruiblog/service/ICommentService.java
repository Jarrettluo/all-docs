package com.jiaruiblog.service;

import com.jiaruiblog.entity.Comment;
import com.jiaruiblog.utils.ApiResult;

public interface ICommentService {

    ApiResult insertTag(Comment comment);
    ApiResult updateTag(Comment comment);
    ApiResult removeTag(Comment comment);
    ApiResult queryById(Comment comment);
    ApiResult search(Comment comment);

}
