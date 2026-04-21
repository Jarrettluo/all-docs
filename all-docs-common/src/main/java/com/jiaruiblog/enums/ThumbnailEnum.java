package com.jiaruiblog.enums;

import com.jiaruiblog.common.converter.BaseEnum;

/**
 * @ClassName ThumbnailEnum
 * @Description ThumbnailEnum
 * @author luojiarui
 * @Date 2022/7/23 5:58 下午
 * @Version 1.0
 **/
public enum ThumbnailEnum implements BaseEnum {

    // 用户的头像缩略图
    USER(1),

    // 文档类，pdf文档的保存
    DOC_PDF(2);

    private final Integer code;

    ThumbnailEnum(int code) {
        this.code = code;
    }

    @Override
    public Integer getCode() {
        return this.code;
    }
}