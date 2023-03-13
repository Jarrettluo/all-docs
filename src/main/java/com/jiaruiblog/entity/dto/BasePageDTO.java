package com.jiaruiblog.entity.dto;

import com.jiaruiblog.common.MessageConstant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(value = "查询分页数据的实体类", description = "各类分页数据列表查询的实体")
@Data
public class BasePageDTO {

    /**
     * 页数
     */
    @ApiModelProperty(value = "分页查询的页数，从1开始", required = true)
    @NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
    @Min(value = 1, message = MessageConstant.PARAMS_FORMAT_ERROR)
    protected Integer page;

    /**
     * 每页条数
     */
    @ApiModelProperty(value = "每页查询的条数，范围是1到100", required = true)
    @NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
    @Min(value = 1, message = MessageConstant.PARAMS_FORMAT_ERROR)
    @Max(value = 100, message = MessageConstant.PARAMS_FORMAT_ERROR)
    protected Integer rows;
}
