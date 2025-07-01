package com.jiaruiblog.repository;

import com.jiaruiblog.entity.User;

/**
 * <p></p>
 * edit at 2025/7/1 11:03
 *
 * @author Jarrett Luo
 * @version 1.0
 */
public interface UserRepository {

    User findById(Long id);

    int insert(User user);

    int update(User user);

    int deleteById(Long id);

    User save(User user);
}
