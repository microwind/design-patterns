package com.github.microwind.springwind.annotation;

import java.lang.annotation.*;

/**
 * 模拟 Spring 的 @RequestParam 注解
 * 用于标注 Controller 方法的参数，指定请求参数名称。
 *
 * 示例：
 * public String hello(@RequestParam("name") String name)
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestParam {

    /**
     * 参数名称（对应请求中的参数名）
     */
    String value() default "";

    /**
     * 是否必填（默认为 true）
     */
    boolean required() default true;

    /**
     * 参数默认值（可选）
     */
    String defaultValue() default "";
}
