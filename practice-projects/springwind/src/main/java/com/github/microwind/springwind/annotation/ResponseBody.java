package com.github.microwind.springwind.annotation;

import java.lang.annotation.*;

/**
 * 标记该方法或类返回标准 JSON 响应体
 *
 * <p>
 * 这是一个语义注解，不直接绑定 Spring MVC，
 * 由 springwind 的 ResponseBodyAdvice / Filter 进行处理
 * </p>
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ResponseBody {

    /**
     * 是否启用统一包装
     * 默认 true：包装为 ApiResponse
     */
    boolean wrap() default true;

    /**
     * 是否启用 JSON 输出
     * 预留扩展：XML / ProtoBuf 等
     */
    boolean json() default true;
}
