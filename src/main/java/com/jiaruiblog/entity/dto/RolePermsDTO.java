package com.jiaruiblog.entity.dto;

import com.jiaruiblog.common.MessageConstant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @author zys
 * @since 2024-06-27
 */
@Data
@ApiModel("角色分配权限")
public class RolePermsDTO {
	@ApiModelProperty(value = "角色主键", required = true)
	@NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
	private String roleId;

	@ApiModelProperty(value = "权限 ID 列表", required = true)
	@NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
	@Size(min = 1, message = MessageConstant.PARAMS_IS_NOT_NULL)
	private List<String> permsIds;
}
