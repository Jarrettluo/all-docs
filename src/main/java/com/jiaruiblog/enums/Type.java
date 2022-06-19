package com.jiaruiblog.enums;

import com.jiaruiblog.utils.enumsCoverterUtils.BaseEnum;

public enum Type implements BaseEnum {

    ALL(1),
    FILTER(2),
    CATEGORY(3),
    TAG(4);

    private Integer code;

    Type(int code) {
        this.code = code;
    }

    @Override
    public Integer getCode() {
        return this.code;
    }
}
