package com.jiaruiblog.bootstrap;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Servlet configuration for web components scanning.
 *
 * Note: JwtFilter in com.jiaruiblog.api.filter has @WebFilter annotation commented out,
 * so it is registered via FilterRegistrationBean instead of @ServletComponentScan.
 * SensitiveFilter in com.jiaruiblog.api.intercepter is not a servlet filter.
 *
 * @author jiarui.luo
 */
@Configuration
public class ServletConfig {

    /**
     * Disables the default Spring character encoding filter registration.
     * Character encoding is handled by the application-level filter chain.
     */
    @Bean
    public FilterRegistrationBean<org.springframework.web.filter.CharacterEncodingFilter> characterEncodingFilterRegistration() {
        FilterRegistrationBean<org.springframework.web.filter.CharacterEncodingFilter> registration =
                new FilterRegistrationBean<>();
        registration.setEnabled(false);
        return registration;
    }
}
