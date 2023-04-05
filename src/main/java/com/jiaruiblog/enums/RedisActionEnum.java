package com.jiaruiblog.enums;

import com.jiaruiblog.util.converter.BaseEnum;

/**
 * @ClassName RedisActionEnum
 * @Description Redis缓存的动作
 * @Author luojiarui
 * @Date 2023/4/5 12:13
 * @Version 1.0
 **/
public enum RedisActionEnum implements BaseEnum {

    LIKE(1, "用户点赞"),

    COLLECT(2,"用户收藏"),

    COMMENT(3, "评论"),

    DEFAULT(4, "其他");


    private Integer code;

    private String description;

    RedisActionEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }


    @Override
    public Integer getCode() {
        return this.code;
    }

    public static RedisActionEnum getActionByCode(Integer code) {
        if (code == null) {
            return null;
        }
        RedisActionEnum[] values = RedisActionEnum.values();
        for (RedisActionEnum value : values) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }
}
