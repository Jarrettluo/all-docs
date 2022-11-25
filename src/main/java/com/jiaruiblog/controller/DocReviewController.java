package com.jiaruiblog.controller;

import com.jiaruiblog.util.BaseApiResult;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author Jarrett Luo
 * @Date 2022/11/25 15:56
 * @Version 1.0
 */
@Slf4j
@RequestMapping("/comment")
public class DocReviewController {

    /**
     * 普通用户、管理员用户，列表查询
     * @return BaseApiResult
     */
    @ApiOperation(value = "2.6 更新评论", notes = "更新评论")
    @GetMapping("queryDocReviewList")
    public BaseApiResult queryDocReviewList() {
        return BaseApiResult.success();
    }

    /**
     * 修改已读
     * @return BaseApiResult
     */
    @ApiOperation(value = "2.6 更新评论", notes = "更新评论")
    @PutMapping("updateDocReview")
    public BaseApiResult updateDocReview() {
        return BaseApiResult.success();
    }

    /**
     * 普通用户删除，管理员删除
     * @return BaseApiResult
     */
    @ApiOperation(value = "2.6 更新评论", notes = "更新评论")
    @DeleteMapping("removeDocReview")
    public BaseApiResult removeDocReview() {
        return BaseApiResult.success();
    }

}
