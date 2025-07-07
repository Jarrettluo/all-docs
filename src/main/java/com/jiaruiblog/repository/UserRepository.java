package com.jiaruiblog.repository;

import com.jiaruiblog.entity.User;
import org.springframework.data.domain.Sort;

import java.util.List;

/**
 * <p></p>
 * edit at 2025/7/1 11:03
 *
 * @author Jarrett Luo
 * @version 1.0
 */
public interface UserRepository {

    User findById(String id);

    List<User> findByUsername(String username);

    void insert(User user);

    int update(User user);

    void updateLoginTime(User user);

    void blockUser(User user);

    int deleteById(String id);

    User save(User user);

    long count();

    List<User> findByPage(int pageNum, int pageSize, Sort sort);
}
