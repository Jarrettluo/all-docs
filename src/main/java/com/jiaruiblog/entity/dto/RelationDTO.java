package com.jiaruiblog.entity.dto;

import com.jiaruiblog.common.MessageConstant;
import com.jiaruiblog.enums.FilterTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @ClassName RelationDTO
 * @Description 关系的dto
 * @Author luojiarui
 * @Date 2022/6/19 5:35 下午
 * @Version 1.0
 **/
@ApiModel(value = "文档与标签/分类的关系")
@Data
public class RelationDTO {

    @ApiModelProperty(value = "文档主键", required = true)
    @NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
    private String docId;

    @ApiModelProperty(value = "筛选的类型", notes = "可选的参数是ALL, FILTER, CATEGORY, TAG", required = true)
    @NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
    private FilterTypeEnum type;

    @ApiModelProperty(value = "关系的主键id")
    private String id;


}
