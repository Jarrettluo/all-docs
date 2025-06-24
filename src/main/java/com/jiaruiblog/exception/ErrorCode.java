package com.jiaruiblog.exception;

import lombok.Getter;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

/**
 * <p>错误码枚举（如 NOT_FOUND, INVALID_PARAM）</p>
 * <p>错误码枚举类，定义常见的业务错误码</p>
 * edit at 2025/3/3 23:14
 *
 * @author Jarrett Luo
 * @version 1.0
 */
@Getter
public enum ErrorCode {
//
//    // 数据未找到
//    NOT_FOUND(404, "资源未找到"),
//
//    // 参数无效
//    INVALID_PARAM(400, "参数无效"),
//
//    // 其他业务错误码
//    UNAUTHORIZED(401, "未授权"),
//
//    FORBIDDEN(403, "禁止访问"),
//
//    INTERNAL_ERROR(500, "服务器内部错误");
//

    // HTTP Status Codes
    NOT_FOUND(404, "error-code.not-found"),
    INVALID_PARAM(400, "error-code.bad-request"),
    UNAUTHORIZED(401, "error-code.unauthorized"),
    FORBIDDEN(403, "error-code.forbidden"),
    INTERNAL_ERROR(500, "error-code.internal-server-error"),

    // Custom Business Errors
    USER_NOT_FOUND(1001, "error-code.user-not-found"),
    INVALID_CREDENTIALS(1002, "error-code.invalid-credentials"),
    EMAIL_EXISTS(1003, "error-code.email-already-exists"),
    USERNAME_EXISTS(1004, "用户名已存在");


    /**
     * -- GETTER --
     *  获取错误码
     *
     */
    // 错误码
    private final Integer code;

    /**
     * -- GETTER --
     *  获取错误信息
     *
     */
    // 错误信息
    private final String messageKey;

    /**
     * 构造方法
     *
     * @param code    错误码
     * @param messageKey 错误信息
     */
    ErrorCode(Integer code, String messageKey) {
        this.code = code;
        this.messageKey = messageKey;
    }

    /**
     * Resolve the localized message using Spring's MessageSource.
     *
     * @param messageSource Autowired MessageSource
     * @param locale        Target locale (if null, falls back to LocaleContextHolder)
     * @return Localized error message
     */
    public String getLocalizedMessage(MessageSource messageSource, Locale locale) {
        Locale targetLocale = (locale != null) ? locale : LocaleContextHolder.getLocale();
        return messageSource.getMessage(
                this.messageKey,
                null,
                targetLocale
        );
    }

}