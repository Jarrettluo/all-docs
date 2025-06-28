package com.jiaruiblog.entity.dto;

import com.jiaruiblog.common.MessageConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @Schema(description = "类型标识列表（0-原始、1-续签、2-补充）", example = "0")
    @NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
    protected List<
            @NotBlank(message = MessageConstant.PARAMS_IS_NOT_NULL)
            @Size(min = 1, max = 64, message = MessageConstant.PARAMS_LENGTH_REQUIRED)
            String> ids;

}
