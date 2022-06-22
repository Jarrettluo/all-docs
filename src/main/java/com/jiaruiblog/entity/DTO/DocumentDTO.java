package com.jiaruiblog.entity.DTO;

import com.jiaruiblog.common.MessageConstant;
import com.jiaruiblog.enums.Type;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @ClassName documentDTO
 * @Description TODO
 * @Author luojiarui
 * @Date 2022/6/19 5:15 下午
 * @Version 1.0
 **/
@Data
public class DocumentDTO {

    @NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
    private Type type;

    @NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
    private String filterWord;

    @NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
    @Min(value = 1, message = MessageConstant.PARAMS_LENGTH_REQUIRED)
    private Integer page;

    @NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
    @Min(value = 1, message = MessageConstant.PARAMS_LENGTH_REQUIRED)
    private Integer rows;

    @NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
    private Long categoryId;

    @NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
    private Long tagId;

}
