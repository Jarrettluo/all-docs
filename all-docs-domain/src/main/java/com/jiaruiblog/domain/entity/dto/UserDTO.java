package com.jiaruiblog.domain.entity.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jiaruiblog.common.MessageConstant;
import com.jiaruiblog.common.RegexConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Date;

/**
 * @ClassName UserDTO
 * @Description 用户DTO（更新资料时使用）
 * @author luojiarui
 **/
@Data
public class UserDTO {

    private String id;

    @JsonIgnore
    private String password;

    @Schema(description = "用户手机号", required = true)
    @NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
    @Pattern(regexp = RegexConstant.PHONE_REG, message = MessageConstant.PARAMS_FORMAT_ERROR)
    private String phone;

    @Schema(description = "用户邮箱", required = true)
    @NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
    @Pattern(regexp = RegexConstant.MAIL_REG, message = MessageConstant.PARAMS_FORMAT_ERROR)
    private String mail;

    @Schema(description = "用户昵称", required = true)
    @NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
    @Size(min = 3, max = 32, message = MessageConstant.PARAMS_LENGTH_REQUIRED)
    private String nickname;

    private boolean male = false;

    private String description;

    private Date birthtime;

    private String role;

}