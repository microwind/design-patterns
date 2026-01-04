package com.github.microwind.springboot4ddd.interfaces.annotation;

import java.lang.annotation.*;

/**
 * 签名验证注解
 * <p>
 * 用于标记需要进行签名验证的 Controller 或方法
 *
 * @author jarry
 * @since 1.0.0
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireSign {
    /**
     * 是否需要参数签名
     * <p>
     * - DEFAULT（默认值）: 使用配置文件中的默认值（或继承类级别的配置）
     * - TRUE: 签名计算包含请求参数
     * - FALSE: 签名计算不包含请求参数
     */
    WithParams withParams() default WithParams.DEFAULT;
}
