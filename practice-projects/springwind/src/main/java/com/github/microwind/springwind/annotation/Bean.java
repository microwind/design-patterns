package com.github.microwind.springwind.annotation;

import java.lang.annotation.*;

/**
 * 标记方法返回的对象要注册成框架容器管理的 Bean
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Bean {

    /**
     * bean 的名称，如果不指定，默认使用方法名
     */
    String value() default "";

    /**
     * 是否是单例（默认 true），可以扩展为 prototype 等
     * （目前你的框架如果还没支持 prototype，可以先忽略这个属性）
     */
    boolean singleton() default true;

    // 可选：若想支持 init-method / destroy-method 可在此添加
    // String initMethod() default "";
    // String destroyMethod() default "";
}