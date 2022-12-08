package com.jiaruiblog.entity.dto;

import com.jiaruiblog.common.MessageConstant;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @ClassName BatchIdDTO
 * @Description TODO
 * @Author luojiarui
 * @Date 2022/12/8 22:30
 * @Version 1.0
 **/
@Data
public class BatchIdDTO {

    @NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
    protected List<String> ids;

}
