package com.jiaruiblog.entity.dto;

import com.jiaruiblog.common.MessageConstant;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @ClassName BatchIdDTO
 * @Description 批量请求基础模板
 * @Author luojiarui
 * @Date 2022/12/8 22:30
 * @Version 1.0
 **/
@Data
public class BatchIdDTO {


    @ApiModelProperty(value = "类型标识列表（0-原始、1-续签、2-补充）", example = "0")
    @NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
    protected List<
            @NotBlank(message = MessageConstant.PARAMS_IS_NOT_NULL)
            @Size(min = 1, max = 64, message = MessageConstant.PARAMS_LENGTH_REQUIRED)
            String> ids;

}
