package com.jiaruiblog.infrastructure.repository.mysql;

import com.jiaruiblog.config.datasource.DataSourceCondition;
import com.jiaruiblog.domain.entity.User;
import com.jiaruiblog.infrastructure.repository.UserRepository;
import org.springframework.context.annotation.Conditional;
import org.springframework.data.domain.Sort;

/**
 * MySQL User Repository Implementation
 *
 * @author Jarrett Luo
 * @version 1.0
 */
@Conditional(DataSourceCondition.MySQLCondition.class)
public class UserMybatisRepository implements UserRepository {

    @Override
    public User findById(String id) {
        throw new UnsupportedOperationException("MySQL mode is not yet implemented. Please use MongoDB mode.");
    }

    @Override
    public java.util.List<User> findByUsername(String username) {
        throw new UnsupportedOperationException("MySQL mode is not yet implemented. Please use MongoDB mode.");
    }

    @Override
    public void insert(User user) {
        throw new UnsupportedOperationException("MySQL mode is not yet implemented. Please use MongoDB mode.");
    }

    @Override
    public int update(User user) {
        throw new UnsupportedOperationException("MySQL mode is not yet implemented. Please use MongoDB mode.");
    }

    @Override
    public void updateLoginTime(User user) {
        throw new UnsupportedOperationException("MySQL mode is not yet implemented. Please use MongoDB mode.");
    }

    @Override
    public void blockUser(User user) {
        throw new UnsupportedOperationException("MySQL mode is not yet implemented. Please use MongoDB mode.");
    }

    @Override
    public int deleteById(String id) {
        throw new UnsupportedOperationException("MySQL mode is not yet implemented. Please use MongoDB mode.");
    }

    @Override
    public User save(User user) {
        throw new UnsupportedOperationException("MySQL mode is not yet implemented. Please use MongoDB mode.");
    }

    @Override
    public long count() {
        throw new UnsupportedOperationException("MySQL mode is not yet implemented. Please use MongoDB mode.");
    }

    @Override
    public java.util.List<User> findByPage(int pageNum, int pageSize, Sort sort) {
        throw new UnsupportedOperationException("MySQL mode is not yet implemented. Please use MongoDB mode.");
    }
}
