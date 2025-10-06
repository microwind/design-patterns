package com.github.microwind.springwind.annotation;

import java.lang.annotation.*;

/**
 * 请求映射注解
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestMapping {
    String value() default "";
    String method() default "GET";
}
