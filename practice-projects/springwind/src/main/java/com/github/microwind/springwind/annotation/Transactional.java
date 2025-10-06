package com.github.microwind.springwind.annotation;

import java.lang.annotation.*;

/**
 * 事务注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Transactional {
}