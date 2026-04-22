package com.jiaruiblog.common;

import com.jiaruiblog.common.exception.ErrorCode;
import org.springframework.context.MessageSource;

import java.util.Locale;

/**
 * @ClassName ApiResult
 * @Description 通用的API响应数据载体
 * @author luojiarui
 * @Date 2022/6/4 5:12 下午
 * @Version 1.0
 **/
public record ApiResult<T>(int code, String message, T data) {

    private static MessageSource messageSource;

    public static void setMessageSource(MessageSource ms) {
        messageSource = ms;
    }

    // ==================== 成功响应 ====================

    public static <T> ApiResult<T> success() {
        return new ApiResult<>(200, "success", null);
    }

    public static <T> ApiResult<T> success(T data) {
        return new ApiResult<>(200, "success", data);
    }

    public static <T> ApiResult<T> success(String message, T data) {
        return new ApiResult<>(200, message, data);
    }

    // ==================== 错误响应 ====================

    public static <T> ApiResult<T> error(int code, String message) {
        return new ApiResult<>(code, message, null);
    }

    public static <T> ApiResult<T> error(ErrorCode errorCode) {
        return new ApiResult<>(errorCode.getCode(), errorCode.getMessageKey(), null);
    }

    public static <T> ApiResult<T> error(ErrorCode errorCode, Locale locale) {
        if (messageSource == null) {
            return new ApiResult<>(errorCode.getCode(), errorCode.getMessageKey(), null);
        }
        String localizedMessage = messageSource.getMessage(
                errorCode.getMessageKey(), null, errorCode.getMessageKey(), locale);
        return new ApiResult<>(errorCode.getCode(), localizedMessage, null);
    }

    public static <T> ApiResult<T> error(ErrorCode errorCode, Locale locale, String detailMessage) {
        String localizedMessage = messageSource != null
                ? messageSource.getMessage(errorCode.getMessageKey(), null, errorCode.getMessageKey(), locale)
                : errorCode.getMessageKey();
        String fullMessage = detailMessage != null ? localizedMessage + " (" + detailMessage + ")" : localizedMessage;
        return new ApiResult<>(errorCode.getCode(), fullMessage, null);
    }
}