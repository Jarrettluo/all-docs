package com.jiaruiblog.controller;

import com.jiaruiblog.auth.Permission;
import com.jiaruiblog.auth.PermissionEnum;
import com.jiaruiblog.common.MessageConstant;
import com.jiaruiblog.entity.DocLog;
import com.jiaruiblog.entity.dto.BasePageDTO;
import com.jiaruiblog.entity.dto.BatchIdDTO;
import com.jiaruiblog.entity.vo.DocLogVO;
import com.jiaruiblog.service.IDocLogService;
import com.jiaruiblog.transformer.PO2VOConverter;
import com.jiaruiblog.util.BaseApiResult;
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
 * @ClassName DocLogController
 * @Description 文档日志的查询等
 * @author luojiarui
 * @Date 2022/12/10 11:10
 * @Version 1.0
 **/
@Tag(name = "文档日志模块", description = "文档操作日志相关接口")
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/docLog")
public class DocLogController {

    @Resource
    private IDocLogService docLogService;

    /**
     * @return com.jiaruiblog.util.BaseApiResult
     * @author luojiarui
     * @Description 系统用户日志查询
     * @Date 21:16 2022/11/30
     * @Param [pageParams]
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
    public BaseApiResult queryLogList(@ModelAttribute("pageParams") @Valid BasePageDTO pageParams) {
        Map<String, Object> result = docLogService.queryDocLogs(pageParams);
        if (result.get("data") instanceof List<?>) {
            List<DocLog> docLogList = (List<DocLog>) result.get("data");
            List<DocLogVO> docLogVOS = PO2VOConverter.docLogListConvert(docLogList);
            result.put("data", docLogVOS);
        }
        return BaseApiResult.success(result);
    }

    /**
     * @return com.jiaruiblog.util.BaseApiResult
     * @author luojiarui
     * @Description 删除用户日志
     * @Date 21:16 2022/11/30
     * @Param [logIds]
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
    public BaseApiResult removeLog(@RequestBody @Valid BatchIdDTO batchIdDTO, HttpServletRequest request) {
        List<String> logIds = batchIdDTO.getIds();
        if (CollectionUtils.isEmpty(logIds)) {
            return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE, MessageConstant.PARAMS_IS_NOT_NULL);
        }
        return docLogService.deleteDocLogBatch(logIds, (String) request.getAttribute("id"));
    }

}
