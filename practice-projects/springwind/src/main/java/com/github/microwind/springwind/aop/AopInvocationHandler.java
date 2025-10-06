package com.github.microwind.springwind.aop;

import java.lang.reflect.Method;
import java.util.List;

/**
 * AOP调用处理器
 */
class AopInvocationHandler implements java.lang.reflect.InvocationHandler {
    private Object target;
    private List<AspectInfo> aspects;

    /**
     * 构造函数
     * @param target 目标对象
     * @param aspects 切面信息列表
     */
    public AopInvocationHandler(Object target, List<AspectInfo> aspects) {
        this.target = target;
        this.aspects = aspects;
    }

    /**
     * 调用目标方法
     * @param proxy 代理对象
     * @param method 目标方法
     * @param args 方法参数
     * @return 方法返回值
     * @throws Throwable 异常
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 执行前置通知
        for (AspectInfo aspect : aspects) {
            if (aspect.getAspectType() == AspectType.BEFORE) {
                aspect.getAdviceMethod().invoke(aspect.getAspectBean());
            }
        }

        Object result = null;
        try {
            // 执行目标方法
            result = method.invoke(target, args);
            
            // 执行后置通知
            for (AspectInfo aspect : aspects) {
                if (aspect.getAspectType() == AspectType.AFTER) {
                    aspect.getAdviceMethod().invoke(aspect.getAspectBean());
                }
            }
        } catch (Exception e) {
            throw e.getCause();
        }

        return result;
    }
}