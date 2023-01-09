package com.jiaruiblog.entity.dto;

import com.jiaruiblog.common.MessageConstant;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @ClassName CommentDTO
 * @Description 评论的dto
 * @Author luojiarui
 * @Date 2022/6/19 5:27 下午
 * @Version 1.0
 **/
@Data
public class CommentDTO {

    @NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
    protected String content;

    @NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
    protected String docId;

}
