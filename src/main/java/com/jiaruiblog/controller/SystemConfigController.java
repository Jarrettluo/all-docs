package com.jiaruiblog.controller;

import cn.hutool.core.io.IoUtil;
import com.jiaruiblog.auth.Permission;
import com.jiaruiblog.auth.PermissionEnum;
import com.jiaruiblog.common.MessageConstant;
import com.jiaruiblog.config.SystemConfig;
import com.jiaruiblog.intercepter.SensitiveFilter;
import com.jiaruiblog.intercepter.SensitiveWordInit;
import com.jiaruiblog.util.BaseApiResult;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import static com.jiaruiblog.controller.FileController.extracted;

/**
 * @ClassName SystemConfigController
 * @Description 管理员获取系统设置的配置信息，查询当前的用户配置信息
 * @Author luojiarui
 * @Date 2022/12/10 11:12
 * @Version 1.0
 **/
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/system")
public class SystemConfigController {

    @Resource
    SystemConfig systemConfig;

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
        try {
            ClassPathResource classPathResource = new ClassPathResource("static/censorword.txt");
            InputStream inputStream = classPathResource.getInputStream();
            byte[] buffer = IoUtil.readBytes(inputStream);
            extracted(response, buffer);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @ApiOperation(value = "管理员更新违禁词")
    @PostMapping(value = "updateProhibitedWord")
    public BaseApiResult updateProhibitedWord(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE, MessageConstant.PARAMS_FORMAT_ERROR);
        }
        String originFileName = file.getOriginalFilename();
        if (!ObjectUtils.nullSafeEquals(originFileName, ".txt")) {
            return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE, MessageConstant.PARAMS_FORMAT_ERROR);
        }

        try {
            Set<String> strings = SensitiveWordInit.getStrings(file.getInputStream());
            Set<String> strings1 = SensitiveWordInit.readSensitiveWordFile();
            strings1.addAll(strings);
            SensitiveFilter filter = SensitiveFilter.getInstance();
            filter.refresh();
        } catch (IOException e) {
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.OPERATE_FAILED);
        }

        return BaseApiResult.success();
    }

}
