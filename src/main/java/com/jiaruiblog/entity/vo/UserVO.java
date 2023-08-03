package com.jiaruiblog.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jiaruiblog.auth.PermissionEnum;
import lombok.Data;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * @ClassName UserVO
 * @Description 返回查询的用户结果
 * @Author luojiarui
 * @Date 2023/2/18 00:14
 * @Version 1.0
 **/
@Data
public class UserVO {

    @Id
    private String id;

    @NotBlank(message = "非空")
    private String username;

    private String phone;

    private String mail;

    private Boolean male = null;

    private String description;

    private String avatar;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date birthtime;

    // 封禁状态
    private Boolean banning = false;

    private PermissionEnum permissionEnum;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastLogin;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateDate;

}
