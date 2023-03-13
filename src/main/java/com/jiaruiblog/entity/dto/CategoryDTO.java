package com.jiaruiblog.entity.dto;

import com.jiaruiblog.common.MessageConstant;
import com.jiaruiblog.enums.FilterTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;


/**
 * @ClassName CategoryDTO
 * @Description 分类的dto
 * @Author luojiarui
 * @Date 2022/6/19 5:32 下午
 * @Version 1.0
 **/
@ApiModel("分类的对象")
@Data
public class CategoryDTO {

    @ApiModelProperty(value = "分类的名称", required = true)
    @NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
    @Length(max = 64, message = MessageConstant.PARAMS_LENGTH_REQUIRED)
    private String name;

    @ApiModelProperty(value = "分类的类型", required = true)
    @NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
    private FilterTypeEnum type;

    @ApiModelProperty("分类的id")
    private String id;

}
