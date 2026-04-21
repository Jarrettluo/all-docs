package com.jiaruiblog.common;

/**
 * @ClassName ApiResult
 * @Description 通用的API响应数据载体
 * @author luojiarui
 * @Date 2022/6/4 5:12 下午
 * @Version 1.0
 **/
public record ApiResult<T>(int code, String message, T data) {

    /**
     * 快速构建成功响应（带数据和默认消息）
     * @deprecated Use {@link ApiResultFactory#success(Object)} instead
     */
    public static <T> ApiResult<T> success(T data) {
        return new ApiResult<>(200, "success", data);
    }

    /**
     * 快速构建成功响应（带自定义消息和数据）
     * @deprecated Use {@link ApiResultFactory#success(String, Object)} instead
     */
    public static <T> ApiResult<T> success(String message, T data) {
        return new ApiResult<>(200, message, data);
    }

    /**
     * 快速构建成功响应（仅消息，无数据）
     * @deprecated Use {@link ApiResultFactory#success(String)} instead
     */
    public static <T> ApiResult<T> success(String message) {
        return new ApiResult<>(200, message, null);
    }

    /**
     * 快速构建成功响应（无数据，使用默认消息）
     * @deprecated Use {@link ApiResultFactory#success()} instead
     */
    public static <T> ApiResult<T> success() {
        return new ApiResult<>(200, "success", null);
    }

    /**
     * 快速构建错误响应
     * @deprecated Use {@link ApiResultFactory#error(int, String)} instead
     */
    public static <T> ApiResult<T> error(int code, String message) {
        return new ApiResult<>(code, message, null);
    }

    /**
     * 快速构建错误响应
     * @deprecated Use {@link ApiResultFactory#error(ErrorCode)} instead
     */
    public static <T> ApiResult<T> error(com.jiaruiblog.common.exception.ErrorCode errorCode) {
        return new ApiResult<>(errorCode.getCode(), errorCode.getMessageKey(), null);
    }

    /**
     * 国际化支持的错误响应
     * @deprecated Use {@link ApiResultFactory#error(ErrorCode, MessageSource, Locale, Object...)} instead
     */
    public static <T> ApiResult<T> error(com.jiaruiblog.common.exception.ErrorCode errorCode,
                                         org.springframework.context.MessageSource messageSource,
                                         java.util.Locale locale,
                                         Object... args) {
        String localizedMessage = messageSource.getMessage(
                errorCode.getMessageKey(),
                args,
                errorCode.getMessageKey(),
                locale);
        return new ApiResult<>(errorCode.getCode(), localizedMessage, null);
    }

    /**
     * 支持详细错误信息的版本
     * @deprecated Use {@link ApiResultFactory#error(ErrorCode, MessageSource, Locale, String, Object...)} instead
     */
    public static <T> ApiResult<T> error(com.jiaruiblog.common.exception.ErrorCode errorCode,
                                         org.springframework.context.MessageSource messageSource,
                                         java.util.Locale locale,
                                         String detailMessage,
                                         Object... args) {
        String localizedMessage = messageSource.getMessage(
                errorCode.getMessageKey(),
                args,
                errorCode.getMessageKey(),
                locale);

        String fullMessage = detailMessage != null
                ? localizedMessage + " (" + detailMessage + ")"
                : localizedMessage;

        return new ApiResult<>(errorCode.getCode(), fullMessage, null);
    }
}