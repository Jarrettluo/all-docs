package com.jiaruiblog.domain.entity.bo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jiaruiblog.common.enums.PermissionEnum;
import lombok.Data;

import java.util.Date;

/**
 * @ClassName UserBO
 * @Description 用户业务对象，包含用户基本信息、角色权限等
 * @author luojiarui
 * @Date 2024/7/23 17:37
 * @Version 1.0
 **/
@Data
public class UserBO {

    private String id;

    private String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private String phone;

    private String mail;

    private String nickname;

    private Boolean male = false;

    private String description;

    private Date birthtime;

    private PermissionEnum role;
}