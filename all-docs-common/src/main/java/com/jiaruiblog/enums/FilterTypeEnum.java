package com.jiaruiblog.enums;

import com.jiaruiblog.common.converter.BaseEnum;

/**
 * @author jiarui.luo
 */
public enum FilterTypeEnum implements BaseEnum {

    /**
     * all
     */
    ALL(1),

    /**
     * filter
     */
    FILTER(2),

    /**
     * category
     */
    CATEGORY(3),

    /**
     * tag
     */
    TAG(4),

    /**
     * only search filename
     **/
    FILE_NAME(5);

    private Integer code;

    FilterTypeEnum(int code) {
        this.code = code;
    }

    @Override
    public Integer getCode() {
        return this.code;
    }
}
