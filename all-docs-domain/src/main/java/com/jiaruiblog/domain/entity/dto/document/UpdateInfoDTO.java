package com.jiaruiblog.domain.entity.dto.document;

import com.jiaruiblog.common.MessageConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * @ClassName UpdateInfoDTO
 * @Description 更新文档基本信息的数据传输对象
 * @author luojiarui
 * @Date 2023/6/28 23:08
 * @Version 1.0
 **/
@Schema(description = "更新文档基本信息")
@Data
public class UpdateInfoDTO {

    @Schema(description = "文档id", example = "文档的id为字符串")
    @NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
    private String id;

    @Schema(description = "文档名称", example = "文档的名称不能为空，不能超过120字")
    @NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
    @Size(min = 1, max = 120)
    private String name;

    @Schema(description = "文档分类", example = "分类id")
    private String categoryId;

    @Schema(description = "文档标签列表", example = "标签列表")
    private List<String> tags;

    @Schema(description = "文档描述信息", example = "不超过200字")
    private String desc;
}