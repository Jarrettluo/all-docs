package com.jiaruiblog.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ErrorCodeTest {

    @Mock
    private MessageSource messageSource;

    @Test
    void testGetLocalizedMessage() {
        // 模拟 MessageSource 行为
        when(messageSource.getMessage(
                "error-code.success",
                null,
                Locale.ENGLISH
        )).thenReturn("Success");

        String result = ErrorCode.SUCCESS.getLocalizedMessage(messageSource, Locale.ENGLISH);
        assertEquals("Success", result);
    }

    @Test
    void testGetMessageWithArgs() {
        // 模拟带参数的 MessageSource 行为
        when(messageSource.getMessage(
                "error-code.params-error",
                new Object[]{"param1"},
                "error-code.params-error",
                Locale.ENGLISH
        )).thenReturn("Invalid parameter: param1");

        String result = ErrorCode.PARAMS_ERROR.getMessage(
                messageSource,
                Locale.ENGLISH,
                "param1"
        );
        assertEquals("Invalid parameter: param1", result);
    }

    @Test
    void testCacheBehavior() {
        // 测试缓存功能
        when(messageSource.getMessage(
                "error-code.user-not-found",
                null,
                "error-code.user-not-found",
                Locale.ENGLISH
        )).thenReturn("User not found");

        // 第一次调用应该触发 messageSource
        String result1 = ErrorCode.USER_NOT_FOUND.getMessage(
                messageSource,
                Locale.ENGLISH
        );

        // 第二次调用应该使用缓存
        String result2 = ErrorCode.USER_NOT_FOUND.getMessage(
                messageSource,
                Locale.ENGLISH
        );

        assertEquals("User not found", result1);
        assertEquals(result1, result2);
    }
}