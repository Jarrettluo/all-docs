package com.jiaruiblog.filter;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @ClassName CrosFilter
 * @Description TODO
 * @Author luojiarui
 * @Date 2022/8/4 10:43 下午
 * @Version 1.0
 **/
@WebFilter(filterName = "CORSFilter", urlPatterns = {"/*"})
//@WebFilter(filterName = "CorsFilter", urlPatterns = {"/comment/auth/*", "/secure/*"})
@Order(value = 1)
@Configuration
public class CorsFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;


        String curOrigin = request.getHeader("Origin");
        response.setHeader("Access-Control-Allow-Origin", curOrigin == null ? "true" : curOrigin);

        // 响应标头指定 指定可以访问资源的URI路径
//        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        //响应标头指定响应访问所述资源到时允许的⼀种或多种⽅法
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE");
        //设置 缓存可以⽣存的最⼤秒数
        response.setHeader("Access-Control-Max-Age", "3600");

        //设置  受⽀持请求标头
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with, access-control-allow-origin, authorization, id, username, content-type, version-info,");
        // 指⽰的请求的响应是否可以暴露于该页⾯。当true值返回时它可以被暴露
        response.setHeader("Access-Control-Allow-Credentials","true");
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}
