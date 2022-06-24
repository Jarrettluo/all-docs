package com.jiaruiblog.entity;

import com.jiaruiblog.common.MessageConstant;
import lombok.Data;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * @ClassName Comment
 * @Description 用户针对某一个文档的评论
 * @Author luojiarui
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
