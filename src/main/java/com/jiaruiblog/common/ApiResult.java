package com.jiaruiblog.common;

import com.jiaruiblog.exception.ErrorCode;
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
    public static <T> ApiResult<T> success() {
        return new ApiResult<>(200, "success", null);
    }

    // 快速构建成功响应（仅消息，无数据）
    public static <T> ApiResult<T> error(int code, String message) {
        return new ApiResult<>(code, message, null);
    }

    public static <T> ApiResult<T> error(ErrorCode errorCode) {
        return new ApiResult<>(errorCode.getCode(), errorCode.getMessageKey(), null);
    }

    // 国际化支持的核心方法
    public static <T> ApiResult<T> error(ErrorCode errorCode,
                                         MessageSource messageSource,
                                         Locale locale,
                                         Object... args) {
        String localizedMessage = messageSource.getMessage(
                errorCode.getMessageKey(),
                args,
                errorCode.getMessageKey(), // 默认回退到messageKey
                locale);
        return new ApiResult<>(errorCode.getCode(), localizedMessage, null);
    }

    // 支持详细错误信息的版本
    public static <T> ApiResult<T> error(ErrorCode errorCode,
                                         MessageSource messageSource,
                                         Locale locale,
                                         String detailMessage,
                                         Object... args) {
        String localizedMessage = messageSource.getMessage(
                errorCode.getMessageKey(),
                args,
                errorCode.getMessageKey(),
                locale);

        // 可以将detailMessage附加到主消息中，或者作为data的一部分
        String fullMessage = detailMessage != null
                ? localizedMessage + " (" + detailMessage + ")"
                : localizedMessage;

        return new ApiResult<>(errorCode.getCode(), fullMessage, null);
    }
}
