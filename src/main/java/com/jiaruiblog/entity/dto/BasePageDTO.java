package com.jiaruiblog.entity.dto;

import com.jiaruiblog.common.MessageConstant;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @ClassName BasePageDTO
 * @Description 页码查询的参数
 * @Author luojiarui
 * @Date 2022/11/29 23:26
 * @Version 1.0
 **/
@Data
public class BasePageDTO {

    /**
     * 页数
     */
    @NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
    @Min(value = 1, message = MessageConstant.PARAMS_FORMAT_ERROR)
    protected Integer page;

    /**
     * 每页条数
     */
    @NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
    @Min(value = 1, message = MessageConstant.PARAMS_FORMAT_ERROR)
    @Max(value = 100, message = MessageConstant.PARAMS_FORMAT_ERROR)
    protected Integer rows;
}
