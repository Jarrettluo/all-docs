package com.jiaruiblog.repository.mysql.mapper;

import com.jiaruiblog.entity.User;

/**
 * <p></p>
 * edit at 2025/7/1 11:04
 *
 * @author Jarrett Luo
 * @version 1.0
 */
//@Mapper
public interface UserMapper {

    User findById(Long id);

    int insert(User user);

    int update(User user);

    int deleteById(Long id);

}
