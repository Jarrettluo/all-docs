package com.jiaruiblog.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.jiaruiblog.common.MessageConstant;
import com.jiaruiblog.entity.Role;
import com.jiaruiblog.entity.dto.BasePageDTO;
import com.jiaruiblog.entity.dto.RolePermsDTO;
import com.jiaruiblog.service.IRoleService;
import com.jiaruiblog.util.BaseApiResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * @author zys
 * @since 2024-06-27
 */
@Slf4j
@CrossOrigin
@RestController
@Api(tags = "角色模块")
@RequiredArgsConstructor
@RequestMapping("/role")
public class RoleController {
	private final IRoleService roleService;

	@GetMapping("/list")
	@ApiOperation("1. 全部角色列表")
	public BaseApiResult allRoles() {
		if (!StpUtil.hasPermission("role.query")) {
			return BaseApiResult.error(MessageConstant.AUTH_ERROR_CODE, MessageConstant.NOT_PERMISSION);
		}
		return BaseApiResult.success(roleService.allRoles());
	}

	@GetMapping("/permission")
	@ApiOperation("2. 根据角色ID获取已有的权限")
	public BaseApiResult getRolePermissions(String roleId) {
		if (!StpUtil.hasPermission("role.query")) {
			return BaseApiResult.error(MessageConstant.AUTH_ERROR_CODE, MessageConstant.NOT_PERMISSION);
		}
		return BaseApiResult.success(roleService.getRolePermissions(roleId));
	}

	@PostMapping("/save")
	@ApiOperation("3. 添加角色")
	public BaseApiResult saveRole(@RequestBody Role role) {
		if (!StpUtil.hasPermission("role.insert")) {
			return BaseApiResult.error(MessageConstant.AUTH_ERROR_CODE, MessageConstant.NOT_PERMISSION);
		}
		return roleService.saveRole(role);
	}

	@GetMapping("/page")
	@ApiOperation("4. 角色分页查询")
	public BaseApiResult getRoleList(BasePageDTO pageDTO) {
		if (!StpUtil.hasPermission("role.query")) {
			return BaseApiResult.error(MessageConstant.AUTH_ERROR_CODE, MessageConstant.NOT_PERMISSION);
		}
		return roleService.getRoleList(pageDTO);
	}

	@GetMapping("/{id}")
	@ApiOperation("5. 根据角色 ID 删除角色")
	public BaseApiResult removeRole(@PathVariable("id") String id) {
		if (!StpUtil.hasPermission("role.remove")) {
			return BaseApiResult.error(MessageConstant.AUTH_ERROR_CODE, MessageConstant.NOT_PERMISSION);
		}
		return roleService.removeRole(id);
	}

	@PostMapping("/perms")
	@ApiOperation("6. 角色分配权限")
	public BaseApiResult updateRolePerms(@RequestBody RolePermsDTO dto) {
		if (!StpUtil.hasPermission("role.perms")) {
			return BaseApiResult.error(MessageConstant.AUTH_ERROR_CODE, MessageConstant.NOT_PERMISSION);
		}
		return roleService.updateRolePerms(dto.getRoleId(), dto.getPermsIds());
	}
}
