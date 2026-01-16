package com.github.microwind.springwind.annotation;

import java.lang.annotation.*;

/**
 * POST 请求映射注解
 * 相当于 @RequestMapping(value = "/path", method = "POST")
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PostMapping {

    /**
     * 请求路径，支持路径参数（如 "/user/{id}"）
     */
    String value() default "";
}
