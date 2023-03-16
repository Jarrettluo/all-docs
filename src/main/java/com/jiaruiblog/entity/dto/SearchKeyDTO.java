package com.jiaruiblog.entity.dto;

import com.jiaruiblog.common.MessageConstant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @ClassName SearchKeyDTO
 * @Description 查询信息
 * @Author luojiarui
 * @Date 2023/2/25 11:20
 * @Version 1.0
 **/
@ApiModel("用户搜索记录")
@Data
public class SearchKeyDTO {

    @ApiModelProperty("用户主键")
    @NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
    private String userId;

    @ApiModelProperty("用户搜索字符")
    @NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
    private String searchWord;

}
