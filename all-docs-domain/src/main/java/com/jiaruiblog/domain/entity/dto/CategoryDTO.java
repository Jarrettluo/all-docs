package com.jiaruiblog.domain.entity.dto;

import com.jiaruiblog.common.MessageConstant;
import com.jiaruiblog.common.enums.FilterTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;



/**
 * @ClassName CategoryDTO
 * @Description 分类的dto
 * @author luojiarui
 * @Date 2022/6/19 5:32 下午
 * @Version 1.0
 **/
@Schema(description = "分类的对象")
@Data
public class CategoryDTO {

    @Schema(description = "分类的名称")
    @NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
    @Size(max = 64, message = MessageConstant.PARAMS_LENGTH_REQUIRED)
    private String name;

    @Schema(description = "分类的类型")
    @NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
    private FilterTypeEnum type;

    @Schema(description = "分类的id")
    private String id;

}