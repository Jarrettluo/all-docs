package com.jiaruiblog.domain.entity.po;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jiaruiblog.common.enums.PermissionEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


/**
 * @author luojiarui
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private String id;

    private String username;

    @JsonIgnore
    private String password;

    private String phone;

    private String mail;

    // 默认为空，没有性别
    private Boolean male;

    private String description;

    private String avatar;

    private Date birthtime;

    // 封禁状态
    private Boolean banning = false;

    private PermissionEnum permissionEnum;

    private String nickname;

    private Date lastLogin;

    private Date createDate;

    private Date updateDate;

    // 管理员可以屏蔽掉某个用户，用户可以注销某个账号
}