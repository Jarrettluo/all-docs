package com.jiaruiblog.exception;

import org.springframework.context.MessageSource;

import java.util.Locale;

/**
 * <p>业务异常类，用于处理业务逻辑中的异常情况</p>
 *
 **/
public class BusinessException extends RuntimeException {

    private Integer code;

    private final transient Object[] args;  // For parameterized messages

    /**
     * 构造方法，传入错误码枚举
     *
     * @param errorCode 错误码枚举
     */
    public BusinessException(ErrorCode errorCode, MessageSource messageSource, Locale locale) {
        super(errorCode.getLocalizedMessage(messageSource, locale));
        this.code = errorCode.getCode();
        this.args = null;
    }

    public BusinessException(ErrorCode errorCode, Object[] args, MessageSource messageSource, Locale locale) {
        super(messageSource.getMessage(errorCode.getMessageKey(), args, locale));
        this.code = errorCode.getCode();
        this.args = args;
    }

    /**
     * 构造方法，传入错误码枚举
     *
     * @param errorCode 错误码枚举
     */
    public BusinessException(int errorCode, String msg) {
        super(msg);
        this.code = errorCode;
        this.args = null;
    }


    /**
     * 构造方法，传入错误码枚举和异常原因
     *
     * @param errorCode 错误码枚举
     * @param cause     异常原因
     */
    public BusinessException(ErrorCode errorCode,  MessageSource messageSource, Locale locale, Throwable cause) {
        super(errorCode.getLocalizedMessage(messageSource, locale), cause);
        this.code = errorCode.getCode();
        this.args = null;
    }

    /**
     * 获取错误信息
     *
     * @return 错误信息
     */
    public String getErrorMessage() {
        return super.getLocalizedMessage();
    }

    public Integer getCode() {
        return this.code;
    }
}