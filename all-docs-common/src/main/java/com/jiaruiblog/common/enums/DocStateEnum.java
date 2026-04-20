package com.jiaruiblog.common.enums;

import com.jiaruiblog.common.converter.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @ClassName DocStateEnum
 * @Description 文档建立索引时候的状态
 * @author luojiarui
 * @Date 2022/11/13 14:32
 * @Version 1.0
 **/
@Getter
@AllArgsConstructor
public enum DocStateEnum implements BaseEnum {

    /**
     * 建立索引时的等待状态，默认都是等待状态
     */
    WAITE(0, "等待状态"),
    /**
     * 进行中的状态
     */
    ON_PROCESS(1, "进行中"),
    /**
     * 成功状态
     */
    SUCCESS(2, "成功"),
    /**
     * 失败状态
     */
    FAIL(3, "失败");

    private final Integer code;
    private final String description;

}