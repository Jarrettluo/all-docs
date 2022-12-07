package com.jiaruiblog.auth;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @ClassName Permission
 * @Description TODO
 * @Author luojiarui
 * @Date 2022/12/7 12:19
 * @Version 1.0
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
public @interface Permission {

    /**
     * 权限
     */
    @AliasFor("value")
    PermissionEnum[] name() default {};

    /**
     * 权限
     */
    @AliasFor("name")
    PermissionEnum[] value() default {};
}
