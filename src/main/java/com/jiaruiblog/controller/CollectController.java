package com.jiaruiblog.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.jiaruiblog.common.MessageConstant;
import com.jiaruiblog.entity.CollectDocRelationship;
import com.jiaruiblog.entity.dto.CollectDTO;
import com.jiaruiblog.service.impl.CollectServiceImpl;
import com.jiaruiblog.util.BaseApiResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * @ClassName CollectController
 * @Description user collection module
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

    @Resource
    CollectServiceImpl collectServiceImpl;

    /**
     * @Author luojiarui
     * @Description 废弃该文档
     * @Date 13:30 2023/4/5
     * @Param [collect, request]
     * @return com.jiaruiblog.util.BaseApiResult
     **/
    @Deprecated
    @ApiOperation(value = "新增一个收藏文档", notes = "新增单个收藏文档")
    @PostMapping(value = "/auth/insert")
    public BaseApiResult insert(@RequestBody CollectDTO collect, HttpServletRequest request) {
        if (!StpUtil.hasPermission("collect.insert")) {
            return BaseApiResult.error(MessageConstant.AUTH_ERROR_CODE, MessageConstant.NOT_PERMISSION);
        }
        return collectServiceImpl.insert(setRelationshipValue(collect, request));
    }

    @ApiOperation(value = "根据id移除某个收藏文档", notes = "根据id移除某个文档")
    @DeleteMapping(value = "/auth/remove")
    public BaseApiResult remove(@RequestBody CollectDTO collect, HttpServletRequest request) {
        if (!StpUtil.hasPermission("collect.remove")) {
            return BaseApiResult.error(MessageConstant.AUTH_ERROR_CODE, MessageConstant.NOT_PERMISSION);
        }
        return collectServiceImpl.remove(setRelationshipValue(collect, request));
    }

    /**
     * @return com.jiaruiblog.entity.CollectDocRelationship
     * @Author luojiarui
     * @Description // 创建一个关系实体
     * @Date 9:36 下午 2022/6/23
     * @Param [collect, request]
     **/
    private CollectDocRelationship setRelationshipValue(CollectDTO collect, HttpServletRequest request) {
        CollectDocRelationship relationship = new CollectDocRelationship();
        relationship.setDocId(collect.getDocId());
        relationship.setUserId((String) request.getAttribute("id"));
        relationship.setCreateDate(new Date());
        relationship.setUpdateDate(new Date());
        return relationship;
    }

}
