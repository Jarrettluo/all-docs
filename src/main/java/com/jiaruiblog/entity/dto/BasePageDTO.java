package com.jiaruiblog.entity.dto;

import com.jiaruiblog.common.MessageConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Schema(name = "BasePageDTO", description = "查询分页数据的实体类")
@Data
public class BasePageDTO {

    @Schema(description = "分页查询的页数，从1开始", minimum = "1", required = true)
    @NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
    @Min(value = 1, message = MessageConstant.PARAMS_FORMAT_ERROR)
    protected Integer page;

    @Schema(description = "每页查询的条数，范围是1到100", minimum = "1", maximum = "100", required = true)
    @NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
    @Min(value = 1, message = MessageConstant.PARAMS_FORMAT_ERROR)
    protected Integer rows;
}
