package com.jiaruiblog.annontation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @Author Jarrett Luo
 * @Date 2022/12/5 20:20
 * @Version 1.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Component
public @interface MustAdmin {
}
