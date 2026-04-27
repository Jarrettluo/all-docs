package com.jiaruiblog.api.controller;

import com.jiaruiblog.application.service.LikeService;
import com.jiaruiblog.common.ApiResult;
import com.jiaruiblog.domain.request.LikeRequest;
import com.jiaruiblog.domain.entity.vo.LikeVO;
import com.jiaruiblog.common.enums.RedisActionEnum;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author luojiarui
 * 点赞功能参考地址：https://blog.csdn.net/qq_45243783/article/details/128221372
 * redis的数据和数据库的数据保持同步的方案： https://blog.csdn.net/qq_22343483/article/details/103304826
 **/
@Tag(name = "统计模块")
@RestController
@Slf4j
@CrossOrigin
@RequestMapping("/api/v1/like")
public class LikeController{

    @Autowired
    private LikeService likeService;

    // entityType: 1:点赞
    // entityType: 2:收藏
    @PostMapping("/")
    public ApiResult<LikeVO> like(@RequestBody LikeRequest request, HttpServletRequest httpRequest) {
        String userId = (String) httpRequest.getAttribute("id");
        log.info("用户 {} 正在执行点赞操作，entityType={}, entityId={}", userId, request.getEntityType(), request.getEntityId());
        try {
            likeService.like(userId, request.getEntityType(), request.getEntityId());
            LikeVO result = buildLikeVO(userId, request.getEntityId());
            log.info("用户 {} 点赞操作成功，entityType={}, entityId={}", userId, request.getEntityType(), request.getEntityId());
            return ApiResult.success(result);
        } catch (Exception e) {
            log.error("用户 {} 点赞操作失败，entityType={}, entityId={}", userId, request.getEntityType(), request.getEntityId(), e);
            throw e;
        }
    }

    @GetMapping("/info")
    public ApiResult<LikeVO> getLikeInfo(@RequestParam String entityId, HttpServletRequest httpRequest) {
        String userId = (String) httpRequest.getAttribute("id");
        LikeVO result = buildLikeVO(userId, entityId);
        return ApiResult.success(result);
    }

    private LikeVO buildLikeVO(String userId, String entityId) {
        int likeType = RedisActionEnum.LIKE.getCode();
        int collectType = RedisActionEnum.COLLECT.getCode();

        long likeCount = likeService.findEntityLikeCount(likeType, entityId);
        int likeStatus = likeService.findEntityLikeStatus(userId, likeType, entityId);

        long collectCount = likeService.findEntityLikeCount(collectType, entityId);
        int collectStatus = likeService.findEntityLikeStatus(userId, collectType, entityId);

        LikeVO vo = new LikeVO();
        vo.setLikeCount(likeCount);
        vo.setLikeStatus(likeStatus);
        vo.setCollectCount(collectCount);
        vo.setCollectStatus(collectStatus);
        return vo;
    }
}