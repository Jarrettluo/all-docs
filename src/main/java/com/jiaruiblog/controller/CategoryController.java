package com.jiaruiblog.controller;

import com.jiaruiblog.entity.Category;
import com.jiaruiblog.entity.Tag;
import com.jiaruiblog.utils.ApiResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName CategoryController
 * @Description TODO
 * @Author luojiarui
 * @Date 2022/6/4 3:10 下午
 * @Version 1.0
 **/
@Api(tags = "文档分类模块")
@RestController
@Slf4j
@RequestMapping("/category")
public class CategoryController {

    @ApiOperation(value = "新增单个分类", notes = "新增单个分类")
    @PostMapping(value = "/insert")
    public ApiResult insertTag(@RequestBody Category category){
        return ApiResult.success("新增成功");
    }

    @ApiOperation(value = "更新分类", notes = "更新分类")
    @PutMapping(value = "/update")
    public ApiResult updateTag(@RequestBody Category category){
        return ApiResult.success("新增成功");
    }

    @ApiOperation(value = "根据id移除某个分类", notes = "根据id移除某个分类")
    @DeleteMapping(value = "/remove")
    public ApiResult removeTag(@RequestBody Category category){
        return ApiResult.success("新增成功");
    }

    @ApiOperation(value = "根据id查询某个分类", notes = "根据id查询某个分类")
    @GetMapping(value = "/queryById")
    public ApiResult queryById(@RequestBody Category category){
        return ApiResult.success("新增成功");
    }

    @ApiOperation(value = "根据关键字检索分类", notes = "检索分类")
    @PostMapping(value = "/search")
    public ApiResult search(@RequestBody Category category){
        return ApiResult.success("新增成功");
    }
    // // TODO: 2022/6/4 添加种类下的doc动作，解除

}
