package com.jiaruiblog.service.impl;

import com.jiaruiblog.auth.PermissionEnum;
import com.jiaruiblog.entity.User;
import com.jiaruiblog.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

/**
 * @Author Jarrett Luo
 * @Date 2022/6/24 13:48
 * @Version 1.0
 */
@Service
public class UserServiceImpl implements IUserService {

    private static final String COLLECTION_NAME = "user";

    @Autowired
    MongoTemplate mongoTemplate;

    /**
     * 根据用户的主键id查询用户信息
     * @param userId
     * @return
     */
    public boolean isExist(String userId) {
        if(userId == null || "".equals(userId)) {
            return false;
        }
        User user = queryById(userId);
        return user != null;
    }

    /**
     * 检索已经存在的user
     * @param userId String userId
     * @return User
     */
    @Override
    public User queryById(String userId) {
        return mongoTemplate.findById(userId, User.class, COLLECTION_NAME);
    }

    /**
     * @Author luojiarui
     * @Description 检查某个用户是否具有某种权限
     * @Date 21:28 2022/12/7
     * @Param [user, permissionEnum]
     * @return boolean
     **/
    @Override
    public boolean checkPermissionForUser(User user, PermissionEnum[] permissionEnums) {
        return true;
    }
}
