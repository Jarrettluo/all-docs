package com.jiaruiblog.enums;

/**
 * @ClassName DocStateEnum
 * @Description 文档建立索引时候的状态
 * @Author luojiarui
 * @Date 2022/11/13 14:32
 * @Version 1.0
 **/
public enum DocStateEnum {

    /**
     * 建立索引时的等待状态，默认都是等待状态
     */
    WAITE(),
    /**
     * 进行中的状态
     */
    ON_PROCESS(),
    /**
     * 成功状态
     */
    SUCCESS(),
    /**
     * 失败状态
     */
    FAIL();

}
