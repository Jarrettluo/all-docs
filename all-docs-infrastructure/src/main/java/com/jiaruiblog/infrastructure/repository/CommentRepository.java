package com.jiaruiblog.infrastructure.repository;

import com.jiaruiblog.domain.entity.po.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository {

    int save(Comment comment);

    Optional<Comment> findById(String id);

    List<Comment> findByDocId(String docId);

    List<Comment> findByUserId(String userId);

    List<Comment> findAll();

    List<Comment> findByContentContaining(String keyword);

    long countByDocId(String docId);

    void deleteById(String id);

    void deleteByDocId(String docId);

    void deleteAllByIdIn(List<String> ids);

    long count();
}