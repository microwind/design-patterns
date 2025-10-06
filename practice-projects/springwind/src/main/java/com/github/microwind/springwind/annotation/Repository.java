package com.github.microwind.springwind.annotation;

import java.lang.annotation.*;

/**
 * 仓库注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Repository {
    String value() default "";
}