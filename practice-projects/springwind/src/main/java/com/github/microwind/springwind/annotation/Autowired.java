package com.github.microwind.springwind.annotation;

import java.lang.annotation.*;

/**
 * 自动注入注解
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Autowired {
    /**
     * Bean名称（可选）
     */
    String value() default "";

    /**
     * 是否必须注入（默认true，找不到依赖时抛出异常）
     */
    boolean required() default true;
}
