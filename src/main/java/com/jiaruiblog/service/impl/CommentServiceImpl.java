package com.jiaruiblog.service.impl;

import com.jiaruiblog.common.MessageConstant;
import com.jiaruiblog.entity.Comment;
import com.jiaruiblog.entity.User;
import com.jiaruiblog.service.ICommentService;
import com.jiaruiblog.utils.ApiResult;
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @ClassName CommentServiceImpl
 * @Description TODO
 * @Author luojiarui
 * @Date 2022/6/4 5:23 下午
 * @Version 1.0
 **/
@Service
public class CommentServiceImpl implements ICommentService {

    @Autowired
    MongoTemplate template;


    @Override
    public ApiResult insert(Comment comment) {
        comment.setCreateDate(new Date());
        comment.setUpdateDate(new Date());
        Comment savedComment = template.save(comment);
        return ApiResult.success(MessageConstant.SUCCESS);
    }

    @Override
    public ApiResult update(Comment comment) {
        Query query = new Query(Criteria.where("_id").is(comment.getId()));
        Update update  = new Update();
        update.set("content", comment.getContent());
        update.set("updateDate", new Date());
        UpdateResult updateResult = template.updateFirst(query, update, User.class);
        return ApiResult.success(MessageConstant.SUCCESS);
    }

    @Override
    public ApiResult remove(Comment comment) {
        return ApiResult.success(MessageConstant.SUCCESS);
    }

    @Override
    public ApiResult queryById(Comment comment) {
        return ApiResult.success(MessageConstant.SUCCESS);
    }

    @Override
    public ApiResult search(Comment comment) {
        return ApiResult.success(MessageConstant.SUCCESS);
    }
}
