package com.jiaruiblog.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.jiaruiblog.common.MessageConstant;
import com.jiaruiblog.service.IPermissionService;
import com.jiaruiblog.util.BaseApiResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zys
 * @since 2024-06-27
 */
@Slf4j
@CrossOrigin
@RestController
@Api(tags = "权限模块")
@RequiredArgsConstructor
@RequestMapping("/permission")
public class PermissionController {
	private final IPermissionService permissionService;

	@GetMapping("/list")
	@ApiOperation("1. 全部权限列表")
	public BaseApiResult allRoles() {
		if (!StpUtil.hasPermission("permission.query")) {
			return BaseApiResult.error(MessageConstant.AUTH_ERROR_CODE, MessageConstant.NOT_PERMISSION);
		}
		return BaseApiResult.success(permissionService.allPermissions());
	}
}
