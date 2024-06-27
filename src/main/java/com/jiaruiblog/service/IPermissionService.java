package com.jiaruiblog.service;

import com.jiaruiblog.entity.Permission;

import java.util.List;

/**
 * @author zys
 * @since 2024-06-27
 */
public interface IPermissionService {
	List<Permission> allPermissions();
}
