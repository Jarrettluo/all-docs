package com.jiaruiblog.entity.dto;

import com.jiaruiblog.common.MessageConstant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @ClassName CommentDTO
 * @Description 评论的dto
 * @Author luojiarui
 * @Date 2022/6/19 5:27 下午
 * @Version 1.0
 **/
@ApiModel(value = "用户评论信息")
@Data
public class CommentDTO {

    @ApiModelProperty(value = "评论内容", notes = "评论信息长度限制")
    @NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
    protected String content;

    @ApiModelProperty(value = "文档的主键")
    @NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
    protected String docId;

}
