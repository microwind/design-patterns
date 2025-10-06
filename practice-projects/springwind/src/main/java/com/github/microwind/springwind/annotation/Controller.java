package com.github.microwind.springwind.annotation;

import java.lang.annotation.*;

/**
 * 控制器注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Controller {
    String value() default "";
}