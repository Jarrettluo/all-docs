package com.jiaruiblog.application.service.converter;

import com.jiaruiblog.domain.entity.po.Comment;
import com.jiaruiblog.domain.entity.vo.CommentWithUserVO;
import org.springframework.stereotype.Component;

@Component
public class CommentConverter {

    public CommentWithUserVO toVO(Comment comment, String docName) {
        CommentWithUserVO vo = new CommentWithUserVO();
        vo.setId(comment.getId());
        vo.setUserId(comment.getUserId());
        vo.setUserName(comment.getUserName());
        vo.setContent(comment.getContent());
        vo.setDocId(comment.getDocId());
        vo.setDocName(docName);
        vo.setCreateDate(comment.getCreateDate());
        vo.setUpdateDate(comment.getUpdateDate());
        return vo;
    }
}
