package com.jiaruiblog.service.impl;

import com.jiaruiblog.entity.Comment;
import com.jiaruiblog.entity.dto.BasePageDTO;
import com.jiaruiblog.entity.dto.CommentListDTO;
import com.jiaruiblog.entity.vo.CommentWithUserVO;
import com.jiaruiblog.entity.vo.PageVO;
import com.jiaruiblog.intercepter.SensitiveFilter;
import com.jiaruiblog.repository.CommentRepository;
import com.jiaruiblog.service.ICommentService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author luojiarui
 **/
@Slf4j
@Service
public class CommentServiceImpl implements ICommentService {

    @Resource
    CommentRepository commentRepository;

    /**
     * 插入新的评论
     *
     * @param comment 评论对象
     */
    @Override
    public void insert(Comment comment) {
        if (!StringUtils.hasText(comment.getUserId()) || !StringUtils.hasText(comment.getUserName())) {
            return;
        }
        try {
            SensitiveFilter filter = SensitiveFilter.getInstance();
            String content = comment.getContent();
            content = filter.replaceSensitiveWord(content, 1, "*");
            comment.setContent(content);
        } catch (IOException e) {
            return;
        }

        comment.setCreateDate(new Date());
        comment.setUpdateDate(new Date());
        commentRepository.save(comment);
    }

    /**
     * 更新评论内容
     *
     * @param comment 评论对象
     */
    @Override
    public void update(Comment comment) {
        if (!StringUtils.hasText(comment.getUserId()) || !StringUtils.hasText(comment.getUserName())) {
            return;
        }
        Optional<Comment> commentDb = commentRepository.findById(comment.getId());
        if (commentDb.isEmpty() || !commentDb.get().getUserId().equals(comment.getUserId())) {
            return;
        }

        comment.setContent(comment.getContent());
        comment.setUpdateDate(new Date());
        try {
            commentRepository.save(comment);
        } catch (Exception e) {
            log.error("更新评论信息{}==>出错==>{}", comment, e);
        }
    }

    /**
     * 删除指定评论
     *
     * @param comment 评论对象
     * @param userId  用户ID
     */
    @Override
    public void remove(Comment comment, String userId) {
        Optional<Comment> commentDb = commentRepository.findById(comment.getId());
        if (commentDb.isEmpty() || !commentDb.get().getUserId().equals(comment.getUserId())) {
            return;
        }
        commentRepository.deleteById(comment.getId());
    }

    /**
     * 批量删除评论
     *
     * @param commentIdList 评论ID列表
     */
    @Override
    public void removeBatch(List<String> commentIdList) {
        commentRepository.deleteAllByIdIn(commentIdList);
    }

    /**
     * 根据文档ID查询相关评论列表（分页）
     *
     * @param comment 评论查询DTO，包含文档ID和分页信息
     * @return Map包含评论总数和评论列表
     */
    @Override
    public Map<String, Object> queryById(CommentListDTO comment) {
        if (comment == null || comment.getDocId() == null) {
            return new HashMap<>();
        }

        List<Comment> comments = commentRepository.findByDocId(comment.getDocId());
        Sort.by(Sort.Direction.DESC, "createDate")
                .stream()
                .skip((long) comment.getPage() * comment.getRows())
                .limit(comment.getRows())
                .collect(Collectors.toList());

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

    /**
     * 搜索评论（暂未实现）
     *
     * @param comment 评论对象
     * @return null
     */
    @Override
    public Object search(Comment comment) {
        return null;
    }

    /**
     * 根据文档ID查询评论数量
     *
     * @param docId 文档ID
     * @return 评论数量
     */
    @Override
    public Long commentNum(String docId) {
        return commentRepository.countByDocId(docId);
    }

    /**
     * 根据关键字模糊搜索相关文档ID
     *
     * @param keyWord 搜索关键字
     * @return 匹配的文档ID列表
     */
    @Override
    public List<String> fuzzySearchDoc(String keyWord) {
        if (keyWord == null || "".equalsIgnoreCase(keyWord)) {
            return Lists.newArrayList();
        }
        List<Comment> comments = commentRepository.findByContentContaining(keyWord);
        return comments.stream().map(Comment::getDocId).collect(Collectors.toList());
    }

    /**
     * 根据文档ID删除所有相关评论
     *
     * @param docId 文档ID
     */
    @Override
    public void removeByDocId(String docId) {
        commentRepository.deleteByDocId(docId);
    }

    /**
     * 统计所有评论数量
     *
     * @return 评论总数
     */
    @Override
    public long countAllFile() {
        return commentRepository.count();
    }

    /**
     * 分页查询评论信息（支持管理员和普通用户不同权限）
     *
     * @param page    分页参数
     * @param userId  用户ID
     * @param isAdmin 是否管理员
     * @return 分页结果VO对象
     */
    @Override
    public PageVO<CommentWithUserVO> queryAllComments(BasePageDTO page, String userId, Boolean isAdmin) {
        log.info("查询的参数是：{}, {}", page, userId);
        Criteria criteria = new Criteria();
        if (Boolean.FALSE.equals(isAdmin)) {
            criteria = Criteria.where("userId").is(userId);
        }

        // Query for comments
        Query query = new Query(criteria)
                .with(Sort.by(Sort.Direction.DESC, "createDate"))
                .skip((long) (page.getPage() - 1) * page.getRows())
                .limit(page.getRows());

        List<Comment> comments = commentRepository.findByQuery(query);
        List<CommentWithUserVO> commentWithUserVOList = new ArrayList<>();

        for (Comment comment : comments) {
            CommentWithUserVO vo = new CommentWithUserVO();
            BeanUtils.copyProperties(comment, vo);
            // Additional processing if needed
            commentWithUserVOList.add(vo);
        }

        long count = commentRepository.countByQuery(new Query(criteria));
        return PageVO.<CommentWithUserVO>builder()
                .total((int) count)
                .list(commentWithUserVOList)
                .pageNum(page.getPage())
                .pageSize(page.getRows())
                .build();
    }
}
