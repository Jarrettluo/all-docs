package com.jiaruiblog.bootstrap;

import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Servlet configuration for web components scanning.
 *
 * @author jiarui.luo
 */
@Configuration
@ServletComponentScan(basePackages = "com.jiaruiblog.api.filter")
public class ServletConfig {
}
