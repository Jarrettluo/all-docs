package com.jiaruiblog.controller;

import com.jiaruiblog.entity.CateDocRelationship;
import com.jiaruiblog.entity.Category;
import com.jiaruiblog.entity.Tag;
import com.jiaruiblog.enums.Type;
import com.jiaruiblog.service.CategoryService;
import com.jiaruiblog.utils.ApiResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    // 一个文章只能有一个分类项目
    // 一个文章下可能有多个列表


    @Autowired
    CategoryService categoryService;

    @ApiOperation(value = "新增单个分类", notes = "新增单个分类")
    @PostMapping(value = "/insert")
    public ApiResult insert(@RequestBody Category category){
        return categoryService.insert(category);
    }

    @ApiOperation(value = "更新分类", notes = "更新分类")
    @PutMapping(value = "/update")
    public ApiResult update(@RequestBody Category category){
        return categoryService.update(category);
    }

    @ApiOperation(value = "根据id移除某个分类", notes = "根据id移除某个分类")
    @DeleteMapping(value = "/remove")
    public ApiResult remove(@RequestBody Category category){
        return categoryService.remove(category);
    }

    @ApiOperation(value = "根据id移除某个分类", notes = "根据id移除某个分类")
    @GetMapping(value = "/all")
    public ApiResult list(@RequestParam Type type){
        return categoryService.remove(category);
    }

    @ApiOperation(value = "根据关键字检索分类", notes = "检索分类")
    @PostMapping(value = "/addRelationship")
    public ApiResult addRealationship(@RequestBody CateDocRelationship relationship) {
        return categoryService.addRelationShip(relationship);
    }
    @ApiOperation(value = "根据关键字检索分类", notes = "检索分类")
    @DeleteMapping(value = "/removeRelationship")
    public ApiResult removeRelationship(@RequestBody CateDocRelationship relationship) {
        return categoryService.cancleCategoryRelationship(relationship);
    }


}
