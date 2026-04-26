package com.jiaruiblog.infrastructure.config.mybatis;

import com.jiaruiblog.common.enums.PermissionEnum;

public class PermissionEnumTypeHandler extends EnumTypeHandler<PermissionEnum> {
    public PermissionEnumTypeHandler() {
        super(PermissionEnum.class);
    }
}