package com.jiaruiblog.entity.dto;

import com.jiaruiblog.common.MessageConstant;
import com.jiaruiblog.enums.FilterTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @ClassName documentDTO
 * @Description 文档的dto
 * @Author luojiarui
 * @Date 2022/6/19 5:15 下午
 * @Version 1.0
 **/
@ApiModel("文档查询对象")
@Data
public class DocumentDTO extends BasePageDTO{

    @ApiModelProperty(value = "过滤类型")
    @NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
    private FilterTypeEnum type;

    @ApiModelProperty(value = "过滤词")
    @NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
    private String filterWord;

    @ApiModelProperty(value = "分类id")
    @NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
    private String categoryId;

    @ApiModelProperty(value = "标签id")
    @NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
    private String tagId;

    @ApiModelProperty(value = "用户id")
    private String userId;
}
