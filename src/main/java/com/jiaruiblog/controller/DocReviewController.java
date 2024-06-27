package com.jiaruiblog.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.jiaruiblog.auth.Permission;
import com.jiaruiblog.auth.PermissionEnum;
import com.jiaruiblog.common.MessageConstant;
import com.jiaruiblog.entity.dto.BasePageDTO;
import com.jiaruiblog.entity.dto.BatchIdDTO;
import com.jiaruiblog.entity.dto.RefuseBatchDTO;
import com.jiaruiblog.entity.dto.RefuseDTO;
import com.jiaruiblog.service.DocReviewService;
import com.jiaruiblog.service.IFileService;
import com.jiaruiblog.util.BaseApiResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * 文档评审，日志查询
 *
 * @Author Jarrett Luo
 * @Date 2022/11/25 15:56
 * @Version 1.0
 */
@Api(tags = "文档评审模块")
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/docReview")
public class DocReviewController {

    @Resource
    private DocReviewService docReviewService;

    @Resource
    private IFileService fileService;

    /**
     * 普通用户、管理员用户，列表查询
     * 该接口必须在登录条件下才能查询
     * 普通普通查询到自己上传的文档
     * 管理员查询到所有的文档评审信息
     * 必须是管理员才有资格进行评审
     *
     * @return BaseApiResult
     */
    // @Permission({PermissionEnum.ADMIN})
    @ApiOperation(value = "查询需要评审的文档列表", notes = "查询需要评审的文档列表")
    @GetMapping("queryDocForReview")
    public BaseApiResult queryDocReviewList(@ModelAttribute("pageParams") @Valid BasePageDTO pageParams) {
        if (!StpUtil.hasPermission("doc.view.query")) {
            return BaseApiResult.error(MessageConstant.AUTH_ERROR_CODE, MessageConstant.NOT_PERMISSION);
        }
        return fileService.queryFileDocumentResult(pageParams, true);
    }


    /**
     * 修改已读， 只有普通用户有此权限
     * 修改评审意见为通过
     * 用户必须是文档的上传人
     *
     * @return BaseApiResult
     */
    // @Permission({PermissionEnum.ADMIN, PermissionEnum.USER})
    @ApiOperation(value = "修改已读", notes = "修改已读功能只有普通用户有此权限")
    @PutMapping("userRead")
    public BaseApiResult updateDocReview(@RequestBody @Valid BatchIdDTO batchIdDTO, HttpServletRequest request) {
        if (!StpUtil.hasPermission("doc.view.status")) {
            return BaseApiResult.error(MessageConstant.AUTH_ERROR_CODE, MessageConstant.NOT_PERMISSION);
        }

        String userId = (String) request.getAttribute("id");
        return docReviewService.userRead(batchIdDTO.getIds(), userId);
    }

    /**
     * @return com.jiaruiblog.util.BaseApiResult
     * @Author luojiarui
     * @Description 单个进行拒绝
     * @Date 21:12 2022/11/30
     * @Param [docId, reason]
     **/
    // @Permission({PermissionEnum.ADMIN})
    @ApiOperation(value = "管理员拒绝某个文档", notes = "管理员拒绝某个文档，只有管理员有操作该文档的权限")
    @PostMapping("refuse")
    public BaseApiResult refuse(@RequestBody @Validated RefuseDTO refuseDTO) {
        if (!StpUtil.hasPermission("doc.view.refuse")) {
            return BaseApiResult.error(MessageConstant.AUTH_ERROR_CODE, MessageConstant.NOT_PERMISSION);
        }
        String docId = refuseDTO.getDocId();
        String reason = refuseDTO.getReason();
        return docReviewService.refuse(docId, reason);
    }

