package com.jiaruiblog.api.controller;

import com.jiaruiblog.api.auth.Permission;
import com.jiaruiblog.common.enums.PermissionEnum;
import com.jiaruiblog.common.ApiResult;
import com.jiaruiblog.domain.entity.FileDocument;
import com.jiaruiblog.domain.entity.User;
import com.jiaruiblog.domain.entity.dto.DocumentDTO;
import com.jiaruiblog.domain.entity.dto.RemoveObjectDTO;
import com.jiaruiblog.domain.entity.dto.document.UpdateInfoDTO;
import com.jiaruiblog.domain.entity.vo.DocWithCateVO;
import com.jiaruiblog.domain.entity.vo.PageVO;
import com.jiaruiblog.common.enums.FilterTypeEnum;
import com.jiaruiblog.common.exception.BusinessException;
import com.jiaruiblog.common.exception.ErrorCode;
import com.jiaruiblog.api.intercepter.SensitiveFilter;
import com.jiaruiblog.application.service.IDocLogService;
import com.jiaruiblog.application.service.DocumentService;
import com.jiaruiblog.application.service.RedisService;
import com.jiaruiblog.application.service.impl.DocLogServiceImpl;
import com.jiaruiblog.application.service.impl.RedisServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * 文档查询删除控制器
 * @author luojiarui
 **/
@Schema(description = "文档模块")
@RestController
@Slf4j
@CrossOrigin
@RequestMapping("/api/v1/document")
public class DocumentController {

    @Resource
    DocumentService documentService;

    @Resource
    RedisService redisService;

    @Resource
    IDocLogService docLogService;


    @Operation(summary = "2.1 查询文档的分页列表页", description = "根据参数查询文档列表")
    @PostMapping(value = "/list")
    public ApiResult<Object> list(@RequestBody @Schema(description = "文档查询DTO") DocumentDTO documentDTO)
            throws IOException {
        String userId = documentDTO.getUserId();
        if (StringUtils.hasText(documentDTO.getFilterWord()) &&
                documentDTO.getType() == FilterTypeEnum.FILTER) {
            String filterWord = documentDTO.getFilterWord();
            //非法敏感词汇判断
            SensitiveFilter filter = SensitiveFilter.getInstance();
            int n = filter.checkSensitiveWord(filterWord, 0, 1);
            //存在非法字符
            if (n > 0) {
                log.error("这个人输入了非法字符--> {},不知道他到底要查什么~", filterWord);
            } else {
                redisService.incrementScoreByUserId(filterWord, RedisServiceImpl.SEARCH_KEY);
                if (StringUtils.hasText(userId)) {
                    redisService.addSearchHistoryByUserId(userId, filterWord);
                }
            }
        }
        return ApiResult.success(documentService.list(documentDTO));
    }

    @Operation(summary = "2.2 查询文档的详细信息", description = "查询文档的详细信息")
    @GetMapping(value = "/detail")
    public ApiResult<Object> detail(
            @RequestParam(value = "docId")
            @Schema(description = "文档ID") String id) {
        return ApiResult.success(documentService.detail(id));
    }

    @Operation(summary = "3.2 删除某个文档", description = "删除某个文档")
    @DeleteMapping(value = "/auth/remove")
    @Permission(value = PermissionEnum.ADMIN)
    public ApiResult<Object> remove(
            @RequestBody @Schema(description = "文档删除DTO") RemoveObjectDTO removeObjectDTO,
            HttpServletRequest request) {
        FileDocument fileDocument = documentService.queryById(removeObjectDTO.getId());
        if (fileDocument == null) {
            throw new BusinessException(ErrorCode.DOCUMENT_NOT_FOUND);
        }
        String username = (String) request.getAttribute("username");
        String userId = (String) request.getAttribute("id");
        User user = new User();
        user.setUsername(username);
        user.setId(userId);
        docLogService.addLog(user, fileDocument, DocLogServiceImpl.Action.DELETE);
        documentService.remove(fileDocument);
        return ApiResult.success();
    }

    @Operation(summary = "3.2 管理员修改文档基本信息", description = "管理员修改某个文档信息")
    @PutMapping(value="/auth/updateInfo")
    @Permission(value = PermissionEnum.ADMIN)
    public ApiResult<Object> updateInfo(@RequestBody @Schema(description = "文档更新信息DTO") UpdateInfoDTO updateInfoDTO) {
        documentService.updateInfo(updateInfoDTO);
        return ApiResult.success();
    }


    @Operation(summary = "2.3 指定分类时，查询文档的分页列表页", description = "根据参数查询文档列表")
    @GetMapping(value = "/listWithCategory")
    public ApiResult<PageVO<DocWithCateVO>> listWithCategory(
            @ModelAttribute("documentDTO")
            @Schema(description = "文档查询DTO", required = true) DocumentDTO documentDTO) {
        FilterTypeEnum filterType = documentDTO.getType();
        if (filterType.equals(FilterTypeEnum.CATEGORY) || filterType.equals(FilterTypeEnum.TAG)) {
            return ApiResult.success(documentService.listWithCategory(documentDTO));
        } else {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
    }

    @GetMapping("/addKey")
    public ApiResult<Object> addKey(@RequestParam("key") String key) {

        redisService.addSearchHistoryByUserId("ljr", key);
        log.info("added key: {}", key);
        redisService.incrementScoreByUserId(key, RedisServiceImpl.SEARCH_KEY);
        return ApiResult.success(key);
    }

    @GetMapping("/keyList")
    public ApiResult<Object> keyList() {
        List<String> keyList = redisService.getSearchHistoryByUserId("ljr");
        return ApiResult.success(keyList);
    }


    @GetMapping("/hot")
    public ApiResult<Object> hot() {
        List<String> keyList = redisService.getHotList(null, RedisServiceImpl.SEARCH_KEY);
        return ApiResult.success(keyList);
    }
}