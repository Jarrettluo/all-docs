package com.jiaruiblog.controller;

import com.jiaruiblog.common.ApiResult;
import com.jiaruiblog.common.MessageConstant;
import com.jiaruiblog.entity.CollectDocRelationship;
import com.jiaruiblog.entity.dto.CollectDTO;
import com.jiaruiblog.service.CollectService;
import com.jiaruiblog.service.IFileService;
import com.jiaruiblog.service.IUserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * @ClassName CollectController
 * @Description user collection module
 * @author luojiarui
 * @Date 2022/6/4 3:11 下午
 * @Version 1.0
 **/
@Tag(name = "用户收藏模块")
@RestController
@Slf4j
@CrossOrigin
@RequestMapping("/collect")
public class CollectController {

    @Resource
    private CollectService collectService;

    @Resource
    private IUserService userService;

    @Resource
    private IFileService fileService;

    /**
     * @author luojiarui
     * @Description 废弃该文档
     * @Date 13:30 2023/4/5
     * @Param [collect, request]
     * @return com.jiaruiblog.util.BaseApiResult
     **/
    @Deprecated
    @PostMapping(value = "/auth/insert")
    public ApiResult<Object> insert(@RequestBody CollectDTO collect, HttpServletRequest request) {
        CollectDocRelationship relationship = setRelationshipValue(collect, request);
        // 必须经过userId和docId的校验，否则不予关注
        if (!userService.isExist(relationship.getUserId()) || !fileService.isExist(relationship.getDocId())) {
            return ApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.OPERATE_FAILED);
        }
         collectService.insert(relationship);
        return ApiResult.success("");
    }

    @DeleteMapping(value = "/auth/remove")
    public ApiResult remove(@RequestBody CollectDTO collect, HttpServletRequest request) {
        collectService.remove(setRelationshipValue(collect, request));

        return ApiResult.success("");
    }

    /**
     * @return com.jiaruiblog.entity.CollectDocRelationship
     * @author luojiarui
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
