package com.jiaruiblog.controller;

import com.jiaruiblog.auth.Permission;
import com.jiaruiblog.auth.PermissionEnum;
import com.jiaruiblog.util.BaseApiResult;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName SystemConfigController
 * @Description TODO
 * @Author luojiarui
 * @Date 2022/12/10 11:12
 * @Version 1.0
 **/

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/system")
public class SystemConfigController {

    @Permission({PermissionEnum.ADMIN})
    @ApiOperation(value = "管理员修改系统设置", notes = "只有管理员有权限修改系统的设置信息")
    @PutMapping("config")
    public BaseApiResult systemConfig(@RequestBody String params) {
        return BaseApiResult.success();
    }

}
