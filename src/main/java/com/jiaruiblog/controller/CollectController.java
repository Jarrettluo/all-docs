package com.jiaruiblog.controller;

import com.jiaruiblog.entity.CollectDocRelationship;
import com.jiaruiblog.entity.dto.CollectDTO;
import com.jiaruiblog.service.CollectService;
import com.jiaruiblog.utils.ApiResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

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
@CrossOrigin
@RequestMapping("/collect")
public class CollectController {

    @Autowired
    CollectService collectService;

    @ApiOperation(value = "2.3 新增一个收藏文档", notes = "新增单个收藏文档")
    @PostMapping(value = "/auth/insert")
    public ApiResult insert(@RequestBody CollectDTO collect, HttpServletRequest request){
        return collectService.insert(setRelationshipValue(collect, request));
    }

    @ApiOperation(value = "2.4 根据id移除某个收藏文档", notes = "根据id移除某个文档")
    @DeleteMapping(value = "/auth/remove")
    public ApiResult remove(@RequestBody CollectDTO collect, HttpServletRequest request){
        return collectService.remove(setRelationshipValue(collect, request));
    }

    /**
     * @Author luojiarui
     * @Description // 创建一个关系实体
     * @Date 9:36 下午 2022/6/23
     * @Param [collect, request]
     * @return com.jiaruiblog.entity.CollectDocRelationship
     **/
    private CollectDocRelationship setRelationshipValue(CollectDTO collect, HttpServletRequest request) {
        CollectDocRelationship relationship = new CollectDocRelationship();
        relationship.setDocId(collect.getDocId());
        relationship.setUserId((String) request.getAttribute("id"));
        return relationship;
    }

}
