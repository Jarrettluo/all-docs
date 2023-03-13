package com.jiaruiblog.entity.dto;

import com.jiaruiblog.common.MessageConstant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @ClassName RefuseDTO
 * @Description 拒绝文档的实体类
 * @Author luojiarui
 * @Date 2022/12/8 21:02
 * @Version 1.0
 **/
@ApiModel("拒绝文档的传入参数")
@Data
public class RefuseDTO {

    @ApiModelProperty(value = "文档id", notes = "id长度最小为1最大为64", required = true)
    @NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
    @NotBlank(message = MessageConstant.PARAMS_LENGTH_REQUIRED)
    @Size(min = 1, max = 64, message = MessageConstant.PARAMS_LENGTH_REQUIRED)
    private String docId;


    @ApiModelProperty(value = "拒绝文档的原因", notes = "拒绝原因最小为1，最大为128", required = true)
    @NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
    @NotBlank(message = MessageConstant.PARAMS_LENGTH_REQUIRED)
    @Size(min = 1, max = 128, message = MessageConstant.PARAMS_LENGTH_REQUIRED)
    private String reason;

}
