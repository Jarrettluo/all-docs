package com.jiaruiblog.entity.dto;

import com.jiaruiblog.common.MessageConstant;
import com.jiaruiblog.enums.FilterTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


/**
 * @ClassName RelationDTO
 * @Description 关系的dto
 * @author luojiarui
 * @Date 2022/6/19 5:35 下午
 * @Version 1.0
 **/
@Schema(name = "RelationDTO", description = "文档与标签/分类的关系")
@Data
public class RelationDTO {

    @Schema(description = "文档主键", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
    private String docId;

    @Schema(description = "筛选的类型", allowableValues = {"ALL", "FILTER", "CATEGORY", "TAG"}, requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
    private FilterTypeEnum type;

    @Schema(description = "关系的主键id")
    private String id;

}
