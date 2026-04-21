package com.jiaruiblog.bootstrap;

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
}
