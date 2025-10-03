package com.github.microwind.springwind.annotation;

import java.lang.annotation.*;

/**
 * 组件注解，标记该类由SpringWind容器管理
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Component {
    String value() default "";
}