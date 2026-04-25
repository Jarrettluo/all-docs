package com.jiaruiblog.infrastructure.repository.mysql;

import com.jiaruiblog.domain.entity.po.Tag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TagMapper {

    Tag save(Tag tag);

    Tag findById(@Param("id") String id);

    List<Tag> findByIds(@Param("idList") List<String> idList);

    List<Tag> findByName(@Param("name") String name);

    List<Tag> findByNames(@Param("nameList") List<String> names);

    List<Tag> findAll();

    long count();

    void delete(Tag tag);
}