package com.jiaruiblog.domain.entity.dto;

import com.jiaruiblog.common.MessageConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * @ClassName BatchIdDTO
 * @Description 批量请求基础模板
 * @author luojiarui
 * @Date 2022/12/8 22:30
 * @Version 1.0
 **/
@Schema(name = "批量请求基础对象")
@Data
public class BatchIdDTO {

    @Schema(description = "ID列表")
    @NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
    private List<String> ids;

}
