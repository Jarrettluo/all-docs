package com.jiaruiblog.application.service.impl;

import cn.hutool.core.util.IdUtil;
import com.jiaruiblog.application.service.ICommentService;
import com.jiaruiblog.domain.entity.po.Comment;
import com.jiaruiblog.domain.entity.dto.BasePageDTO;
import com.jiaruiblog.domain.entity.dto.CommentListDTO;
import com.jiaruiblog.domain.entity.vo.CommentWithUserVO;
import com.jiaruiblog.domain.entity.vo.PageVO;
import com.jiaruiblog.infrastructure.repository.CommentRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
        if (comment == null || !StringUtils.hasText(comment.getUserId()) || !StringUtils.hasText(comment.getUserName())) {
            return;
        }
        // Note: Sensitive filtering should be done at the API layer before calling this method
        comment.setId(IdUtil.fastUUID());
        comment.setCreateUser(comment.getUserId());
        comment.setCreateDate(new Date());
        comment.setUpdateDate(new Date());
        commentRepository.save(comment);
    }

    @Override
    public void update(Comment comment) {
        if (comment == null) {
            return;
        }
        comment.setUpdateDate(new Date());
        commentRepository.save(comment);
    }

    @Override
    public void remove(Comment comment, String userId) {
        Optional<Comment> commentDb = commentRepository.findById(comment.getId());
        if (commentDb.isEmpty() || !commentDb.get().getUserId().equals(userId)) {
            return;
        }
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
        if (comment == null || comment.getDocId() == null) {
            return new HashMap<>();
        }

        List<Comment> comments = commentRepository.findByDocId(comment.getDocId());

        comments = comments.stream()
                .skip((long) comment.getPage() * comment.getRows())
                .limit(comment.getRows())
                .toList();

        Long totalNum = commentRepository.countByDocId(comment.getDocId());
        List<CommentWithUserVO> commentWithUserVOList = new ArrayList<>();
        for (Comment item : comments) {
            CommentWithUserVO commentWithUserVO = new CommentWithUserVO();
            BeanUtils.copyProperties(item, commentWithUserVO);
            commentWithUserVOList.add(commentWithUserVO);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("totalNum", totalNum);
        result.put("comments", commentWithUserVOList);

        return result;
    }

    @Override
    public Long commentNum(String docId) {
        return commentRepository.countByDocId(docId);
    }

    @Override
    public List<String> fuzzySearchDoc(String keyWord) {
        if (keyWord == null || "".equalsIgnoreCase(keyWord)) {
            return new ArrayList<>();
        }
        List<Comment> comments = commentRepository.findByContentContaining(keyWord);
        return comments.stream().map(Comment::getDocId).collect(Collectors.toList());
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
        log.info("查询的参数是：{}, {}", page, userId);
        // Note: For admin, return all comments; for user, return only their own
        List<Comment> comments;
        if (Boolean.TRUE.equals(isAdmin)) {
            comments = commentRepository.findAll();
        } else {
            comments = commentRepository.findByUserId(userId);
        }

        long count = comments.size();

        // Pagination: page is 1-indexed, convert to 0-indexed for skip
        int pageNum = page.getPage();
        int pageSize = page.getRows();
        int skip = (pageNum - 1) * pageSize;
        comments = comments.stream()
                .skip(skip)
                .limit(pageSize)
                .toList();

        List<CommentWithUserVO> commentWithUserVOList = new ArrayList<>();
        for (Comment comment : comments) {
            CommentWithUserVO vo = new CommentWithUserVO();
            BeanUtils.copyProperties(comment, vo);
            commentWithUserVOList.add(vo);
        }

        return PageVO.<CommentWithUserVO>builder()
                .total((int) count)
                .list(commentWithUserVOList)
                .pageNum(pageNum)
                .pageSize(pageSize)
                .build();
    }
}