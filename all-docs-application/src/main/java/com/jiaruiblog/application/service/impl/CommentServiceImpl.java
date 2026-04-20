package com.jiaruiblog.application.service.impl;

import com.jiaruiblog.application.service.ICommentService;
import com.jiaruiblog.domain.entity.Comment;
import com.jiaruiblog.domain.entity.dto.BasePageDTO;
import com.jiaruiblog.domain.entity.dto.CommentListDTO;
import com.jiaruiblog.domain.entity.vo.CommentWithUserVO;
import com.jiaruiblog.domain.entity.vo.PageVO;
import com.jiaruiblog.infrastructure.repository.CommentRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author luojiarui
 **/
@Slf4j
@Service
public class CommentServiceImpl implements ICommentService {

    @Resource
    CommentRepository commentRepository;

    @Override
    public void insert(Comment comment) {
        comment.setCreateDate(new Date());
        comment.setUpdateDate(new Date());
        commentRepository.save(comment);
    }

    @Override
    public void update(Comment comment) {
        comment.setUpdateDate(new Date());
        commentRepository.save(comment);
    }

    @Override
    public void remove(Comment comment, String userId) {
        commentRepository.deleteById(comment.getId());
    }

    @Override
    public void removeBatch(List<String> commentIds) {
        commentRepository.deleteAllByIdIn(commentIds);
    }

    @Override
    public void search(Comment comment) {
    }

    @Override
    public Map<String, Object> queryById(CommentListDTO comment) {
        return Map.of();
    }

    @Override
    public Long commentNum(String docId) {
        return commentRepository.countByDocId(docId);
    }

    @Override
    public List<String> fuzzySearchDoc(String keyWord) {
        return List.of();
    }

    @Override
    public void removeByDocId(String docId) {
        commentRepository.deleteByDocId(docId);
    }

    @Override
    public long countAllFile() {
        return commentRepository.count();
    }

    @Override
    public PageVO<CommentWithUserVO> queryAllComments(BasePageDTO page, String userId, Boolean isAdmin) {
        return PageVO.<CommentWithUserVO>builder().build();
    }
}