package com.jiaruiblog.api.controller;

import com.jiaruiblog.api.auth.Permission;
import com.jiaruiblog.common.enums.PermissionEnum;
import com.jiaruiblog.common.ApiResult;
import com.jiaruiblog.domain.entity.po.DocLog;
import com.jiaruiblog.domain.entity.dto.BasePageDTO;
import com.jiaruiblog.domain.entity.dto.BatchIdDTO;
import com.jiaruiblog.domain.entity.vo.DocLogVO;
import com.jiaruiblog.common.exception.BusinessException;
import com.jiaruiblog.common.exception.ErrorCode;
import com.jiaruiblog.application.service.IDocLogService;
import com.jiaruiblog.application.transformer.PO2VOConverter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 文档日志的查询等
 *
 * @author luojiarui
 **/
@Tag(name = "文档日志模块", description = "文档操作日志相关接口")
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/api/v1/docLog")
public class DocLogController {

    @Resource
    private IDocLogService docLogService;

    /**
     * @return com.jiaruiblog.util.BaseApiResult
     * @author luojiarui
     **/
    @Permission({PermissionEnum.ADMIN})
    @Operation(
            summary = "管理员查询系统日志信息",
            description = "只有管理员有权限查询日志列表",
            parameters = {
                    @Parameter(name = "pageParams", description = "分页参数", required = true,
                            content = @Content(schema = @Schema(implementation = BasePageDTO.class)))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "成功响应",
                            content = @Content(schema = @Schema(implementation = Map.class))),
                    @ApiResponse(responseCode = "403", description = "无权限访问")
            }
    )
    @GetMapping("queryLogList")
    public ApiResult<Map<String, Object>> queryLogList(@ModelAttribute("pageParams") @Valid BasePageDTO pageParams) {
        Map<String, Object> result = docLogService.queryDocLogs(pageParams);
        if (result.get("data") instanceof List<?>) {
            List<DocLog> docLogList = (List<DocLog>) result.get("data");
            List<DocLogVO> docLogVOS = PO2VOConverter.docLogListConvert(docLogList);
            result.put("data", docLogVOS);
        }
        return ApiResult.success(result);
    }

    /**
     * @return com.jiaruiblog.util.BaseApiResult
     * @author luojiarui
     **/
    @Permission(PermissionEnum.ADMIN)
    @Operation(
            summary = "管理员删除文档信息",
            description = "只有管理员有权限删除文档的日志",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "批量ID参数",
                    required = true,
                    content = @Content(schema = @Schema(implementation = BatchIdDTO.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "删除成功"),
                    @ApiResponse(responseCode = "400", description = "参数错误"),
                    @ApiResponse(responseCode = "403", description = "无权限访问")
            }
    )
    @DeleteMapping("removeLog")
    public ApiResult<Void> removeLog(@RequestBody @Valid BatchIdDTO batchIdDTO, HttpServletRequest request) {
        List<String> logIds = batchIdDTO.getIds();
        if (CollectionUtils.isEmpty(logIds)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        docLogService.deleteDocLogBatch(logIds, (String) request.getAttribute("id"));
        return ApiResult.success();
    }

}