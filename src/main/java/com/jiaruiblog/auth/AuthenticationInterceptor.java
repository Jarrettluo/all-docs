package com.jiaruiblog.auth;

import com.jiaruiblog.entity.User;
import com.jiaruiblog.service.IUserService;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * @ClassName AuthenticationInterceptor
 * @Description 权限校验，拦截器
 * 参考文章： 注解式权限校验 https://blog.csdn.net/LitongZero/article/details/103628706
 * @Author luojiarui
 * @Date 2022/12/7 20:24
 * @Version 1.0
 **/
public class AuthenticationInterceptor implements HandlerInterceptor {

    // 拦截器中无法注入bean，因此使用构造器
    private final IUserService userService;

    public AuthenticationInterceptor(IUserService userService) {
        this.userService = userService;
    }



    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 如果不是映射到方法直接通过
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        // 获取方法中的注解
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        // 省略判断是否需要登录的方法.....

        // 获取类注解
        Permission permissionClass = AnnotationUtils.findAnnotation(handlerMethod.getBeanType(), Permission.class);
        // 获取方法注解
        Permission permissionMethod = AnnotationUtils.findAnnotation(method,Permission.class);

        // 判断是否需要权限校验
        if (permissionClass == null && permissionMethod == null) {
            // 不需要校验权限，直接放行
            return true;
        }

        // 省略Token解析的方法.....

        //获取 header里的token
//        final String token = request.getHeader("authorization");
//        if (token == null) {
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            return false;
//        }
//        Map<String, Claim> userData = JwtUtil.verifyToken(token);
//
//        if (CollectionUtils.isEmpty(userData)) {
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            return false;
//        }
//
//        User userInfo = userService.queryById(userData.get("id").asString());

        User userInfo = new User();

        // 此处根据自己的系统架构，通过Token或Cookie等获取用户信息。
//        UserInfo userInfo = userService.getUserByToken(token);


        // 获取该方法注解，优先级:方法注解>类注解
        PermissionEnum[] permissionEnums;
        if (permissionClass != null && permissionMethod == null) {
            // 类注解不为空，方法注解为空，使用类注解
            permissionEnums = permissionClass.name();
        } else if (permissionClass == null) {
            // 类注解为空，使用方法注解
            permissionEnums = permissionMethod.name();
        } else {
            // 都不为空，使用方法注解
            permissionEnums = permissionMethod.name();
        }

//        System.out.println(
//                permissionEnums
//        );
//
//        System.out.println(userService);

        // 校验该用户是否有改权限
        // 校验方法可自行实现，拿到permissionEnums中的参数进行比较
        if(userService.checkPermissionForUser(userInfo, permissionEnums)){
             // 拥有权限
            return true;
        } else {
             // 抛出自定义异常，可在全局异常捕获后自行处理。
//            throw new CourseException
            throw new Exception("sdfjdls");
        }


//        return HandlerInterceptor.super.preHandle(request, response, handler);

//        return true;
    }

    /**
     * @Author luojiarui
     * @Description 请求处理之后进行调用，但是在视图被渲染之前（Controller方法调用之后）
     * @Date 20:26 2022/12/7
     * @Param [request, response, handler, modelAndView]
     * @return void
     **/
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    /**
     * @Author luojiarui
     * @Description 在整个请求结束之后被调用，也就是在DispatcherServlet 渲染了对应的视图之后执行（主要是用于进行资源清理工作）
     * @Date 20:27 2022/12/7
     * @Param [request, response, handler, ex]
     * @return void
     **/
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
