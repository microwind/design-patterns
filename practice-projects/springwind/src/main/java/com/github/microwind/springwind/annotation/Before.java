package com.github.microwind.springwind.annotation;

import java.lang.annotation.*;

/**
 * 前置通知注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Before {
    String value() default "";
}
