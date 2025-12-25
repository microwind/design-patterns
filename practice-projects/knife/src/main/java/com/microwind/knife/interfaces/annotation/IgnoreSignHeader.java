package com.microwind.knife.interfaces.annotation;

import java.lang.annotation.*;

/**
 * 忽略签名检查的注解
 * 挂在 Controller 的方法上，表示该接口不需要执行 SignHeaderAdvice 的逻辑
 */
@Target(ElementType.METHOD) // 标识这个注解只能放在方法上
@Retention(RetentionPolicy.RUNTIME) // 运行时有效，这样反射才能读到
@Documented
public @interface IgnoreSignHeader {
}