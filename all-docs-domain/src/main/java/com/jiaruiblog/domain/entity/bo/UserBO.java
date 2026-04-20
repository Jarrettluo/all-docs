package com.jiaruiblog.domain.entity.bo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jiaruiblog.common.enums.PermissionEnum;
import lombok.Data;

import java.util.Date;

/**
 * @ClassName UserBO
 * @Description TODO
 * @author luojiarui
 * @Date 2024/7/23 17:37
 * @Version 1.0
 **/
@Data
public class UserBO {

    private String id;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private String phone;

    private String mail;

    private Boolean male = false;

    private String description;

    private Date birthtime;

    private PermissionEnum role;
}