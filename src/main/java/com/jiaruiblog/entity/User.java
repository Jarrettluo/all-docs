package com.jiaruiblog.entity;

import com.alibaba.fastjson.JSON;
import com.jiaruiblog.auth.PermissionEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * @ClassName User
 * @Description User
 * @Author luojiarui
 * @Date 2022/6/4 9:37 上午
 * @Version 1.0
 **/
@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    private String id;

    @NotBlank(message = "非空")
    private String username;

    private String password;

    private String phone;

    private String mail;

    // 默认为空，没有性别
    private Boolean male;

    private String description;

    private List<String> avatarList = new ArrayList<>();

    private String avatar;

    private Date birthtime;

    // 封禁状态
    private Boolean banning = false;

    private PermissionEnum permissionEnum;

    private Date lastLogin;

    private Date createDate;

    private Date updateDate;

    @Override
    public String toString () {
        return JSON.toJSONString(this);
    }

    // 管理员可以屏蔽掉某个用户，用户可以注销某个账号
}
