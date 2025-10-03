package com.github.microwind.springwind.annotation;

import java.lang.annotation.*;

/**
 * 自动注入注解
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Autowired {
    String value() default "";
}