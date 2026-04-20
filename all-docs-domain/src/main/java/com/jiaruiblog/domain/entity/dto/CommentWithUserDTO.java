package com.jiaruiblog.domain.entity.dto;

import lombok.Data;

import java.util.Date;

/**
 * @ClassName CommentWithUserDTO
 * @Description 查询用户的评论信息
 * @author luojiarui
 * @Date 2023/1/8 22:58
 * @Version 1.0
 **/
@Data
public class CommentWithUserDTO extends CommentDTO {

    private String id;

    private String userName;

    private Date createDate;

    private String userId;

}