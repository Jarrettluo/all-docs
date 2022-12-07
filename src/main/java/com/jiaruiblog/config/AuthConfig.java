package com.jiaruiblog.config;

import com.jiaruiblog.auth.AuthenticationInterceptor;
import com.jiaruiblog.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * @ClassName AuthConfig
 * @Description 登录拦截器配置，参考地址
 * https://cloud.tencent.com/developer/article/1860615
 * @Author luojiarui
 * @Date 2022/12/7 20:39
 * @Version 1.0
 **/
@Configuration
public class AuthConfig extends WebMvcConfigurationSupport {

    @Autowired
    IUserService userService;

    /**
     * @Author luojiarui
     * @Description 使用.excludePathPatterns(); 可剔除掉部分内容
     * @Date 21:08 2022/12/7
     * @Param [registry]
     * @return void
     **/
    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        //注册TestInterceptor拦截器
        registry.addInterceptor(new AuthenticationInterceptor(userService))
                .addPathPatterns("/**");
    }


}
