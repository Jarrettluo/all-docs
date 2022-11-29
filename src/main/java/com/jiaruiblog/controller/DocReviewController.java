package com.jiaruiblog.controller;

import com.jiaruiblog.entity.BasePageDTO;
import com.jiaruiblog.util.BaseApiResult;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author Jarrett Luo
 * @Date 2022/11/25 15:56
 * @Version 1.0
 */
@Slf4j
@RequestMapping("/docReview")
public class DocReviewController {

    /**
     * 普通用户、管理员用户，列表查询
     * @return BaseApiResult
     */
    @ApiOperation(value = "2.6 更新评论", notes = "更新评论")
    @GetMapping("queryDocReviewList")
    public BaseApiResult queryDocReviewList(@ModelAttribute("pageParams") BasePageDTO pageParams) {



        return BaseApiResult.success();
    }


    /**
     * 修改已读
     * @return BaseApiResult
     */
    @ApiOperation(value = "2.6 更新评论", notes = "更新评论")
    @PutMapping("updateDocReview")
    public BaseApiResult updateDocReview(@RequestBody List<String> ids) {
        // 修改评审意见为通过
        return BaseApiResult.success();
    }

    // 单个进行拒绝
    @PostMapping("signalA")
    public BaseApiResult single() {
        return BaseApiResult.success();
    }

    // 批量进行拒绝，并删除文档
    @PostMapping("allA")
    public BaseApiResult allA() {
        return BaseApiResult.success();
    }

    // 评审结果列表
    @GetMapping("queryReviewResultList")
    public BaseApiResult queryReviewResultList(@ModelAttribute("pageParams") BasePageDTO pageParams) {
        return BaseApiResult.success();
    }


    /**
     * 普通用户删除，管理员删除，删除评审日志
     * @return BaseApiResult
     */
    @ApiOperation(value = "2.6 更新评论", notes = "更新评论")
    @DeleteMapping("removeDocReview")
    public BaseApiResult removeDocReview() {
        return BaseApiResult.success();
    }


    // 评审结果列表
    @GetMapping("queryLogList")
    public BaseApiResult queryLogList(@ModelAttribute("pageParams") BasePageDTO pageParams) {
        return BaseApiResult.success();
    }

    @DeleteMapping("removeDocReview")
    public BaseApiResult removeLog() {
        return BaseApiResult.success();
    }

}
