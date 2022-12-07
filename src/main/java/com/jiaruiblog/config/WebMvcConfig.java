package com.jiaruiblog.config;

import com.jiaruiblog.util.converter.IntegerCodeToEnumConverterFactory;
import com.jiaruiblog.util.converter.StringCodeToEnumConverterFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @ClassName WebMvcConfig
 * @Description 把传入的参数自动转换为枚举量
 * @Author luojiarui
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


}
