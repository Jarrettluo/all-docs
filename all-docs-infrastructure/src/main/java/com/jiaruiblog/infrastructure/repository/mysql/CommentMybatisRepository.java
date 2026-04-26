package com.jiaruiblog.infrastructure.repository.mysql;

import com.jiaruiblog.domain.entity.po.Comment;
import com.jiaruiblog.infrastructure.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * MyBatis Comment Repository Implementation
 *
 * @author luojiarui
 */
@Repository
public class CommentMybatisRepository implements CommentRepository {

    @Autowired
    private CommentMapper commentMapper;

    @Override
    public Comment save(Comment comment) {
        return commentMapper.save(comment);
    }

    @Override
    public Optional<Comment> findById(String id) {
        return Optional.ofNullable(commentMapper.findById(id));
    }

    @Override
    public List<Comment> findByDocId(String docId) {
        return commentMapper.findByDocId(docId);
    }

    @Override
    public List<Comment> findByUserId(String userId) {
        return commentMapper.findByUserId(userId);
    }

    @Override
    public List<Comment> findByContentContaining(String keyword) {
        return commentMapper.findByContentContaining(keyword);
    }

    @Override
    public long countByDocId(String docId) {
        return commentMapper.countByDocId(docId);
    }

    @Override
    public void deleteById(String id) {
        commentMapper.deleteById(id);
    }

    @Override
    public void deleteByDocId(String docId) {
        commentMapper.deleteByDocId(docId);
    }

    @Override
    public void deleteAllByIdIn(List<String> ids) {
        commentMapper.deleteAllByIdIn(ids);
    }

    @Override
    public long count() {
        return commentMapper.count();
    }
}
