package com.jiaruiblog.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
@AllArgsConstructor
public enum PermissionEnum {

    /**
     * 用户权限
     */
    USER(1, "用户权限"),
    /**
     * 管理员管理权限
     */
    ADMIN(2, "管理员管理权限"),

    /**
     * 无需校验,
     */
    NO(-99999, "无需权限"),
    ;
    /**
     * 权限编码
     */
    private final Integer code;
    /**
     * 权限名称
     */
    private final String msg;

    public static PermissionEnum getRoleByName(String name) {
        if (StringUtils.isEmpty(name)) {
            return null;
        }
        for (PermissionEnum value : PermissionEnum.values()) {
            if (value.name().equals(name)) {
                return value;
            }
        }
        return null;
    }

}
