package com.jiaruiblog.infrastructure.repository.mysql;

import com.jiaruiblog.domain.entity.po.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface CategoryMapper {

    void save(Category category);

    Optional<Category> findById(@Param("id") String id);

    List<Category> findByName(@Param("name") String name);

    List<Category> findAll();

    long countAll();
}