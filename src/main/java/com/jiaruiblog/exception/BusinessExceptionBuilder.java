package com.jiaruiblog.exception;

/**
 * 业务异常构建器，提供流畅的API
 */
public class BusinessExceptionBuilder {
    private final ErrorCode errorCode;
    private Object[] messageArgs;
    private String detailMessage;
    private Throwable cause;

    private BusinessExceptionBuilder(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public static BusinessExceptionBuilder of(ErrorCode errorCode) {
        return new BusinessExceptionBuilder(errorCode);
    }

    public BusinessExceptionBuilder args(Object... messageArgs) {
        this.messageArgs = messageArgs;
        return this;
    }

    public BusinessExceptionBuilder detail(String detailMessage) {
        this.detailMessage = detailMessage;
        return this;
    }

    public BusinessExceptionBuilder cause(Throwable cause) {
        this.cause = cause;
        return this;
    }

    public BusinessException build() {
        return new BusinessException(errorCode, messageArgs, detailMessage, cause);
    }

    public void throwIt() {
        throw build();
    }
}