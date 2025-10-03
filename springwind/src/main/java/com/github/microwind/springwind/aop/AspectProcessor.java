package com.github.microwind.springwind.aop;

import com.github.microwind.springwind.annotation.Aspect;
import com.github.microwind.springwind.annotation.Before;
import com.github.microwind.springwind.annotation.After;
import com.github.microwind.springwind.annotation.Around;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 切面处理器
 */
public class AspectProcessor {
    private final List<AspectInfo> aspectInfos = new ArrayList<>();

    /**
     * 注册切面
     * @param aspectBean 切面Bean
     */
    public void registerAspect(Object aspectBean) {
        Class<?> aspectClass = aspectBean.getClass();
        if (!aspectClass.isAnnotationPresent(Aspect.class)) {
            return;
        }

        for (Method method : aspectClass.getMethods()) {
            if (method.isAnnotationPresent(Before.class)) {
                String pointcut = method.getAnnotation(Before.class).value();
                aspectInfos.add(new AspectInfo(pointcut, AspectType.BEFORE, method, aspectBean));
            } else if (method.isAnnotationPresent(After.class)) {
                String pointcut = method.getAnnotation(After.class).value();
                aspectInfos.add(new AspectInfo(pointcut, AspectType.AFTER, method, aspectBean));
            } else if (method.isAnnotationPresent(Around.class)) {
                String pointcut = method.getAnnotation(Around.class).value();
                aspectInfos.add(new AspectInfo(pointcut, AspectType.AROUND, method, aspectBean));
            }
        }
    }

    /**
     * 创建代理对象
     * @param target 目标对象
     * @return 代理对象
     */
    public Object createProxy(Object target) {
        List<AspectInfo> matchedAspects = findMatchedAspects(target.getClass());
        if (matchedAspects.isEmpty()) {
            return target;
        }

        return java.lang.reflect.Proxy.newProxyInstance(
            target.getClass().getClassLoader(),
            target.getClass().getInterfaces(),
            new AopInvocationHandler(target, matchedAspects)
        );
    }

    /**
     * 查找匹配的切面
     * @param targetClass 目标类
     * @return 匹配的切面列表
     */
    private List<AspectInfo> findMatchedAspects(Class<?> targetClass) {
        List<AspectInfo> matched = new ArrayList<>();
        String className = targetClass.getName();
        
        for (AspectInfo aspectInfo : aspectInfos) {
            if (className.matches(aspectInfo.getPointcut().replace("*", ".*"))) {
                matched.add(aspectInfo);
            }
        }
        return matched;
    }
}