package com.jiaruiblog.api.controller;

import com.jiaruiblog.api.auth.Permission;
import com.jiaruiblog.common.enums.PermissionEnum;
import com.jiaruiblog.common.ApiResult;
import com.jiaruiblog.domain.entity.DocReview;
import com.jiaruiblog.domain.entity.FileDocument;
import com.jiaruiblog.domain.entity.dto.BasePageDTO;
import com.jiaruiblog.domain.entity.dto.BatchIdDTO;
import com.jiaruiblog.domain.entity.dto.RefuseBatchDTO;
import com.jiaruiblog.domain.entity.dto.RefuseDTO;
import com.jiaruiblog.domain.entity.vo.PageVO;
import com.jiaruiblog.common.exception.BusinessException;
import com.jiaruiblog.common.exception.BusinessExceptionBuilder;
import com.jiaruiblog.common.exception.ErrorCode;
import com.jiaruiblog.application.service.DocReviewService;
import com.jiaruiblog.application.service.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 文档评审，日志查询
 *
 * @author Jarrett Luo
 */
@Tag(name = "文档评审模块")
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/api/v1/doc-review")
public class DocReviewController {

    @Resource
    private DocReviewService docReviewService;

    @Resource
    private DocumentService fileService;

    /**
     * 普通用户、管理员用户，列表查询
     * 该接口必须在登录条件下才能查询
     * 普通普通查询到自己上传的文档
     * 管理员查询到所有的文档评审信息
     * 必须是管理员才有资格进行评审
     *
     * @return BaseApiResult
     */
    @Permission({PermissionEnum.ADMIN})
    @Operation(summary = "查询需要评审的文档列表", description = "管理员可以查询所有需要评审的文档列表")
    @GetMapping("queryDocForReview")
    public ApiResult<Object> queryDocReviewList(@Parameter(description = "分页参数") @ModelAttribute("pageParams") @Valid BasePageDTO pageParams) {
        Map<String, Object> result = fileService.queryFileDocumentResult(pageParams, true);
        return ApiResult.success(result);
    }


    /**
     * 修改已读， 只有普通用户有此权限
     * 修改评审意见为通过
     * 用户必须是文档的上传人
     *
     * @return BaseApiResult
     */
    @Permission({PermissionEnum.ADMIN, PermissionEnum.USER})
    @Operation(summary = "修改已读", description = "修改已读状态，普通用户可以标记自己上传的文档为已读")
    @PutMapping("userRead")
    public ApiResult<Object> updateDocReview(@Parameter(description = "批量ID参数") @RequestBody @Valid BatchIdDTO batchIdDTO,
                                             @Parameter(hidden = true) HttpServletRequest request) {
        String userId = (String) request.getAttribute("id");
        docReviewService.userRead(batchIdDTO.getIds(), userId);
        return ApiResult.success(null);
    }

    /**
     * @return com.jiaruiblog.util.BaseApiResult
     * @author luojiarui
     **/
    @Permission({PermissionEnum.ADMIN})
    @Operation(summary = "管理员拒绝某个文档", description = "管理员拒绝某个文档，只有管理员有操作该文档的权限")
    @PostMapping("refuse")
    public ApiResult<Void> refuse(@Parameter(description = "拒绝参数") @RequestBody @Validated RefuseDTO refuseDTO) {
        String docId = refuseDTO.getDocId();
        String reason = refuseDTO.getReason();
        if (docReviewService.docIdExist(Collections.singletonList(docId))) {
            throw BusinessExceptionBuilder.of(ErrorCode.DOCUMENT_NOT_FOUND).build();
        }
        // 校验某个文档是否存在, 查询并删除某个文档
        FileDocument fileDocument = fileService.queryById(docId);
        fileService.queryAndRemove(docId);
        if (Objects.isNull(fileDocument)) {
            throw BusinessExceptionBuilder.of(ErrorCode.DOCUMENT_NOT_FOUND).build();
        }
        docReviewService.refuse(fileDocument, reason);
        return ApiResult.success();
    }

