package com.github.microwind.springwind.annotation;

import java.lang.annotation.*;

/**
 * 服务层注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Service {
    String value() default "";
}