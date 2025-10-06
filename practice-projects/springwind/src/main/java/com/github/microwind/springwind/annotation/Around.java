package com.github.microwind.springwind.annotation;

import java.lang.annotation.*;

/**
 * 环绕通知注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Around {
    String value() default "";
}