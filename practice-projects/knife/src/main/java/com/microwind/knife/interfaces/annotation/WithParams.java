package com.microwind.knife.interfaces.annotation;

/**
 * 签名验证时是否包含参数的枚举
 * <p>
 * 用于 @RequireSign 注解的 withParams 属性
 */
public enum WithParams {
    /**
     * 使用参数签名（签名计算包含请求参数，使用 SM3 算法）
     */
    TRUE,

    /**
     * 不使用参数签名（签名计算不包含请求参数，使用 SHA-256 算法）
     */
    FALSE,

    /**
     * 使用默认配置（从配置文件或类级别继承）
     */
    DEFAULT
}
