package com.jiaruiblog.entity.dto;

import com.jiaruiblog.auth.PermissionEnum;
import com.jiaruiblog.common.MessageConstant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

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

    @ApiModelProperty(value = "用户角色", notes = "可选的参数有 USER, ADMIN, NO", hidden = true)
    // @NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
    private PermissionEnum role;

    @ApiModelProperty(value = "用户角色ID 列表", required = true)
    @NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
    @Size(min = 1, message = MessageConstant.PARAMS_IS_NOT_NULL)
    private List<String> roleIds;
}
