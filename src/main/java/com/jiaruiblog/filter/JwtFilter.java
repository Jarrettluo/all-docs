package com.jiaruiblog.filter;

import com.auth0.jwt.interfaces.Claim;
import com.jiaruiblog.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * JWT过滤器，拦截 /secure的请求
 * //@WebFilter(filterName = "JwtFilter", urlPatterns = "/secure/*")
 * 参考地址：https://blog.csdn.net/CSDN2497242041/article/details/115605626
 * @author jiarui.luo
 * @date 2022年10月8日
 * @version v2.0
 */
@Slf4j
@WebFilter(filterName = "JwtFilter", urlPatterns = {"/*"})
public class JwtFilter implements Filter
{

    private static final String OPTIONS = "OPTIONS";

    /**
     * 安全的url，不需要令牌; 游客可以访问的
     */
    private static final List<String> SAFE_URL_LIST = Arrays.asList("*/user/login", "*/user/register");



    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest) req;
        final HttpServletResponse response = (HttpServletResponse) res;

        response.setCharacterEncoding("UTF-8");
        String url = request.getRequestURI().substring(request.getContextPath().length());
        // 登录和注册等请求不需要令牌
        if (url.contains("login") || url.contains("/user/insert") || url.contains("/files/view") || url.contains("/files/image2")) {
            response.setStatus(HttpServletResponse.SC_OK);
            chain.doFilter(request, response);
            return;
        }

        //获取 header里的token
        final String token = request.getHeader("authorization");

        if (OPTIONS.equals(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            chain.doFilter(request, response);
        }
        // Except OPTIONS, other request should be checked by JWT
        else {
            if (token == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            // 校验token是否合法，如果合法则去除其中的信息放到request中
            Map<String, Claim> userData = JwtUtil.verifyToken(token);
            if (CollectionUtils.isEmpty(userData)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            } else {
                //拦截器 拿到用户信息，放到request中
                request.setAttribute("id", userData.get("id").asString());
                request.setAttribute("username", userData.get("username").asString());
                request.setAttribute("password", userData.get("password").asString());
                chain.doFilter(req, res);
            }
        }
    }

}