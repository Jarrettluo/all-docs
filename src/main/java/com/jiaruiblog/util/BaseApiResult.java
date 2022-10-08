package com.jiaruiblog.util;

import java.io.Serializable;

/**
 * 通用返回体.
 * https://github.com/KimZing/kimzing-utils/blob/9952c1c36af5c2ba78102632c697c2c6fac54bad/src/main/java/com/kimzing/utils/result/ApiResult.java
 * @author KimZing - kimzing@163.com
 * @since 2019/12/4 15:04
 */
public abstract class BaseApiResult implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 当前时间戳
     */
    public Long timestamp;

    /**
     * 状态码,0-成功，其他-失败
     */
    public Integer code;

    /**
     * 创建成功返回体，无数据
     *
     * @return ApiResult
     */
    public static BaseApiResult success() {
        return new SuccessApiResult();
    }

    /**
     * 创建成功返回体，包含数据
     *
     * @param data 数据体
     * @return ApiResult
     */
    public static <T> BaseApiResult success(T data) {
        return new SuccessApiResult<T>(data);
    }

    /**
     * 创建错误返回体
     *
     * @param code 错误码
     * @param message 错误信息
     * @return ApiResult
     */
    public static BaseApiResult error(Integer code, String message) {
        return new ErrorApiResult(code, message);
    }

    public Integer getCode() {
        return code;
    }
}
