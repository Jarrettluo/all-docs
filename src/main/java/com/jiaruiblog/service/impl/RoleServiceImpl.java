package com.jiaruiblog.service.impl;

import com.jiaruiblog.common.MessageConstant;
import com.jiaruiblog.entity.Permission;
import com.jiaruiblog.entity.Role;
import com.jiaruiblog.entity.User;
import com.jiaruiblog.entity.dto.BasePageDTO;
import com.jiaruiblog.service.IRoleService;
import com.jiaruiblog.util.BaseApiResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zys
 * @since 2024-06-27
 */
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements IRoleService {
	private final MongoTemplate mongoTemplate;

	@Override
	public List<Role> allRoles() {
		return mongoTemplate.findAll(Role.class);
	}

	@Override
	public List<Permission> getRolePermissions(String roleId) {
		Role role = mongoTemplate.findById(roleId, Role.class, "role");
		if (role == null) {
			return Collections.emptyList();
		}
		List<String> permIds = role.getPermIds();
		Criteria criteria = Criteria.where("_id").in(permIds);
		Query query = Query.query(criteria);
		return mongoTemplate.find(query, Permission.class, "permission");
	}

	@Override
	public List<Permission> getPermissionsByRoleIds(List<String> roleIds) {
		Criteria criteria = Criteria.where("_id").in(roleIds);
		Query query = Query.query(criteria);
		List<Role> roles = mongoTemplate.find(query, Role.class, "role");

		List<String> permIds = roles.stream().map(Role::getPermIds).flatMap(Collection::stream).collect(Collectors.toList());
		Criteria criteria1 = Criteria.where("_id").in(permIds);
		Query query1 = Query.query(criteria1);
		return mongoTemplate.find(query1, Permission.class, "permission");
	}

	@Override
	public BaseApiResult saveRole(Role role) {
		// 判断角色关键是否存在
		Criteria criteria = Criteria.where("roleKey").is(role.getRoleKey());
		Query query = Query.query(criteria);
		Role dbRole = mongoTemplate.findOne(query, Role.class, "role");
		if (dbRole != null) {
			return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.DATA_DUPLICATE);
		}
		role.setId(null);
		role.setCreateDate(new Date());
		role.setUpdateDate(new Date());
		mongoTemplate.save(role, "role");

		return BaseApiResult.success(MessageConstant.SUCCESS);
	}

	@Override
	public BaseApiResult getRoleList(BasePageDTO page) {
		long count = mongoTemplate.count(new Query(), Role.class, "role");
		if (count < 1) {
			return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.DATA_IS_NULL);
		}
		int pageNum = Optional.ofNullable(page.getPage()).orElse(1);
		int pageSize = Optional.ofNullable(page.getRows()).orElse(10);
		// 如果传入的参数超过了总数，返回第一页
		if ((long) (pageNum - 1) * pageSize > count) {
			pageNum = 1;
		}
		Query query = new Query();
		query.skip((long) (pageNum - 1) * pageSize);
		query.limit(pageSize);
		query.with(Sort.by(Sort.Direction.DESC, "createDate"));
		List<Role> users = mongoTemplate.find(query, Role.class, "role");
		Map<String, Object> result = new HashMap<>();
		result.put("total", count);
		result.put("pageNum", pageNum);
		result.put("pageSize", pageSize);
		result.put("result", users);
		return BaseApiResult.success(result);
	}

	@Override
	public BaseApiResult removeRole(String id) {
		Query query = new Query(Criteria.where("_id").is(id));
		Role role = mongoTemplate.findOne(query, Role.class);
		if (role == null) {
			return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.DATA_IS_NULL);
		}

		Query userQuery = new Query(Criteria.where("roleIds").is(role.getId()));
		long count = mongoTemplate.count(userQuery, User.class);
		if (count > 0) {
			return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE, "当前角色还有用户关联");
		}
		mongoTemplate.findAndRemove(query, Role.class);
		return BaseApiResult.success(MessageConstant.SUCCESS);
	}

	@Override
	public BaseApiResult updateRolePerms(String roleId, List<String> permsIds) {
		Role role = mongoTemplate.findById(roleId, Role.class, "role");
		if (role == null) {
			return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE, MessageConstant.PARAMS_FORMAT_ERROR);
		}
		Criteria criteria = Criteria.where("_id").in(permsIds);
		Query query = Query.query(criteria);
		List<Permission> perms = mongoTemplate.find(query, Permission.class, "permission");
		List<String> permIds1 = perms.stream().map(Permission::getId).collect(Collectors.toList());
		role.setPermIds(permIds1);

		Query query1 = new Query().addCriteria(Criteria.where("_id").is(roleId));
		Update update = new Update();
		update.set("permsId", permIds1);
		update.set("updateDate", new Date());
		mongoTemplate.updateFirst(query1, update, "role");
		return BaseApiResult.success(MessageConstant.SUCCESS);
	}
}
