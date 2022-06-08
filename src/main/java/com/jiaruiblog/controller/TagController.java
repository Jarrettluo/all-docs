package com.jiaruiblog.controller;

import com.jiaruiblog.entity.CateDocRelationship;
import com.jiaruiblog.entity.Tag;
import com.jiaruiblog.entity.TagDocRelationship;
import com.jiaruiblog.entity.User;
import com.jiaruiblog.service.TagService;
import com.jiaruiblog.utils.ApiResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * @ClassName TagController
 * @Description TODO
 * @Author luojiarui
 * @Date 2022/6/4 3:11 下午
 * @Version 1.0
 **/
@Api(tags = "标签模块")
@RestController
@Slf4j
@RequestMapping("/tag")
public class TagController {

    @Autowired
    TagService tagService;

    @ApiOperation(value = "新增单个标签", notes = "新增单个标签")
    @PostMapping(value = "/insert")
    public ApiResult insertTag(@RequestBody Tag tag){
        return tagService.insert(tag);
    }

    @ApiOperation(value = "更新标签", notes = "更新标签")
    @PostMapping(value = "/update")
    public ApiResult updateTag(@RequestBody Tag tag){
        return tagService.update(tag);
    }

    @ApiOperation(value = "根据id移除某个标签", notes = "根据id移除某个标签")
    @PostMapping(value = "/remove")
    public ApiResult removeTag(@RequestBody Tag tag){
        return tagService.remove(tag);
    }

    @ApiOperation(value = "根据id查询某个标签", notes = "根据id查询某个标签")
    @PostMapping(value = "/queryById")
    public ApiResult queryById(@RequestBody Tag tag){
        return tagService.queryById(tag);
    }

    @ApiOperation(value = "根据关键字检索标签", notes = "检索标签")
    @PostMapping(value = "/search")
    public ApiResult search(@RequestBody Tag tag){
        return tagService.search(tag);
    }


    @ApiOperation(value = "根据关键字检索分类", notes = "检索分类")
    @PostMapping(value = "/addRelationship")
    public ApiResult addRealationship(@RequestBody TagDocRelationship relationship) {
        return tagService.addRelationShip(relationship);
    }
    @ApiOperation(value = "根据关键字检索分类", notes = "检索分类")
    @DeleteMapping(value = "/moveRelationship")
    public ApiResult removeRelationship(@RequestBody TagDocRelationship relationship) {
        return tagService.cancleTagRelationship(relationship);
    }


}
