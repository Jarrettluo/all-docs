package com.jiaruiblog.api.controller;

import cn.hutool.core.io.IoUtil;
import com.jiaruiblog.api.auth.Permission;
import com.jiaruiblog.common.enums.PermissionEnum;
import com.jiaruiblog.common.ApiResult;
import com.jiaruiblog.infrastructure.config.SystemConfig;
import com.jiaruiblog.common.exception.BusinessException;
import com.jiaruiblog.common.exception.BusinessExceptionBuilder;
import com.jiaruiblog.common.exception.ErrorCode;
import com.jiaruiblog.api.intercepter.SensitiveFilter;
import com.jiaruiblog.api.intercepter.SensitiveWordInit;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.jiaruiblog.api.controller.FileController.extracted;

@Tag(name = "SystemConfigController", description = "系统设置模块")
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/api/v1/system")
public class SystemConfigController {

    public static final String STATIC_CENSOR_WORD_TXT = "static" + File.separator + "censorWord.txt";

    @Resource
    SystemConfig systemConfig;

    @Value("${all-docs.file-path.sensitive-file}")
    private String userDefinePath;

    @Permission(PermissionEnum.ADMIN)
    @GetMapping("getConfig")
    public ApiResult<SystemConfig> getSystemConfig() {
        return ApiResult.success(systemConfig);
    }

    @Permission({PermissionEnum.ADMIN})
    @Operation(summary = "管理员修改系统设置", description = "只有管理员有权限修改系统的设置信息")
    @PutMapping("updateConfig")
    public ApiResult<SystemConfig> systemConfig(
            @Parameter(description = "系统配置参数", required = true,
                    content = @Content(schema = @Schema(implementation = SystemConfig.class)))
            @RequestBody SystemConfig userSetting) {
        if (userSetting.getUserUpload() == null || userSetting.getUserRegistry() == null
                || userSetting.getAdminReview() == null || userSetting.getProhibitedWord() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        systemConfig.setUserUpload(userSetting.getUserUpload());
        systemConfig.setProhibitedWord(userSetting.getProhibitedWord());
        systemConfig.setUserRegistry(userSetting.getUserRegistry());
        systemConfig.setAdminReview(userSetting.getAdminReview());
        return ApiResult.success(userSetting);
    }

    @Operation(summary = "管理员下载最新的违禁词", description = "下载系统当前使用的违禁词列表")
    @ApiResponse(responseCode = "200", description = "下载成功",
            content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE))
    @ApiResponse(responseCode = "500", description = "服务器内部错误")
    @GetMapping(value = "getProhibitedWord", produces = MediaType.TEXT_PLAIN_VALUE)
    public void downloadTxt(HttpServletResponse response) {
        File file = new File(userDefinePath);
        try {
            if (file.exists()) {
                byte[] buffer = IoUtil.readBytes(new FileInputStream(file));
                extracted(response, buffer);
            } else {
                ClassPathResource classPathResource = new ClassPathResource(STATIC_CENSOR_WORD_TXT);
                try (InputStream inputStream = classPathResource.getInputStream()) {
                    byte[] buffer = IoUtil.readBytes(inputStream);
                    extracted(response, buffer);
                }
            }
        } catch (IOException ex) {
            log.error("下载最新的违禁词错误", ex);
        }
    }

    @Operation(summary = "管理员更新违禁词", description = "上传新的违禁词列表文件")
    @PostMapping(value = "updateProhibitedWord")
    public ApiResult<Object> updateProhibitedWord(
            @Parameter(description = "违禁词文件", required = true,
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
            @RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty() || file.getSize() > 20000) {
            throw BusinessExceptionBuilder.of(ErrorCode.PARAMS_ERROR).build();
        }
        String originFileName = file.getOriginalFilename();
        originFileName = Optional.ofNullable(originFileName).orElse("");
        String suffix = originFileName.substring(originFileName.lastIndexOf(".") + 1).toLowerCase(Locale.ROOT);
        if (!ObjectUtils.nullSafeEquals(suffix, "txt")) {
            throw BusinessExceptionBuilder.of(ErrorCode.PARAMS_ERROR).build();
        }

        try {
            Set<String> strings = SensitiveWordInit.getStrings(file.getInputStream(), StandardCharsets.UTF_8);
            writeToFile(strings);
            SensitiveFilter filter = SensitiveFilter.getInstance();
            filter.refresh();
        } catch (IOException e) {
            log.error("管理员更新违禁词错误", e.getCause());
            throw BusinessExceptionBuilder.of(ErrorCode.OPERATE_FAILED).build();
        }

        return ApiResult.success();
    }
    private void writeToFile(Set<String> strSet) throws IOException {
        String txt = strSet.stream().limit(10000).collect(Collectors.joining("\n"));
        String replacedTxt = txt.replace(" ", "");

        FileOutputStream fileOutputStream = new FileOutputStream(userDefinePath);
        try (OutputStreamWriter out = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8)) {
            out.write(replacedTxt);
            out.flush();
        } catch (IOException e) {
            log.error("写入文件错误", e);
        }
    }
}