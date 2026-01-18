package com.github.microwind.springwind.annotation;

import java.lang.annotation.*;

/**
 * 模拟 Spring 的 @RequestBody 注解
 * 用于标注 Controller 方法的参数，表示该参数来自请求体。
 *
 * 示例：
 * public ViewResult create(@RequestBody Map<String, Object> data)
 * public ViewResult create(@RequestBody User user)
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestBody {

    /**
     * 是否必填（默认为 true）
     */
    boolean required() default true;
}
