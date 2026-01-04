package com.github.microwind.springboot4ddd.interfaces.annotation;

/**
 * 签名验证时是否包含参数的枚举
 *
 * @author jarry
 * @since 1.0.0
 */
public enum WithParams {
    /**
     * 使用参数签名（签名计算包含请求参数）
     */
    TRUE,

    /**
     * 不使用参数签名（签名计算不包含请求参数）
     */
    FALSE,

    /**
     * 使用默认配置（从配置文件或类级别继承）
     */
    DEFAULT
}
