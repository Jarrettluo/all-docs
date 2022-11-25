package com.jiaruiblog.entity.dto;

import com.jiaruiblog.common.MessageConstant;
import com.jiaruiblog.enums.FilterTypeEnum;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @ClassName RelationDTO
 * @Description 关系的dto
 * @Author luojiarui
 * @Date 2022/6/19 5:35 下午
 * @Version 1.0
 **/
@Data
public class RelationDTO {

    @NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
    private String docId;

    @NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
    private FilterTypeEnum type;

    private String id;


}
