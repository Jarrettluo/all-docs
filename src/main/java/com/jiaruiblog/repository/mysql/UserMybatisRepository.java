package com.jiaruiblog.repository.mysql;

import com.jiaruiblog.config.datasource.DataSourceCondition;
import com.jiaruiblog.entity.User;
import com.jiaruiblog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

/**
 * <p></p>
 * edit at 2025/7/1 11:05
 *
 * @author Jarrett Luo
 * @version 1.0
 */
@Repository
@Conditional(DataSourceCondition.MySQLCondition.class)
public class UserMybatisRepository implements UserRepository {


    @Override
    public User findById(Long id) {
        return null;
    }

    @Override
    public int insert(User user) {
        return 0;
    }

    @Override
    public int update(User user) {
        return 0;
    }

    @Override
    public int deleteById(Long id) {
        return 0;
    }

    @Override
    public User save(User user) {
        return null;
    }
}
