package com.jiaruiblog.service.impl;

import com.jiaruiblog.entity.Permission;
import com.jiaruiblog.service.IPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zys
 * @since 2024-06-27
 */
@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements IPermissionService {
	private final MongoTemplate mongoTemplate;
	@Override
	public List<Permission> allPermissions() {
		return mongoTemplate.findAll(Permission.class, "permission");
	}
}
