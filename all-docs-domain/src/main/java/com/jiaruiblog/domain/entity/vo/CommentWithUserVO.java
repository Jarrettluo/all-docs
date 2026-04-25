package com.jiaruiblog.domain.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jiaruiblog.domain.entity.dto.CommentDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @ClassName CommentWithUserVO
 * @Description 评论视图对象，包含评论信息
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CommentWithUserVO extends CommentDTO {

    private String id;

    private String userName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createDate;

    private String userId;

    private String userAvatarId;

    private String docName;
}