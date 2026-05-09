package com.jiaruiblog.infrastructure.config.mybatis;

import com.jiaruiblog.common.enums.ThumbSizeEnum;

public class ThumbSizeEnumTypeHandler extends EnumTypeHandler<ThumbSizeEnum> {
    public ThumbSizeEnumTypeHandler() {
        super(ThumbSizeEnum.class);
    }
}