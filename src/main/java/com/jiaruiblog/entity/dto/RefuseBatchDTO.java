package com.jiaruiblog.entity.dto;

import com.jiaruiblog.common.MessageConstant;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @ClassName RefuseBatchDTO
 * @Description TODO
 * @Author luojiarui
 * @Date 2022/12/8 22:45
 * @Version 1.0
 **/
@Data
public class RefuseBatchDTO extends BatchIdDTO{

    @NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
    @Size(min = 1, max = 128, message = MessageConstant.PARAMS_LENGTH_REQUIRED)
    protected String reason;
}
