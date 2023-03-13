package com.jiaruiblog.entity.dto;

import com.jiaruiblog.common.MessageConstant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @ClassName CommentListDTO
 * @Description CommentListDTO
 * @Author luojiarui
 * @Date 2022/9/4 11:45
 * @Version 1.0
 **/
@ApiModel(value = "根据文档信息查询所属的文档评论")
@Data
public class CommentListDTO extends BasePageDTO {

    @ApiModelProperty(value = "文档主键", required = true)
    @NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
    private String docId;

}
