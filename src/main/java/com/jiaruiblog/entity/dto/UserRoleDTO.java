package com.jiaruiblog.entity.dto;

import com.jiaruiblog.auth.PermissionEnum;
import com.jiaruiblog.common.MessageConstant;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @ClassName UserSexDTO
 * @Description TODO
 * @Author luojiarui
 * @Date 2023/2/20 21:48
 * @Version 1.0
 **/
@Data
public class UserRoleDTO {

    @NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
    private String userId;

    @NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
    private PermissionEnum role;
}
