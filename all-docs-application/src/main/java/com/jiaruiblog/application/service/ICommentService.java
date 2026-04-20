package com.jiaruiblog.application.service;

import com.jiaruiblog.domain.entity.Comment;
import com.jiaruiblog.domain.entity.dto.BasePageDTO;
import com.jiaruiblog.domain.entity.dto.CommentListDTO;
import com.jiaruiblog.domain.entity.vo.CommentWithUserVO;
import com.jiaruiblog.domain.entity.vo.PageVO;

import java.util.List;
import java.util.Map;

/**
 * @author Jarrett Luo
 * @Date 2022/6/7 11:39
 * @Version 1.0
 */
public interface ICommentService {

    void insert(Comment comment);

    void update(Comment comment);

    void remove(Comment comment, String userId);

    void removeBatch(List<String> commentIds);

    void search(Comment comment);

    Map<String, Object> queryById(CommentListDTO comment);

    Long commentNum(String docId);

    List<String> fuzzySearchDoc(String keyWord);

    void removeByDocId(String docId);

    long countAllFile();

    PageVO<CommentWithUserVO> queryAllComments(BasePageDTO page, String userId, Boolean isAdmin);
}