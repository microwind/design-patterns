package com.github.microwind.springboot4ddd.interfaces.annotation;

import java.lang.annotation.*;

/**
 * 忽略签名验证注解
 * <p>
 * 用于标记需要跳过签名验证的方法（即使类上有 @RequireSign 注解）
 *
 * @author jarry
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IgnoreSignHeader {
}
