package com.github.microwind.springwind.annotation;

import java.lang.annotation.*;

/**
 * 标记配置类，里面通常包含 @Bean 方法
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Configuration {
    // value 用于指定 bean 名称（可选）
    String value() default "";
}