package com.jiaruiblog.service.impl;

import com.google.common.collect.Maps;
import com.jiaruiblog.common.MessageConstant;

import com.jiaruiblog.entity.Comment;
import com.jiaruiblog.entity.User;
import com.jiaruiblog.entity.dto.CommentListDTO;
import com.jiaruiblog.service.ICommentService;
import com.jiaruiblog.utils.ApiResult;
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
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
        if( !StringUtils.hasText(comment.getUserId()) || !StringUtils.hasText(comment.getUserName())) {
            return ApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.PARAMS_IS_NOT_NULL);
        }
        comment.setCreateDate(new Date());
        comment.setUpdateDate(new Date());
        template.save(comment, collectionName);
        return ApiResult.success(MessageConstant.SUCCESS);
    }

    @Override
    public ApiResult update(Comment comment) {

        if( !StringUtils.hasText(comment.getUserId()) || !StringUtils.hasText(comment.getUserName())) {
            return ApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.PARAMS_IS_NOT_NULL);
        }

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
    public ApiResult remove(Comment comment, String userId) {
        Query query = new Query(Criteria.where("_id").is(comment.getId()));
        Comment commentDb = template.findById(comment.getId(), Comment.class, collectionName);
        if( !commentDb.getUserId().equals(comment.getUserId())) {
            return ApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.OPERATE_FAILED);
        }
        template.remove(query, Comment.class, collectionName);
        return ApiResult.success(MessageConstant.SUCCESS);
    }

    /**
     * @Author luojiarui
     * @Description 根据文档的id查询相关的评论列表
     * @Date 11:57 2022/9/4
     * @Param [comment]
     * @return com.jiaruiblog.utils.ApiResult
     **/
    @Override
    public ApiResult queryById(CommentListDTO comment) {
        if (comment == null || comment.getDocId() == null) {
            return ApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.PARAMS_FORMAT_ERROR);
        }
        Query query = new Query(Criteria.where("docId").is(comment.getDocId()))
                .with(Sort.by(Sort.Direction.DESC, "uploadDate"));
        Long totalNum = template.count(query, Comment.class, collectionName);
        long skip = (comment.getPage()) * comment.getRows();
        query.skip(skip);
        query.limit(comment.getRows());
        List<Comment> comments = template.find(query, Comment.class, collectionName);

        Map<String, Object> result = Maps.newHashMap();
        result.put("totalNum", totalNum);
        result.put("comments", comments);

        return ApiResult.success(result);
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
    public Long commentNum(String docId) {
        Query query = new Query().addCriteria(Criteria.where("docId").is(docId));
        return template.count(query, Comment.class, collectionName);
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
        query.addCriteria(Criteria.where("content").regex(pattern));

        List<Comment> comments = template.find(query, Comment.class, collectionName);
        return comments.stream().map(Comment::getDocId).collect(Collectors.toList());

    }

    /**
     * @Author luojiarui
     * @Description //根据文档进行删除评论信息
     * @Date 11:14 上午 2022/6/25
     * @Param [docId]
     * @return void
     **/
    public void removeByDocId(String docId) {
        Query query = new Query(Criteria.where("docId").is(docId));
        List<Comment> commentDb = template.find(query, Comment.class, collectionName);
        commentDb.forEach(item -> {
            template.remove(item, collectionName);
        });

    }

    /**
     * @Author luojiarui
     * @Description // 统计总数
     * @Date 4:40 下午 2022/6/26
     * @Param []
     * @return java.lang.Integer
     **/
    public long countAllFile() {
        return template.getCollection(collectionName).estimatedDocumentCount();
    }
}
