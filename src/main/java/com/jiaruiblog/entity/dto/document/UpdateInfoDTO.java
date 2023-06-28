package com.jiaruiblog.entity.dto.document;

import com.jiaruiblog.common.MessageConstant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @ClassName UpdateInfoDTO
 * @Description TODO
 * @Author luojiarui
 * @Date 2023/6/28 23:08
 * @Version 1.0
 **/
@ApiModel(value = "更新文档基本信息")
@Data
public class UpdateInfoDTO {

    @ApiModelProperty(value = "文档id", notes = "文档的id为字符串")
    @NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
    private String id;

    @ApiModelProperty(value = "文档名称", notes = "文档的名称不能为空，不能超过120字")
    @NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
    @Size(min = 1, max = 120)
    private String name;

    @ApiModelProperty(value = "文档分类", notes = "分类id")
    private String categoryId;

    @ApiModelProperty(value = "文档标签列表", notes = "标签列表")
    private List<String> tags;

    @ApiModelProperty(value = "文档描述信息", notes = "不超过200字")
    @Size(min = 0, max = 200)
    private String desc;
}
