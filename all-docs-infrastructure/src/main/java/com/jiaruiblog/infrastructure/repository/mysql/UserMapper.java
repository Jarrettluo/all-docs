package com.jiaruiblog.infrastructure.repository.mysql;

import com.jiaruiblog.domain.entity.po.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {

    User findById(@Param("id") String id);

    List<User> findByUsername(@Param("username") String username);

    void insert(User user);

    int update(User user);

    void updateLoginTime(@Param("id") String id, @Param("lastLogin") java.util.Date lastLogin);

    void blockUser(@Param("id") String id, @Param("banning") boolean banning);

    int deleteById(@Param("id") String id);

    User save(User user);

    long count();

    List<User> findByPage(@Param("offset") int offset, @Param("limit") int limit);
}