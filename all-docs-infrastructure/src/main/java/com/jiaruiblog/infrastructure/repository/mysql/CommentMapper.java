package com.jiaruiblog.infrastructure.repository.mysql;

import com.jiaruiblog.domain.entity.po.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommentMapper {

    Comment save(Comment comment);

    Comment findById(@Param("id") String id);

    List<Comment> findByDocId(@Param("docId") String docId);

    List<Comment> findByUserId(@Param("userId") String userId);

    long countByDocId(@Param("docId") String docId);

    void deleteById(@Param("id") String id);

    void deleteByDocId(@Param("docId") String docId);
}