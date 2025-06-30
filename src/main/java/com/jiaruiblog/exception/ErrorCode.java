package com.jiaruiblog.exception;

import lombok.Getter;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public enum ErrorCode {

    // HTTP Status Codes
    NOT_FOUND(404, "error-code.not-found"),
    INVALID_PARAM(400, "error-code.bad-request"),
    UNAUTHORIZED(401, "error-code.unauthorized"),
    FORBIDDEN(403, "error-code.forbidden"),
    INTERNAL_ERROR(500, "error-code.internal-server-error"),

    // User Related
    USER_NOT_FOUND(1001, "error-code.user-not-found"),
    INVALID_CREDENTIALS(1002, "error-code.invalid-credentials"),
    EMAIL_EXISTS(1003, "error-code.email-already-exists"),
    USERNAME_EXISTS(1004, "error-code.username-already-exists"),
    USER_DISABLED(1005, "error-code.user-disabled"),
    ACCOUNT_LOCKED(1006, "error-code.account-locked"),
    PASSWORD_EXPIRED(1007, "error-code.password-expired"),
    INVALID_PASSWORD(1008, "error-code.invalid-password"),

    // File Related
    FILE_NOT_FOUND(2001, "error-code.file-not-found"),
    FILE_UPLOAD_FAILED(2002, "error-code.file-upload-failed"),
    FILE_DOWNLOAD_FAILED(2003, "error-code.file-download-failed"),
    FILE_DELETE_FAILED(2004, "error-code.file-delete-failed"),
    FILE_SIZE_EXCEEDED(2005, "error-code.file-size-exceeded"),
    INVALID_FILE_TYPE(2006, "error-code.invalid-file-type"),
    STORAGE_QUOTA_EXCEEDED(2007, "error-code.storage-quota-exceeded"),

    // Document Related
    DOCUMENT_NOT_FOUND(3001, "error-code.document-not-found"),
    DOCUMENT_UPDATE_FAILED(3002, "error-code.document-update-failed"),
    DOCUMENT_DELETE_FAILED(3003, "error-code.document-delete-failed"),
    DOCUMENT_VERSION_CONFLICT(3004, "error-code.document-version-conflict"),

    // Team Related
    TEAM_NOT_FOUND(4001, "error-code.team-not-found"),
    TEAM_MEMBER_EXISTS(4002, "error-code.team-member-exists"),
    TEAM_MEMBER_NOT_FOUND(4003, "error-code.team-member-not-found"),
    TEAM_QUOTA_EXCEEDED(4004, "error-code.team-quota-exceeded"),

    // Organization Related
    ORGANIZATION_NOT_FOUND(5001, "error-code.organization-not-found"),
    ORGANIZATION_MEMBER_EXISTS(5002, "error-code.organization-member-exists"),
    ORGANIZATION_MEMBER_NOT_FOUND(5003, "error-code.organization-member-not-found"),

    // Permission Related
    PERMISSION_DENIED(6001, "error-code.permission-denied"),
    INVALID_PERMISSION(6002, "error-code.invalid-permission"),
    ROLE_NOT_FOUND(6003, "error-code.role-not-found"),

    // Category & Tag Related
    CATEGORY_NOT_FOUND(7001, "error-code.category-not-found"),
    TAG_NOT_FOUND(7002, "error-code.tag-not-found"),
    CATEGORY_EXISTS(7003, "error-code.category-exists"),
    TAG_EXISTS(7004, "error-code.tag-exists"),

    // Comment Related
    COMMENT_NOT_FOUND(8001, "error-code.comment-not-found"),
    COMMENT_DELETE_FAILED(8002, "error-code.comment-delete-failed"),

    // System Configuration
    CONFIG_NOT_FOUND(9001, "error-code.config-not-found"),
    CONFIG_UPDATE_FAILED(9002, "error-code.config-update-failed");


    private final Integer code;
    private final String messageKey;

    ErrorCode(Integer code, String messageKey) {
        this.code = code;
        this.messageKey = messageKey;
    }

    public String getLocalizedMessage(MessageSource messageSource, Locale locale) {
        Locale targetLocale = (locale != null) ? locale : LocaleContextHolder.getLocale();
        return messageSource.getMessage(
                this.messageKey,
                null,
                targetLocale
        );
    }

    private static Map<Locale, Map<ErrorCode, String>> cache = new ConcurrentHashMap<>();

    public String getMessage(MessageSource messageSource, Locale locale, Object... args) {
        return cache.computeIfAbsent(locale, l -> new ConcurrentHashMap<>())
                .computeIfAbsent(this,
                        ec -> messageSource.getMessage(
                                this.messageKey,
                                args,
                                this.messageKey,
                                locale));
    }
}