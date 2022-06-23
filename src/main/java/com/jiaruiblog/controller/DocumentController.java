package com.jiaruiblog.controller;

import com.jiaruiblog.entity.Comment;
import com.jiaruiblog.entity.DTO.DocumentDTO;
import com.jiaruiblog.entity.DTO.RemoveObjectDTO;
import com.jiaruiblog.service.IFileService;
import com.jiaruiblog.utils.ApiResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    IFileService iFileService;

    @ApiOperation(value = "2.1 查询文档的分页列表页", notes = "根据参数查询文档列表")
    @GetMapping(value = "/list")
    public ApiResult list(@RequestParam DocumentDTO documentDTO){
        return iFileService.list(documentDTO);
    }

    @ApiOperation(value = "2.2 查询文档的详细信息", notes = "查询文档的详细信息")
    @GetMapping(value = "/detail")
    public ApiResult detail(@RequestParam(value = "docId") Long id){
        return iFileService.detail(id);
    }

    @ApiOperation(value = "3.2 删除某个文档", notes = "删除某个文档")
    @DeleteMapping(value = "/remove")
    public ApiResult remove(@RequestBody RemoveObjectDTO removeObjectDTO){
        return iFileService.remove(removeObjectDTO.getId());
    }

}
