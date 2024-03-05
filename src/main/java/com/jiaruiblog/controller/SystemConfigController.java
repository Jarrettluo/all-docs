package com.jiaruiblog.controller;

import cn.hutool.core.io.IoUtil;
import com.jiaruiblog.auth.Permission;
import com.jiaruiblog.auth.PermissionEnum;
import com.jiaruiblog.common.MessageConstant;
import com.jiaruiblog.config.SystemConfig;
import com.jiaruiblog.intercepter.SensitiveFilter;
import com.jiaruiblog.intercepter.SensitiveWordInit;
import com.jiaruiblog.util.BaseApiResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.jiaruiblog.controller.FileController.extracted;

/**
 * @ClassName SystemConfigController
 * @Description 管理员获取系统设置的配置信息，查询当前的用户配置信息
 * @Author luojiarui
 * @Date 2022/12/10 11:12
 * @Version 1.0
 **/
@Api(tags = "系统设置模块")
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/system")
public class SystemConfigController {

    public static final String STATIC_CENSOR_WORD_TXT = "static" + File.separator + "censorWord.txt";

    @Resource
    SystemConfig systemConfig;

    @Value("${all-docs.file-path.sensitive-file}")
    private String userDefinePath;

    @Permission(PermissionEnum.ADMIN)
    @GetMapping("getConfig")
    @ApiOperation(value = "管理员获取系统设置", notes = "只有管理员有权限修改系统的设置信息")
    public BaseApiResult getSystemConfig() {
        return BaseApiResult.success(systemConfig);
    }

    @Permission({PermissionEnum.ADMIN})
    @ApiOperation(value = "管理员修改系统设置", notes = "只有管理员有权限修改系统的设置信息")
    @PutMapping("updateConfig")
    public BaseApiResult systemConfig(@RequestBody SystemConfig userSetting) {
        if (userSetting.getUserUpload() == null || userSetting.getUserRegistry() == null
                || userSetting.getAdminReview() == null || userSetting.getProhibitedWord() == null) {
            return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE, MessageConstant.PARAMS_FORMAT_ERROR);
        }
        systemConfig.setUserUpload(userSetting.getUserUpload());
        systemConfig.setProhibitedWord(userSetting.getProhibitedWord());
        systemConfig.setUserRegistry(userSetting.getUserRegistry());
        systemConfig.setAdminReview(userSetting.getAdminReview());
        return BaseApiResult.success(userSetting);
    }

    @ApiOperation(value = "管理员下载最新的违禁词")
    @GetMapping(value = "getProhibitedWord", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public void downloadTxt(HttpServletResponse response) {
        File file = new File(userDefinePath);
        try {
            if (file.exists()) {
                byte[] buffer = IoUtil.readBytes(new FileInputStream(file));
                extracted(response, buffer);
            } else {
                ClassPathResource classPathResource = new ClassPathResource(STATIC_CENSOR_WORD_TXT);
                InputStream inputStream = classPathResource.getInputStream();
                byte[] buffer = IoUtil.readBytes(inputStream);
                extracted(response, buffer);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @ApiOperation(value = "管理员更新违禁词")
    @PostMapping(value = "updateProhibitedWord")
    public BaseApiResult updateProhibitedWord(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty() || file.getSize() > 20000) {
            return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE, MessageConstant.PARAMS_FORMAT_ERROR);
        }
        String originFileName = file.getOriginalFilename();
        originFileName = Optional.ofNullable(originFileName).orElse("");
        String suffix = originFileName.substring(originFileName.lastIndexOf(".") + 1).toLowerCase(Locale.ROOT);
        if (!ObjectUtils.nullSafeEquals(suffix, "txt")) {
            return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE, MessageConstant.PARAMS_FORMAT_ERROR);
        }

        try {
            Set<String> strings = SensitiveWordInit.getStrings(file.getInputStream(), StandardCharsets.UTF_8);
            writeToFile(strings);
            SensitiveFilter filter = SensitiveFilter.getInstance();
            filter.refresh();
        } catch (IOException e) {
            e.printStackTrace();
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.OPERATE_FAILED);
        }

        return BaseApiResult.success();
    }

    private void writeToFile(Set<String> strSet) throws IOException {
        String txt = strSet.stream().limit(10000).collect(Collectors.joining("\n"));
        String replacedTxt = txt.replace(" ", "");

        FileOutputStream fileOutputStream = new FileOutputStream(userDefinePath);
        try (OutputStreamWriter out = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8)) {
            out.write(replacedTxt);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}