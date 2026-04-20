package com.jiaruiblog.api.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.Locale;

@Configuration
public class LocaleConfig {

    @Bean
    public LocaleResolver localeResolver() {
        // 创建组合解析器
        AcceptHeaderLocaleResolver headerResolver = new AcceptHeaderLocaleResolver();
        headerResolver.setDefaultLocale(Locale.ENGLISH);

        // 优先使用URL参数，没有参数时回退到请求头
        return new LocaleResolver() {
            @Override
            public Locale resolveLocale(HttpServletRequest request) {
                String lang = request.getParameter("lang");
                if (lang != null && !lang.isEmpty()) {
                    try {
                        return Locale.forLanguageTag(lang.replace("_", "-"));  // 处理下划线格式
                    } catch (Exception e) {
                        // 参数无效时回退
                    }
                }
                return headerResolver.resolveLocale(request);
            }

            @Override
            public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
                throw new UnsupportedOperationException("禁止动态修改Locale");
            }
        };
    }
}