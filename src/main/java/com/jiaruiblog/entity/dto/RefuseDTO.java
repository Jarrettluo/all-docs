package com.jiaruiblog.entity.dto;

import com.jiaruiblog.common.MessageConstant;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @ClassName RefuseDTO
 * @Description TODO
 * @Author luojiarui
 * @Date 2022/12/8 21:02
 * @Version 1.0
 **/
@Data
public class RefuseDTO {

    @NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
    @NotBlank(message = MessageConstant.PARAMS_LENGTH_REQUIRED)
    @Size(min = 1, max = 64, message = MessageConstant.PARAMS_LENGTH_REQUIRED)
    private String docId;

    @NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
    @NotBlank(message = MessageConstant.PARAMS_LENGTH_REQUIRED)
    @Size(min = 1, max = 128, message = MessageConstant.PARAMS_LENGTH_REQUIRED)
    private String reason;

}
