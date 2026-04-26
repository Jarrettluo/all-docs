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
 * @ClassName BasicRegistryDTO
 * @Description 用户注册实体类（简化版 - 注册时仅需账号密码）
 * @author luojiarui
 * @Date 2026/4/25
 * @Version 1.0
 **/
@Schema(name = "用户注册对象（简化版）")
@Data
public class BasicRegistryDTO {

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
