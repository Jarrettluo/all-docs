package com.jiaruiblog.entity.dto;

import com.jiaruiblog.auth.PermissionEnum;
import com.jiaruiblog.common.MessageConstant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @ClassName UserSexDTO
 * @Description 用户角色
 * @Author luojiarui
 * @Date 2023/2/20 21:48
 * @Version 1.0
 **/
@ApiModel("用户角色对象")
@Data
public class UserRoleDTO {

    @ApiModelProperty(value = "用户主键", required = true)
    @NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
    private String userId;

    @ApiModelProperty(value = "用户角色", notes = "可选的参数有 USER, ADMIN, NO", required = true)
    @NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
    private PermissionEnum role;
}
