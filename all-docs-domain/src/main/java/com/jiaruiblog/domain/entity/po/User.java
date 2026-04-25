package com.jiaruiblog.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jiaruiblog.common.enums.PermissionEnum;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


/**
 * @ClassName User
 * @Description User
 * @author luojiarui
 * @Date 2022/6/4 9:37 上午
 * @Version 1.0
 **/
@Table(name = "user")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    private String id;

    @NotBlank(message = "非空")
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

    @Enumerated(EnumType.STRING)
    @Column(name = "permission_enum")
    private PermissionEnum permissionEnum;

    private String nickname;

    private Date lastLogin;

    private Date createDate;

    private Date updateDate;

    // 管理员可以屏蔽掉某个用户，用户可以注销某个账号
}