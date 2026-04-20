package com.jiaruiblog.domain.entity.dto;

import com.jiaruiblog.common.MessageConstant;
import com.jiaruiblog.enums.FilterTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


/**
 * @ClassName documentDTO
 * @Description 文档的dto
 * @author luojiarui
 * @Date 2022/6/19 5:15 下午
 * @Version 1.0
 **/
@Schema(description = "文档查询对象")
@Data
public class DocumentDTO extends BasePageDTO{

    @Schema(description = "过滤类型", requiredMode = Schema.RequiredMode.REQUIRED)
    private FilterTypeEnum type;

    @Schema(description = "过滤词", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
    private String filterWord;

    @Schema(description = "分类id", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
    private String categoryId;

    @Schema(description = "标签id", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
    private String tagId;

    @Schema(description = "用户id")
    private String userId;
}