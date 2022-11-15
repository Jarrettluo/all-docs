package com.jiaruiblog.entity.dto;

import com.jiaruiblog.common.MessageConstant;
import com.jiaruiblog.enums.FilterTypeEnum;
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

@Data
public class CategoryDTO {

    @NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
    @Length(max = 64, message = MessageConstant.PARAMS_LENGTH_REQUIRED)
    private String name;

    @NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
    private FilterTypeEnum type;


    private String id;

}
