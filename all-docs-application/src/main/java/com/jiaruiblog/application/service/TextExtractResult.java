package com.jiaruiblog.application.service;

/**
 * 文件解析结果封装类
 */
public class TextExtractResult {

    private final boolean success;
    private final String content;
    private final String errorMessage;

    private TextExtractResult(boolean success, String content, String errorMessage) {
        this.success = success;
        this.content = content;
        this.errorMessage = errorMessage;
    }

    public static TextExtractResult success(String content) {
        return new TextExtractResult(true, content, null);
    }

    public static TextExtractResult failure(String errorMessage) {
        return new TextExtractResult(false, null, errorMessage);
    }

    public static TextExtractResult empty() {
        return new TextExtractResult(true, "", null);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getContent() {
        return content;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