    /**
     * @return com.jiaruiblog.util.BaseApiResult
     * @author luojiarui
     **/
    @Permission({PermissionEnum.ADMIN})
    @Operation(summary = "管理员拒绝一批文档", description = "管理员拒绝一批文档，只有管理员有操作该文档的权限")
    @PostMapping("refuseBatch")
    public ApiResult<Object> refuseBatch(@Parameter(description = "批量拒绝参数") @RequestBody @Valid RefuseBatchDTO refuseBatchDTO) {
        List<String> docIds = refuseBatchDTO.getIds();
        String reason = refuseBatchDTO.getReason();
        if (docReviewService.docIdExist(docIds)) {
            throw new BusinessException(ErrorCode.OPERATE_FAILED);
        }
        String[] array = docIds.toArray(new String[0]);
        List<FileDocument> fileDocumentList = fileService.queryAndRemove(array);
        if (CollectionUtils.isEmpty(fileDocumentList)) {
            throw new BusinessException(ErrorCode.OPERATE_FAILED);
        }
        docReviewService.refuseBatch(fileDocumentList, reason);
        return ApiResult.success("success");
    }

    /**
     * @author luojiarui
     **/
    @Permission({PermissionEnum.ADMIN})
    @Operation(summary = "同意某一批文档", description = "管理员同意某一批文档")
    @PostMapping("approve")
    public ApiResult<Object> approve(@Parameter(description = "批量ID参数") @RequestBody @Valid BatchIdDTO batchIdDTO) {
        List<String> docIds = batchIdDTO.getIds();
        if (docReviewService.docIdExist(docIds)) {
            throw new BusinessException(ErrorCode.OPERATE_FAILED);
        }
        List<FileDocument> fileDocumentList = fileService.queryAndUpdate(docIds.toArray(new String[0]));
        if (CollectionUtils.isEmpty(fileDocumentList)) {
            throw new BusinessException(ErrorCode.OPERATE_FAILED);
        }
        docReviewService.approveBatch(fileDocumentList);
        return ApiResult.success("success");
    }

    /**
     * @return com.jiaruiblog.util.BaseApiResult
     * @author luojiarui
     **/
    @Permission({PermissionEnum.ADMIN})
    @Operation(summary = "管理员和普通用户分别查询数据", description = "查询文档审批的列表")
    @GetMapping("queryReviewResultList")
    public ApiResult<PageVO<DocReview>> queryReviewResultList(@Parameter(description = "分页参数") @ModelAttribute("pageParams") @Valid BasePageDTO pageParams) {
        return ApiResult.success(docReviewService.queryReviewLog(pageParams, null, true));
    }

    /**
     * @return com.jiaruiblog.util.BaseApiResult
     * @author luojiarui
     **/
    @Permission({PermissionEnum.USER, PermissionEnum.ADMIN})
    @Operation(summary = "管理员和普通用户分别查询数据", description = "查询文档审批的列表")
    @GetMapping("queryMyReviewResultList")
    public ApiResult<PageVO<DocReview>> queryMyReviewResultList(@Parameter(description = "分页参数")
                                                                @ModelAttribute("pageParams")
                                                                @Valid BasePageDTO pageParams,
                                                                @Parameter(hidden = true) HttpServletRequest request) {
        return ApiResult.success(docReviewService.queryReviewLog(pageParams, (String) request.getAttribute("id"), false));
    }

    /**
     * 普通用户删除，管理员删除，删除评审日志
     *
     * @return BaseApiResult
     */
    @Operation(summary = "删除评审日志", description = "管理员和普通用户都可以删除评审结果")
    @DeleteMapping("removeDocReview")
    public ApiResult<Void> removeDocReview(@Parameter(description = "批量ID参数") @RequestBody
                                           @Valid BatchIdDTO batchIdDTO,
                                           @Parameter(hidden = true) HttpServletRequest request) {
        if (CollectionUtils.isEmpty(batchIdDTO.getIds())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        docReviewService.deleteReviewsBatch(batchIdDTO.getIds(), (String) request.getAttribute("id"));
        return ApiResult.success();
    }

}