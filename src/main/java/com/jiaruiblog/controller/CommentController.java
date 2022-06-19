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

    @ApiOperation(value = "新增单个评论", notes = "新增单个评论")
    @PostMapping(value = "/insert")
    public ApiResult insert(@RequestBody CommentDTO commentDTO, HttpServletRequest request){
        Comment comment = new Comment();
        comment.setContent(commentDTO.getContent());
        comment.setDocId(commentDTO.getDocId().longValue());
//        comment.setId(com);
        return commentService.insert(comment);
    }

    @ApiOperation(value = "更新评论", notes = "更新评论")
    @PostMapping(value = "/update")
    public ApiResult update(@RequestBody Comment comment, HttpServletRequest request){
        return commentService.update(comment);
    }

    @ApiOperation(value = "根据id移除某个评论", notes = "根据id移除某个评论")
    @DeleteMapping(value = "/remove")
    public ApiResult remove(@RequestBody Comment comment, HttpServletRequest request){
        return commentService.remove(comment);
    }

    @ApiOperation(value = "根据文档id查询相关评论", notes = "根据id查询某个评论")
    @PostMapping(value = "/list")
    public ApiResult queryById(@RequestBody Comment comment){
        return commentService.queryById(comment);
    }

}
