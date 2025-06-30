package com.jiaruiblog.controller;

import com.jiaruiblog.common.ApiResult;
import com.jiaruiblog.exception.BusinessExceptionBuilder;
import com.jiaruiblog.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.LocaleResolver;

import java.util.Locale;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private LocaleResolver localeResolver; // 新增注入

    @GetMapping("/i18n")
    public String testI18n(@RequestParam String code, HttpServletRequest request) {
        Locale actualLocale = localeResolver.resolveLocale(request); // 直接调用解析器
        return messageSource.getMessage(
                code,
                null,
                actualLocale);
    }

    @GetMapping("/luo")
    public ApiResult<Object> testI18n2(@RequestParam String code) {
        if (StringUtils.isNoneBlank(code)) {
            throw BusinessExceptionBuilder.of(ErrorCode.FILE_SIZE_EXCEEDED).args(code).build();
        }
        return ApiResult.success(ErrorCode.SUCCESS.getMessageKey());
    }
}