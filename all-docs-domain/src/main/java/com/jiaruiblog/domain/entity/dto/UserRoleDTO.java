package com.jiaruiblog.domain.entity.dto;

import com.jiaruiblog.common.enums.PermissionEnum;
import com.jiaruiblog.common.MessageConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


/**
 * 用户角色数据传输对象
 *
 * @author luojiarui
 **/
@Schema(description = "用户角色对象")
@Data
public class UserRoleDTO {

    @Schema(description = "用户主键", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
    private String userId;

    @Schema(description = "用户角色", allowableValues = {"USER", "ADMIN", "NO"}, requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
    private PermissionEnum role;
}