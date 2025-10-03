// 日志切面
package com.github.microwind.springwind.example.aop;

import com.github.microwind.springwind.annotation.Aspect;
import com.github.microwind.springwind.annotation.Before;

@Aspect
public class LogAspect {
    
    @Before("com.github.microwind.springwind.example.service.*")
    public void beforeService() {
        System.out.println("方法执行前记录日志");
    }
}