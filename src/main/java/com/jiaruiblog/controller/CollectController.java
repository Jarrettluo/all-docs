package com.jiaruiblog.controller;

import com.jiaruiblog.common.ApiResult;
import com.jiaruiblog.entity.CollectDocRelationship;
import com.jiaruiblog.entity.dto.CollectDTO;
import com.jiaruiblog.exception.BusinessException;
import com.jiaruiblog.exception.ErrorCode;
import com.jiaruiblog.service.CollectService;
import com.jiaruiblog.service.DocumentService;
import com.jiaruiblog.service.IUserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * @author luojiarui
 **/
@Tag(name = "用户收藏模块")
@RestController
@Slf4j
@RequestMapping("/collect")
public class CollectController {

    @Resource
    private CollectService collectService;

    @Resource
    private IUserService userService;

    @Resource
    private DocumentService fileService;

    /**
     * @param collect 收藏数据传输对象
     * @param request HTTP请求对象
     * @return ApiResult<Void> 操作结果
     * @author luojiarui
     **/
    @PostMapping(value = "/auth/insert")
    public ApiResult<Void> insert(@RequestBody CollectDTO collect, HttpServletRequest request) {
        log.info("开始执行文档收藏操作，文档ID: {}, 用户ID: {}", collect.getDocId(), request.getAttribute("id"));
        CollectDocRelationship relationship = setRelationshipValue(collect, request);
        // 必须经过userId和docId的校验，否则不予关注
        if (!userService.isExist(relationship.getUserId())) {
            log.error("用户不存在，用户ID: {}", relationship.getUserId());
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        if (!fileService.isExist(relationship.getDocId())) {
            log.error("文档不存在，文档ID: {}", relationship.getDocId());
            throw new BusinessException(ErrorCode.DOCUMENT_NOT_FOUND);
        }
        collectService.insert(relationship);
        log.info("文档收藏成功，文档ID: {}, 用户ID: {}", relationship.getDocId(), relationship.getUserId());
        return ApiResult.success();
    }

    /**
     * @param collect 收藏数据传输对象
     * @param request HTTP请求对象
     * @return ApiResult<Void> 操作结果
     * @author luojiarui
     **/
    @DeleteMapping(value = "/auth/remove")
    public ApiResult<Void> remove(@RequestBody CollectDTO collect, HttpServletRequest request) {
        log.info("开始执行取消收藏操作，文档ID: {}, 用户ID: {}", collect.getDocId(), request.getAttribute("id"));
        CollectDocRelationship relationship = setRelationshipValue(collect, request);
        collectService.remove(relationship);
        log.info("取消收藏成功，文档ID: {}, 用户ID: {}", relationship.getDocId(), relationship.getUserId());
        return ApiResult.success();
    }

    /**
     * @param collect 收藏数据传输对象
     * @param request HTTP请求对象
     * @return CollectDocRelationship 收藏文档关系实体
     * @author luojiarui
     **/
    private CollectDocRelationship setRelationshipValue(CollectDTO collect, HttpServletRequest request) {
        log.debug("创建收藏关系实体，文档ID: {}, 用户ID: {}", collect.getDocId(), request.getAttribute("id"));
        CollectDocRelationship relationship = new CollectDocRelationship();
        relationship.setDocId(collect.getDocId());
        relationship.setUserId((String) request.getAttribute("id"));
        relationship.setCreateDate(new Date());
        relationship.setUpdateDate(new Date());
        return relationship;
    }

}
