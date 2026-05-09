package com.jiaruiblog.api.config;

import com.jiaruiblog.api.filter.JwtFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author luojiarui
 **/
@Configuration
public class JwtFilterConfig {

    @Bean
    public FilterRegistrationBean<JwtFilter> jwtFilterRegistration() {
        FilterRegistrationBean<JwtFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new JwtFilter());
        registration.addUrlPatterns("/*");
        registration.setName("JwtFilter");
        registration.setOrder(1);
        return registration;
    }
}