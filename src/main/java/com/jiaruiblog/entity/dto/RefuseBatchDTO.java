package com.jiaruiblog.entity.dto;

import com.jiaruiblog.common.MessageConstant;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;


/**
 * @ClassName RefuseBatchDTO
 * @Description 批量拒绝的实体类
 * @author luojiarui
 * @Date 2022/12/8 22:45
 * @Version 1.0
 **/
@Data
public class RefuseBatchDTO extends BatchIdDTO{

    @NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
    @Size(min = 1, max = 128, message = MessageConstant.PARAMS_LENGTH_REQUIRED)
    protected String reason;
}
