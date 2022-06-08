package com.jiaruiblog.controller;

import com.jiaruiblog.common.MessageConstant;
import com.jiaruiblog.entity.Comment;
import com.jiaruiblog.entity.Tag;
import com.jiaruiblog.service.ICommentService;
import com.jiaruiblog.utils.ApiResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ApiResult insert(@RequestBody Comment comment){
        return commentService.insert(comment);
    }

    @ApiOperation(value = "更新评论", notes = "更新评论")
    @PostMapping(value = "/update")
    public ApiResult update(@RequestBody Comment comment){
        return commentService.update(comment);
    }

    @ApiOperation(value = "根据id移除某个评论", notes = "根据id移除某个评论")
    @PostMapping(value = "/remove")
    public ApiResult remove(@RequestBody Comment comment){
        return commentService.remove(comment);
    }

    @ApiOperation(value = "根据id查询某个评论", notes = "根据id查询某个评论")
    @PostMapping(value = "/queryById")
    public ApiResult queryById(@RequestBody Comment comment){
        return commentService.queryById(comment);
    }

    @ApiOperation(value = "根据关键字检索评论", notes = "检索评论")
    @PostMapping(value = "/search")
    public ApiResult search(@RequestBody Comment comment){
        return commentService.search(comment);
    }

}
