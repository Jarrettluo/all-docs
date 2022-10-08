package com.jiaruiblog.entity.dto;

import com.jiaruiblog.common.MessageConstant;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @ClassName CommentListDTO
 * @Description CommentListDTO
 * @Author luojiarui
 * @Date 2022/9/4 11:45
 * @Version 1.0
 **/
@Data
public class CommentListDTO {

    @NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
    private String docId;

    @NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
    private Integer page;

    @NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
    private Integer rows;

}
