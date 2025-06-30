package com.jiaruiblog.exception;

/**
 * <p>业务异常类，用于处理业务逻辑中的异常情况</p>
 *
 **/
/**
 * 业务异常基类
 */
import lombok.Getter;

import java.util.Arrays;

/**
 * 业务异常类，支持国际化、错误码和消息占位符
 */
public class BusinessException extends RuntimeException {
    // Getters
    @Getter
    private final ErrorCode errorCode;
    private final Object[] messageArgs;
    @Getter
    private final String detailMessage;

    /**
     * 构造方法 - 基础版
     */
    public BusinessException(ErrorCode errorCode) {
        this(errorCode, (Object[]) null, null, null);
    }

    /**
     * 构造方法 - 带消息参数
     */
    public BusinessException(ErrorCode errorCode, Object... messageArgs) {
        this(errorCode, messageArgs, null, null);
    }

    /**
     * 构造方法 - 带详细消息
     */
    public BusinessException(ErrorCode errorCode, String detailMessage) {
        this(errorCode, null, detailMessage, null);
    }

    /**
     * 构造方法 - 带消息参数和详细消息
     */
    public BusinessException(ErrorCode errorCode, Object[] messageArgs, String detailMessage) {
        this(errorCode, messageArgs, detailMessage, null);
    }

    /**
     * 构造方法 - 完整版
     */
    public BusinessException(ErrorCode errorCode, Object[] messageArgs, String detailMessage, Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
        this.messageArgs = messageArgs != null ? messageArgs.clone() : null;
        this.detailMessage = detailMessage;
    }

    public Object[] getMessageArgs() {
        return messageArgs != null ? messageArgs.clone() : null;
    }

    @Override
    public String getMessage() {
        return String.format("BusinessException: code=%s, messageKey=%s, args=%s, detail=%s",
                errorCode.getCode(),
                errorCode.getMessageKey(),
                Arrays.toString(messageArgs),
                detailMessage);
    }
}