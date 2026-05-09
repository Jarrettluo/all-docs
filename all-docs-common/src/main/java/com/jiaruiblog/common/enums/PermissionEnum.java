package com.jiaruiblog.common.enums;

import com.jiaruiblog.common.converter.BaseEnum;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Jarrett Luo
 * @Date 2022/10/24 11:39
 * @Version 1.0
 */
public enum PermissionEnum implements BaseEnum {

    USER(1, "用户权限"),
    ADMIN(2, "管理员管理权限"),
    NO(-99999, "无需权限");

    private final Integer code;
    private final String msg;

    PermissionEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public Integer getCode() {
        return code;
    }

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