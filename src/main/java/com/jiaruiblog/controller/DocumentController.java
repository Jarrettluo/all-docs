package com.jiaruiblog.controller;

import com.jiaruiblog.entity.Comment;
import com.jiaruiblog.entity.DTO.DocumentDTO;
import com.jiaruiblog.utils.ApiResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName DocumentController
 * @Description TODO
 * @Author luojiarui
 * @Date 2022/6/19 5:18 下午
 * @Version 1.0
 **/

@Api(tags = "文档模块")
@RestController
@Slf4j
@RequestMapping("/document")
public class DocumentController {

    @ApiOperation(value = "查询文档的分页列表页", notes = "新增单个评论")
    @GetMapping(value = "/list")
    public ApiResult list(@RequestParam DocumentDTO documentDTO){
        return ApiResult.success();
    }

    @ApiOperation(value = "查询文档的详细信息", notes = "新增单个评论")
    @GetMapping(value = "/detail")
    public ApiResult detail(@RequestParam(value = "docId") Integer id){
        return ApiResult.success();
    }

    @ApiOperation(value = "查询文档的详细信息", notes = "新增单个评论")
    @DeleteMapping(value = "/remove")
    public ApiResult remove(@RequestBody Integer id){
        return ApiResult.success();
    }




}
