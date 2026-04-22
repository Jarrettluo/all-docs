package com.jiaruiblog.domain.entity;

import com.jiaruiblog.common.MessageConstant;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Date;

/**
 * @ClassName Comment
 * @Description 用户针对某一个文档的评论
 * @author luojiarui
 * @Date 2022/6/4 10:31 上午
 * @Version 1.0
 **/
@Data
public class Comment {

    @Id
    private String id;

    @NotNull
    private Long createUser;

    private String userId;

    private String userName;

    @NotBlank(message = "content" + MessageConstant.PARAMS_IS_NOT_NULL)
    @Size(min = 1, max = 140, message = MessageConstant.PARAMS_LENGTH_REQUIRED)
    private String content;

    @NotNull
    private String docId;

    private Date createDate;

    private Date updateDate;
}