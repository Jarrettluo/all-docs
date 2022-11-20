package com.jiaruiblog.service.impl;

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
     * @param userId
     * @return
     */
    public User queryById(String userId) {
        return mongoTemplate.findById(userId, User.class, COLLECTION_NAME);
    }
}
