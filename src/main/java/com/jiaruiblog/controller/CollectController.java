package com.jiaruiblog.controller;

import com.jiaruiblog.entity.CollectDocRelationship;
import com.jiaruiblog.entity.Tag;
import com.jiaruiblog.service.CollectService;
import com.jiaruiblog.utils.ApiResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName CollectController
 * @Description TODO
 * @Author luojiarui
 * @Date 2022/6/4 3:11 下午
 * @Version 1.0
 **/
@Api(tags = "用户收藏模块")
@RestController
@Slf4j
@RequestMapping("/collect")
public class CollectController {

    @Autowired
    CollectService collectService;

    @ApiOperation(value = "新增一个收藏文档", notes = "新增单个收藏文档")
    @PostMapping(value = "/insert")
    public ApiResult insert(@RequestBody CollectDocRelationship collect){
        return collectService.insert(collect);
    }

//    @ApiOperation(value = "更新标签", notes = "更新标签")
//    @PostMapping(value = "/update")
//    public ApiResult updateTag(@RequestBody Tag tag){
//        return ApiResult.success("新增成功");
//    }

    @ApiOperation(value = "根据id移除某个收藏文档", notes = "根据id移除某个文档")
    @DeleteMapping(value = "/remove")
    public ApiResult remove(@RequestBody CollectDocRelationship collect){
        return collectService.remove(collect);
    }

//    @ApiOperation(value = "根据id查询某个标签", notes = "根据id查询某个标签")
//    @PostMapping(value = "/queryById")
//    public ApiResult queryById(@RequestBody Tag tag){
//        return ApiResult.success("新增成功");
//    }
//
//    @ApiOperation(value = "根据关键字检索标签", notes = "检索标签")
//    @PostMapping(value = "/search")
//    public ApiResult search(@RequestBody Tag tag){
//        return ApiResult.success("新增成功");
//    }


}