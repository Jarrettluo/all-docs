package com.jiaruiblog.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PermissionEnum {

    /**
     * 用户管理权限
     */
    USER(1, "用户管理权限"),
    /**
     * 教师管理权限
     */
    TEACHER(2, "教师管理权限"),

    /**
     * 无需校验,
     */
    NO(-99999, "无需权限"),
    ;
    /**
     * 权限编码
     */
    private Integer code;
    /**
     * 权限名称
     */
    private String msg;

}
