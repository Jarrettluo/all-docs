package com.jiaruiblog.domain.repository;

import com.jiaruiblog.domain.entity.User;

import java.util.List;

/**
 * @ClassName UserRepository
 * @Description 用户仓储接口
 * @author luojiarui
 * @Date 2022/6/4 9:37 上午
 * @Version 1.0
 **/
public interface UserRepository {

    /**
     * 根据ID查询用户
     * @param id 用户ID
     * @return 用户实体
     */
    User findById(String id);

    /**
     * 根据用户名查询用户列表
     * @param username 用户名
     * @return 用户列表
     */
    List<User> findByUsername(String username);

    /**
     * 保存用户
     * @param user 用户实体
     * @return 保存后的用户
     */
    User save(User user);

    /**
     * 更新用户
     * @param user 用户实体
     * @return 更新条数
     */
    int update(User user);

    /**
     * 删除用户
     * @param id 用户ID
     * @return 删除条数
     */
    int deleteById(String id);

    /**
     * 更新最后登录时间
     * @param id 用户ID
     * @param lastLogin 最后登录时间
     */
    void updateLoginTime(String id, java.util.Date lastLogin);

    /**
     * 封禁/解封用户
     * @param id 用户ID
     * @param banning 封禁状态
     */
    void blockUser(String id, boolean banning);

    /**
     * 统计用户总数
     * @return 用户数量
     */
    long count();

    /**
     * 分页查询用户
     * @param offset 偏移量
     * @param limit 每页数量
     * @return 用户列表
     */
    List<User> findByPage(int offset, int limit);
}