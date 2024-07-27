package com.jiaruiblog.entity.bo;

import com.jiaruiblog.auth.PermissionEnum;
import lombok.Data;

import java.util.Date;

/**
 * @ClassName UserBO
 * @Description TODO
 * @Author luojiarui
 * @Date 2024/7/23 17:37
 * @Version 1.0
 **/
@Data
public class UserBO {

    private String id;

    private String password;

    private String phone;

    private String mail;

    private Boolean male = false;

    private String description;

    private Date birthtime;

    private PermissionEnum role;
}
