package com.github.microwind.springwind.annotation;

import java.lang.annotation.*;

/**
 * 绑定 URI 模板变量到方法参数
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PathVariable {

    /**
     * 变量名（如果不填，默认使用参数名）
     */
    String value() default "";

    /**
     * 是否必填（目前可以先不实现，默认为 true）
     */
    boolean required() default true;
}