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
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @ClassName CommentServiceImpl
 * @Description TODO
 * @Author luojiarui
 * @Date 2022/6/4 5:23 下午
 * @Version 1.0
 **/
@Service
public class CommentServiceImpl implements ICommentService {


    private static String collectionName = "commentCollection";

    @Autowired
    MongoTemplate template;


    @Override
    public ApiResult insert(Comment comment) {
        comment.setCreateDate(new Date());
        comment.setUpdateDate(new Date());
        template.save(comment, collectionName);
        return ApiResult.success(MessageConstant.SUCCESS);
    }

    @Override
    public ApiResult update(Comment comment) {
        Query query = new Query(Criteria.where("_id").is(comment.getId()));
        Comment commentDb = template.findById(comment.getId(), Comment.class, collectionName);
        if( !commentDb.getUserId().equals(comment.getUserId())) {
            return ApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.OPERATE_FAILED);
        }

        Update update  = new Update();
        update.set("content", comment.getContent());
        update.set("updateDate", new Date());
        UpdateResult updateResult = template.updateFirst(query, update, User.class);
        return ApiResult.success(MessageConstant.SUCCESS);
    }

    @Override
    public ApiResult remove(Comment comment, Long userId) {
        Query query = new Query(Criteria.where("_id").is(comment.getId()));
        Comment commentDb = template.findById(comment.getId(), Comment.class, collectionName);
        if( !commentDb.getUserId().equals(comment.getUserId())) {
            return ApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.OPERATE_FAILED);
        }
        template.remove(query, Comment.class, collectionName);
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

    /**
     * @Author luojiarui
     * @Description //根据文档的id 查询评论的数量
     * @Date 10:47 下午 2022/6/22
     * @Param [docId]
     * @return java.lang.Long
     **/
    public Long commentNum(Long docId) {
        Query query = new Query().addCriteria(Criteria.where("docId").is(docId));
        return template.count(query, collectionName);
    }

    /**
     * 根据关键字模糊搜索相关的文档id
     * @param keyWord 关键字
     * @return 文档的id信息
     */
    public List<String> fuzzySearchDoc(String keyWord) {
        if(keyWord == null || "".equalsIgnoreCase(keyWord)) {
            return null;
        }
        Pattern pattern = Pattern.compile("^.*"+keyWord+".*$", Pattern.CASE_INSENSITIVE);
        Query query = new Query();
        query.addCriteria(Criteria.where("name").regex(pattern));

        List<Comment> comments = template.find(query, Comment.class, collectionName);
        return comments.stream().map(Comment::getDocId).collect(Collectors.toList());

    }

}
