package com.jiaruiblog.entity.DTO;

import com.jiaruiblog.common.MessageConstant;
import com.jiaruiblog.enums.Type;

import javax.validation.constraints.NotNull;

/**
 * @ClassName RelationDTO
 * @Description TODO
 * @Author luojiarui
 * @Date 2022/6/19 5:35 下午
 * @Version 1.0
 **/
public class RelationDTO {

    @NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
    private Integer dbcId;

    @NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
    private Type type;

    private Integer id;


}
