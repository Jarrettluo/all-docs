package com.jiaruiblog.api.controller;

import com.jiaruiblog.api.auth.Permission;
import com.jiaruiblog.application.service.ICommentService;
import com.jiaruiblog.common.ApiResult;
import com.jiaruiblog.common.exception.BusinessException;
import com.jiaruiblog.common.exception.ErrorCode;
import com.jiaruiblog.domain.entity.Comment;
import com.jiaruiblog.domain.entity.dto.BasePageDTO;
import com.jiaruiblog.domain.entity.dto.BatchIdDTO;
import com.jiaruiblog.domain.entity.dto.CommentDTO;
import com.jiaruiblog.domain.entity.dto.CommentListDTO;
import com.jiaruiblog.domain.entity.vo.CommentWithUserVO;
import com.jiaruiblog.domain.entity.vo.PageVO;
import com.jiaruiblog.common.enums.PermissionEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 评论系统的控制器
 * @author luojiarui
 **/
@Tag(name = "评论模块", description = "评论相关接口")
@RestController
@Slf4j
@CrossOrigin
@RequestMapping("/api/v1/comment")
public class CommentController {

    @Resource
    ICommentService commentService;

    @Operation(summary = "新增单个评论", description = "添加新的评论")
    @PostMapping(value = "/auth/insert")
    public ApiResult<Void> insert(@RequestBody CommentDTO commentDTO, HttpServletRequest request) {
        commentService.insert(getComment(commentDTO, request));
        return ApiResult.success();
    }

    @Operation(summary = "更新评论", description = "修改现有评论内容")
    @PostMapping(value = "/auth/update")
    public ApiResult<Void> update(@RequestBody CommentDTO commentDTO, HttpServletRequest request) {
        commentService.update(getComment(commentDTO, request));
        return ApiResult.success();
    }

    @Operation(summary = "删除评论", description = "根据ID删除单个评论")
    @DeleteMapping(value = "/auth/remove")
    public ApiResult<Void> remove(@RequestBody Comment comment, HttpServletRequest request) {
        String userId = (String) request.getAttribute("id");
        if (!StringUtils.hasText(comment.getId())) {
            throw new BusinessException(ErrorCode.COMMENT_NOT_FOUND);
        }
        commentService.remove(comment, userId);
        return ApiResult.success();
    }

    @Permission(value = PermissionEnum.ADMIN)
    @Operation(summary = "批量删除评论", description = "管理员批量删除评论")
    @DeleteMapping(value = "/auth/removeBatch")
    public ApiResult<Void> removeBatch(@RequestBody BatchIdDTO batchIdDTO) {
        List<String> commentIdList = batchIdDTO.getIds();
        if (CollectionUtils.isEmpty(commentIdList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        commentService.removeBatch(commentIdList);
        return ApiResult.success();
    }

    @Operation(summary = "查询文档评论", description = "根据文档ID获取相关评论")
    @PostMapping(value = "/list")
    public ApiResult<Map<String, Object>> queryById(@RequestBody CommentListDTO comment) {
        Map<String, Object> result = commentService.queryById(comment);
        return ApiResult.success(result);
    }

    private Comment getComment(CommentDTO commentDTO, HttpServletRequest request) {
        commentDTO = Optional.ofNullable(commentDTO).orElseThrow(() ->
                new BusinessException(ErrorCode.PARAMS_ERROR));
        if (!StringUtils.hasText(commentDTO.getContent()) || !StringUtils.hasText(commentDTO.getDocId())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Comment comment = new Comment();
        comment.setContent(commentDTO.getContent());
        comment.setDocId(commentDTO.getDocId());
        comment.setUserName((String) request.getAttribute("username"));
        comment.setUserId((String) request.getAttribute("id"));
        return comment;
    }

    @Operation(summary = "查询用户评论", description = "查询当前用户的评论列表")
    @PostMapping(value = "/auth/myComments")
    public ApiResult<PageVO<CommentWithUserVO>> queryMyComments(@RequestBody BasePageDTO pageDTO, HttpServletRequest request) {
        String userId = (String) request.getAttribute("id");
        PageVO<CommentWithUserVO> result = commentService.queryAllComments(pageDTO, userId, false);
        return ApiResult.success(result);
    }

    @Operation(summary = "查询所有评论", description = "管理员查询所有用户的评论列表")
    @Permission(PermissionEnum.ADMIN)
    @PostMapping(value = "/auth/allComments")
    public ApiResult<PageVO<CommentWithUserVO>> queryAllComments(@RequestBody BasePageDTO pageDTO) {
        PageVO<CommentWithUserVO> result = commentService.queryAllComments(pageDTO, null, true);
        return ApiResult.success(result);
    }
}