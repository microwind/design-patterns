package com.github.microwind.springwind.annotation;

import java.lang.annotation.*;

/**
 * 后置通知注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface After {
    String value() default "";
}
