package com.jiaruiblog.controller;

import com.jiaruiblog.auth.Permission;
import com.jiaruiblog.auth.PermissionEnum;
import com.jiaruiblog.common.ApiResult;
import com.jiaruiblog.common.MessageConstant;
import com.jiaruiblog.entity.Comment;
import com.jiaruiblog.entity.dto.BasePageDTO;
import com.jiaruiblog.entity.dto.BatchIdDTO;
import com.jiaruiblog.entity.dto.CommentDTO;
import com.jiaruiblog.entity.dto.CommentListDTO;
import com.jiaruiblog.service.ICommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
 * @ClassName CommentController
 * @Description 评论系统的控制器
 * @author luojiarui
 * @Date 2022/6/4 3:11 下午
 * @Version 1.0
 **/
@Tag(name = "评论模块", description = "评论相关接口")
@RestController
@Slf4j
@CrossOrigin
@RequestMapping("/comment")
public class CommentController {

    @Resource
    ICommentService commentService;

    @Operation(summary = "查询评论列表", description = "获取文档评论列表")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "请求成功", content = @Content(schema = @Schema(implementation = String.class)))
    })
    @GetMapping("queryDocReviewList")
    public ApiResult<Void> queryDocReviewList(@ModelAttribute("pageParams") BasePageDTO pageParams) {
        return ApiResult.success();
    }

    @Operation(summary = "新增单个评论", description = "添加新的评论")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "评论添加成功"),
            @ApiResponse(responseCode = "400", description = "参数错误")
    })
    @PostMapping(value = "/auth/insert")
    public ApiResult<Void> insert(@RequestBody CommentDTO commentDTO, HttpServletRequest request) {
        commentService.insert(getComment(commentDTO, request));
        return ApiResult.success();
    }

    @Operation(summary = "更新评论", description = "修改现有评论内容")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "评论更新成功"),
            @ApiResponse(responseCode = "400", description = "参数错误")
    })
    @PostMapping(value = "/auth/update")
    public ApiResult<Void> update(@RequestBody CommentDTO commentDTO, HttpServletRequest request) {
        commentService.update(getComment(commentDTO, request));
        return ApiResult.success();
    }

    @Operation(summary = "删除评论", description = "根据ID删除单个评论")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "评论删除成功"),
            @ApiResponse(responseCode = "400", description = "参数错误")
    })
    @DeleteMapping(value = "/auth/remove")
    public ApiResult<Void> remove(@RequestBody Comment comment, HttpServletRequest request) {
        String userId = (String) request.getAttribute("id");
        if (!StringUtils.hasText(comment.getId())) {
            return ApiResult.error(MessageConstant.PARAMS_ERROR_CODE, MessageConstant.PARAMS_IS_NOT_NULL);
        }
        commentService.remove(comment, userId);
        return ApiResult.success();
    }

    @Permission(value = PermissionEnum.ADMIN)
    @Operation(summary = "批量删除评论", description = "管理员批量删除评论")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "批量删除成功"),
            @ApiResponse(responseCode = "400", description = "参数错误"),
            @ApiResponse(responseCode = "403", description = "权限不足")
    })
    @DeleteMapping(value = "/auth/removeBatch")
    public ApiResult<Void> removeBatch(@RequestBody BatchIdDTO batchIdDTO) {
        List<String> commentIdList = batchIdDTO.getIds();
        if (CollectionUtils.isEmpty(commentIdList)) {
            return ApiResult.error(MessageConstant.PARAMS_ERROR_CODE, MessageConstant.PARAMS_FORMAT_ERROR);
        }
        commentService.removeBatch(commentIdList);
        return ApiResult.success();
    }

    @Operation(summary = "查询文档评论", description = "根据文档ID获取相关评论")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "请求成功",
                content = @Content(schema = @Schema(implementation = ApiResult.class))),
            @ApiResponse(responseCode = "400", description = "参数错误")
    })
    @Parameters({
            @Parameter(name = "comment", description = "评论查询参数", required = true,
                    content = @Content(schema = @Schema(implementation = CommentListDTO.class)))
    })
    @PostMapping(value = "/list")
    public ApiResult<Map<String, Object>> queryById(@RequestBody CommentListDTO comment) {
        Map<String, Object> result = commentService.queryById(comment);
        return ApiResult.success(result);
    }

    private Comment getComment(CommentDTO commentDTO, HttpServletRequest request) {
        commentDTO = Optional.ofNullable(commentDTO).orElse(new CommentDTO());
        Comment comment = new Comment();
        comment.setContent(commentDTO.getContent());
        comment.setDocId(commentDTO.getDocId());
        comment.setUserName((String) request.getAttribute("username"));
        comment.setUserId((String) request.getAttribute("id"));
        return comment;
    }

    @Operation(summary = "查询用户评论", description = "查询当前用户的评论列表")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "请求成功",
                    content = @Content(schema = @Schema(implementation = ApiResult.class))),
            @ApiResponse(responseCode = "400", description = "参数错误"),
            @ApiResponse(responseCode = "401", description = "未授权")
    })
    @Parameters({
            @Parameter(name = "pageDTO", description = "分页参数", required = true,
                    content = @Content(schema = @Schema(implementation = BasePageDTO.class))),
            @Parameter(name = "request", in = ParameterIn.HEADER, hidden = true)
    })
    @PostMapping(value = "/auth/myComments")
    public ApiResult<Map<String, Object>> queryMyComments(@RequestBody BasePageDTO pageDTO, HttpServletRequest request) {
        String userId = (String) request.getAttribute("id");
        Map<String, Object> result = commentService.queryAllComments(pageDTO, userId, false);
        return ApiResult.success(result);
    }

    @Operation(summary = "查询所有评论", description = "管理员查询所有用户的评论列表")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "请求成功",
                    content = @Content(schema = @Schema(implementation = ApiResult.class))),
            @ApiResponse(responseCode = "400", description = "参数错误"),
            @ApiResponse(responseCode = "403", description = "权限不足")
    })
    @Parameters({
            @Parameter(name = "pageDTO", description = "分页参数", required = true,
                    content = @Content(schema = @Schema(implementation = BasePageDTO.class)))
    })
    @Permission(PermissionEnum.ADMIN)
    @PostMapping(value = "/auth/allComments")
    public ApiResult<Map<String, Object>> queryAllComments(@RequestBody BasePageDTO pageDTO) {
        Map<String, Object> result = commentService.queryAllComments(pageDTO, null, true);
        return ApiResult.success(result);
    }
}
