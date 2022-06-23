package com.jiaruiblog.controller;

import com.jiaruiblog.common.MessageConstant;
import com.jiaruiblog.entity.Comment;
import com.jiaruiblog.entity.DTO.CommentDTO;
import com.jiaruiblog.entity.Tag;
import com.jiaruiblog.service.ICommentService;
import com.jiaruiblog.utils.ApiResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * @ClassName CommentController
 * @Description TODO
 * @Author luojiarui
 * @Date 2022/6/4 3:11 下午
 * @Version 1.0
 **/
@Api(tags = "评论模块")
@RestController
@Slf4j
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    ICommentService commentService;

    @ApiOperation(value = "2.5 新增单个评论", notes = "新增单个评论")
    @PostMapping(value = "/insert")
    public ApiResult insert(@RequestBody CommentDTO commentDTO, HttpServletRequest request){
        return commentService.insert(getComment(commentDTO, request));
    }

    @ApiOperation(value = "2.6 更新评论", notes = "更新评论")
    @PostMapping(value = "/update")
    public ApiResult update(@RequestBody CommentDTO commentDTO, HttpServletRequest request){
        return commentService.update(getComment(commentDTO, request));
    }

    @ApiOperation(value = "2.7 根据id移除某个评论", notes = "根据id移除某个评论")
    @DeleteMapping(value = "/remove")
    public ApiResult remove(@RequestBody Comment comment, HttpServletRequest request){
        Long userId = (Long) request.getAttribute("id");
        return commentService.remove(comment, userId);
    }

    @ApiOperation(value = "2.8 根据文档id查询相关评论", notes = "根据id查询某个评论")
    @PostMapping(value = "/list")
    public ApiResult queryById(@RequestBody Comment comment){
        return commentService.queryById(comment);
    }

    /**
     * @Author luojiarui
     * @Description // 类型转换
     * @Date 10:18 下午 2022/6/23
     * @Param [commentDTO, request]
     * @return com.jiaruiblog.entity.Comment
     **/
    private Comment getComment(CommentDTO commentDTO, HttpServletRequest request) {
        commentDTO = Optional.ofNullable(commentDTO).orElse(new CommentDTO());
        Comment comment = new Comment();
        comment.setContent(commentDTO.getContent());
        comment.setDocId(commentDTO.getDocId().longValue());
        comment.setUserName((String) request.getAttribute("userName"));
        comment.setUserId((Long) request.getAttribute("id"));
        return comment;
    }
}
