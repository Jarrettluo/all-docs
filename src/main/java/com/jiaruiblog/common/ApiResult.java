package com.jiaruiblog.common;

import org.apache.poi.ss.formula.functions.T;
import org.springframework.context.MessageSource;

import java.util.Locale;

public record ApiResult<T>(int code, String message, T data) {

// 快速构建成功响应（带数据和默认消息）
public static <T> ApiResult<T> success(T data) {
    return new ApiResult<>(200, "success", data);
}

// 快速构建成功响应（带自定义消息和数据）
public static <T> ApiResult<T> success(String message, T data) {
    return new ApiResult<>(200, message, data);
}

// 快速构建成功响应（仅消息，无数据）
public static <T> ApiResult<T> success(String message) {
    return new ApiResult<>(200, message, null);
}

// 快速构建成功响应（仅消息，无数据）
public static <T> ApiResult<T> error(int code, String message) {
    return new ApiResult<>(code, message, null);
}

// 快速构建成功响应（仅消息，无数据）
public static <T> ApiResult<T> error(String codeString, String message) {
    return new ApiResult<>(Integer.parseInt(codeString), message, null);
}

public static <T> ApiResult<T> error(ErrorCode errorCode, MessageSource messageSource, Locale locale) {
    String localizedMessage = errorCode.getLocalizedMessage(messageSource, locale);
    return new ApiResult<>(errorCode.getCode(), localizedMessage, null);
}
}