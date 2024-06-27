package com.jiaruiblog.service;

import com.jiaruiblog.entity.Permission;
import com.jiaruiblog.entity.Role;
import com.jiaruiblog.entity.dto.BasePageDTO;
import com.jiaruiblog.util.BaseApiResult;

import java.util.List;

/**
 * @author zys
 * @since 2024-06-27
 */
public interface IRoleService {
	List<Role> allRoles();

	List<Permission> getRolePermissions(String roleId);

	List<Permission> getPermissionsByRoleIds(List<String> roleIds);

	BaseApiResult saveRole(Role role);

	BaseApiResult getRoleList(BasePageDTO pageDTO);

	BaseApiResult removeRole(String id);

	BaseApiResult updateRolePerms(String roleId, List<String> permsIds);

}
