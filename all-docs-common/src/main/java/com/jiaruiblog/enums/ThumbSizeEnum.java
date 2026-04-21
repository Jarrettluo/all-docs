package com.jiaruiblog.enums;

import com.jiaruiblog.common.converter.BaseEnum;

public enum ThumbSizeEnum implements BaseEnum {
    LARGE(1),
    MIDDLE(2),
    SMALL(3),
    TINY(4);

    private final Integer code;

    ThumbSizeEnum(int code) {
        this.code = code;
    }

    @Override
    public Integer getCode() {
        return this.code;
    }
}