    /**
     * @return com.jiaruiblog.util.BaseApiResult
     * @Author luojiarui
     * @Description 批量进行拒绝，并删除文档
     * @Date 21:12 2022/11/30
     * @Param [docIds]
     **/
    // @Permission({PermissionEnum.ADMIN})
    @ApiOperation(value = "管理员拒绝一批文档", notes = "管理员拒绝一批文档，只有管理员有操作该文档的权限")
    @PostMapping("refuseBatch")
    public BaseApiResult refuseBatch(@RequestBody @Valid RefuseBatchDTO refuseBatchDTO) {
        if (!StpUtil.hasPermission("doc.view.refuse")) {
            return BaseApiResult.error(MessageConstant.AUTH_ERROR_CODE, MessageConstant.NOT_PERMISSION);
        }
        return docReviewService.refuseBatch(refuseBatchDTO.getIds(), refuseBatchDTO.getReason());
    }

    /**
     * @Author luojiarui
     * @Description  缺少同意文档的信息
     * @Date 22:04 2022/12/9
     * @Param [batchIdDTO]
     * @return com.jiaruiblog.util.BaseApiResult
     **/
    @Permission({PermissionEnum.ADMIN})
    @ApiOperation(value = "同意某一批文档", notes = "管理员同意某一批文档")
    @PostMapping("approve")
    public BaseApiResult approve(@RequestBody @Valid BatchIdDTO batchIdDTO) {
        if (!StpUtil.hasPermission("doc.view.approve")) {
            return BaseApiResult.error(MessageConstant.AUTH_ERROR_CODE, MessageConstant.NOT_PERMISSION);
        }
        return docReviewService.approveBatch(batchIdDTO.getIds());
    }

    /**
     * @return com.jiaruiblog.util.BaseApiResult
     * @Author luojiarui
     * @Description 管理员和普通用户分别查询
     * @Date 21:15 2022/11/30
     * @Param [pageParams, request]
     **/
    @Permission({PermissionEnum.ADMIN})
    @ApiOperation(value = "管理员和普通用户分别查询数据", notes = "查询文档审批的列表")
    @GetMapping("queryReviewResultList")
    public BaseApiResult queryReviewResultList(@ModelAttribute("pageParams") @Valid BasePageDTO pageParams) {
        if (!StpUtil.hasPermission("doc.view.query")) {
            return BaseApiResult.error(MessageConstant.AUTH_ERROR_CODE, MessageConstant.NOT_PERMISSION);
        }
        return docReviewService.queryReviewLog(pageParams, null, true);
    }

    /**
     * @return com.jiaruiblog.util.BaseApiResult
     * @Author luojiarui
     * @Description 管理员和普通用户分别查询
     * @Date 21:15 2022/11/30
     * @Param [pageParams, request]
     **/
    // @Permission({PermissionEnum.USER, PermissionEnum.ADMIN})
    @ApiOperation(value = "管理员和普通用户分别查询数据", notes = "查询文档审批的列表")
    @GetMapping("queryMyReviewResultList")
    public BaseApiResult queryMyReviewResultList(@ModelAttribute("pageParams") @Valid BasePageDTO pageParams,
                                               HttpServletRequest request) {
        if (!StpUtil.hasPermission("doc.view.query")) {
            return BaseApiResult.error(MessageConstant.AUTH_ERROR_CODE, MessageConstant.NOT_PERMISSION);
        }
        return docReviewService.queryReviewLog(pageParams, (String) request.getAttribute("id"), false);
    }


    /**
     * 普通用户删除，管理员删除，删除评审日志
     * @return BaseApiResult
     */
    @ApiOperation(value = "删除评审日志", notes = "管理员和普通用户都可以删除评审结果")
    @DeleteMapping("removeDocReview")
    public BaseApiResult removeDocReview(@RequestBody @Valid BatchIdDTO batchIdDTO, HttpServletRequest request) {
        if (!StpUtil.hasPermission("doc.view.log.remove")) {
            return BaseApiResult.error(MessageConstant.AUTH_ERROR_CODE, MessageConstant.NOT_PERMISSION);
        }
        if (CollectionUtils.isEmpty(batchIdDTO.getIds())) {
            return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE, MessageConstant.PARAMS_IS_NOT_NULL);
        }
        return docReviewService.deleteReviewsBatch(batchIdDTO.getIds(), (String) request.getAttribute("id"));
    }


}
