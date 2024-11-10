package com.jiaruiblog.config;

import com.jiaruiblog.util.converter.IntegerCodeToEnumConverterFactory;
import com.jiaruiblog.util.converter.StringCodeToEnumConverterFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @ClassName WebMvcConfig
 * @Description 把传入的参数自动转换为枚举量
 * @author luojiarui
 * @Date 2022/6/19 5:09 下午
 * @Version 1.0
 **/
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * 枚举类的转换器工厂 addConverterFactory
     */
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(new IntegerCodeToEnumConverterFactory());
        registry.addConverterFactory(new StringCodeToEnumConverterFactory());
    }

    /**
     * CORS 配置，允许前端访问 Content-Disposition 头部信息
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")  // 根据需要调整允许的前端域名
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*")
                .exposedHeaders("Content-Disposition"); // 允许前端访问 Content-Disposition
    }
}
