package com.jiaruiblog.infrastructure.repository.mysql;

import com.jiaruiblog.domain.entity.po.User;
import com.jiaruiblog.infrastructure.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * MyBatis User Repository Implementation
 *
 * @author Jarrett Luo
 * @version 1.0
 */
@Repository
public class UserMybatisRepository implements UserRepository {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User findById(String id) {
        return userMapper.findById(id);
    }

    @Override
    public List<User> findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    @Override
    public void insert(User user) {
        userMapper.insert(user);
    }

    @Override
    public int update(User user) {
        return userMapper.update(user);
    }

    @Override
    public void updateLoginTime(User user) {
        userMapper.updateLoginTime(user.getId(), user.getLastLogin());
    }

    @Override
    public void blockUser(User user) {
        userMapper.blockUser(user.getId(), user.getBanning());
    }

    @Override
    public int deleteById(String id) {
        return userMapper.deleteById(id);
    }

    @Override
    public User save(User user) {
        userMapper.save(user);
        return user;
    }

    @Override
    public long count() {
        return userMapper.count();
    }

    @Override
    public List<User> findByPage(int pageNum, int pageSize, Sort sort) {
        int offset = pageNum * pageSize;
        return userMapper.findByPage(offset, pageSize);
    }
}