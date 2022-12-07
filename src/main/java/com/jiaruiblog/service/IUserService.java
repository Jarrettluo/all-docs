package com.jiaruiblog.service;

import com.jiaruiblog.auth.PermissionEnum;
import com.jiaruiblog.entity.User;

/**
 * @author jiarui.luo
 */
public interface IUserService {

    User queryById(String userId);

    boolean checkPermissionForUser(User user, PermissionEnum[] permissionEnums);

}
