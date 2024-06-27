package com.jiaruiblog.config;

import cn.dev33.satoken.stp.StpInterface;
import com.jiaruiblog.entity.Permission;
import com.jiaruiblog.entity.Role;
import com.jiaruiblog.service.IRoleService;
import com.jiaruiblog.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zys
 * @since 2024-06-27
 */
@Component
@RequiredArgsConstructor
public class SaTokenStpImpl implements StpInterface {
	private final IUserService userService;
	private final IRoleService roleService;

	@Override
	public List<String> getPermissionList(Object loginId, String loginType) {
		List<Role> userRoles = userService.getUserRoles((String) loginId);
		List<String> roleIds = userRoles.stream().map(Role::getId).collect(Collectors.toList());
		List<Permission> permissions = roleService.getPermissionsByRoleIds(roleIds);
		for (Permission permission : permissions) {
			System.out.println("permission = " + permission);
		}
		return permissions.stream().map(Permission::getPermKey).collect(Collectors.toList());
	}

	@Override
	public List<String> getRoleList(Object loginId, String s) {
		List<Role> userRoles = userService.getUserRoles((String) loginId);
		List<String> roleKeys = userRoles.stream().map(Role::getRoleKey).collect(Collectors.toList());
		if (roleKeys.isEmpty()){
			roleKeys.add("role.normal");
		}
		return roleKeys;
	}
}
