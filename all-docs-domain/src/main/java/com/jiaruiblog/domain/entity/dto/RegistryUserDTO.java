package com.jiaruiblog.domain.entity.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jiaruiblog.common.MessageConstant;
import com.jiaruiblog.common.RegexConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @ClassName RegistryUserDTO
 * @Description 用户注册实体类
 * @author luojiarui
 * @Date 2023/2/14 22:12
 * @Version 1.0
 **/
@Schema(name = "用户注册对象")
@Data
public class RegistryUserDTO {

    @Schema(description = "用户名", minLength = 3, maxLength = 32, required = true)
    @NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
    @Size(min = 3, max = 32, message = MessageConstant.PARAMS_LENGTH_REQUIRED)
    @Pattern(regexp = RegexConstant.NUM_WORD_REG, message = MessageConstant.PARAMS_FORMAT_ERROR)
    String username;

    @Schema(description = "用户密码", minLength = 3, maxLength = 32, required = true)
    @NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
    @Size(min = 3, max = 32, message = MessageConstant.PARAMS_LENGTH_REQUIRED)
    @Pattern(regexp = RegexConstant.NUM_WORD_REG, message = MessageConstant.PARAMS_FORMAT_ERROR)
    String password;

    @Schema(description = "用户邮箱", required = true)
    @NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
    @Pattern(regexp = RegexConstant.MAIL_REG, message = MessageConstant.PARAMS_FORMAT_ERROR)
    String mail;

    @Schema(description = "用户手机号", required = true)
    @NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
    @Pattern(regexp = RegexConstant.PHONE_REG, message = MessageConstant.PARAMS_FORMAT_ERROR)
    String phone;

    @Schema(description = "用户昵称", required = true)
    @NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
    @Size(min = 3, max = 32, message = MessageConstant.PARAMS_LENGTH_REQUIRED)
    @Pattern(regexp = RegexConstant.NUM_WORD_REG, message = MessageConstant.PARAMS_FORMAT_ERROR)
    String nickname;

    @Autowired
    @JsonIgnore
    @Schema(hidden = true)
    private BCryptPasswordEncoder passwordEncoder;

    public String getEncodePassword() {
        if (password == null) {
            return "";
        }
        return passwordEncoder.encode(password);
    }
}