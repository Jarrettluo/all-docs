package com.jiaruiblog.annontation;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author Jarrett Luo
 * @Date 2022/12/5 20:25
 * @Version 1.0
 */
@Component
@Aspect
public class MustAdminAspect {

    /**
     * @return void
     * @Author luojiarui
     * @Description 定义切点
     * @Date 22:11 2022/12/6
     * @Param []
     **/
    @Pointcut("@annotation(com.jiaruiblog.annontation.MustAdmin)")
    public void annotationPointCut() {
        // TODO document why this method is empty
    }

    /**
     * @return java.lang.Object
     * @Author luojiarui
     * @Description 环绕通知, 在目标方法完成前后做增强处理, 环绕通知是最重要的通知类型,
     * @Date 22:32 2022/12/6
     * @Param [proceedingJoinPoint]
     **/
    @Before("annotationPointCut()")
    public void processRequest(ProceedingJoinPoint proceedingJoinPoint) {

        System.out.println(proceedingJoinPoint.getSignature().getName());

        //记录请求开始执行时间：
        long beginTime = System.currentTimeMillis();
        //获取请求信息
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = sra.getRequest();

        String requestURI = request.getRequestURI();
        System.out.println(requestURI);

        System.out.println("这是拦截到的请求信息！");
        String userId = (String) request.getAttribute("id");
        System.out.println(userId);

//        if (userId == "1") {
//            assert false;
//        }


        // 参考地址
        // https://blog.csdn.net/wq2323/article/details/120435293
        // https://blog.csdn.net/LitongZero/article/details/103628706


    }


}
