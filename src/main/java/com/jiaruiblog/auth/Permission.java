package com.jiaruiblog.auth;

import org.springframework.core.annotation.AliasFor;
import java.lang.annotation.*;

/**
 * @ClassName Permission
 * @Description 设置访问权限，可根据不同接口要求，设置为管理员或者普通用户
 * @Author luojiarui
 * @Date 2022/12/7 12:19
 * @Version 1.0
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
public @interface Permission {

    /**
     * 权限数组，来自PermissionEnum
     */
    @AliasFor("value")
    PermissionEnum[] name() default {};

    /**
     * 权限数组，来自PermissionEnum
     */
    @AliasFor("name")
    PermissionEnum[] value() default {};

}
