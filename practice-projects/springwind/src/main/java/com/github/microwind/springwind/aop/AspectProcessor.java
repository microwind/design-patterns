package com.github.microwind.springwind.aop;

import com.github.microwind.springwind.annotation.Aspect;
import com.github.microwind.springwind.annotation.Before;
import com.github.microwind.springwind.annotation.After;
import com.github.microwind.springwind.annotation.Around;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * 切面处理器（支持JDK动态代理和CGLIB代理）
 */
public class AspectProcessor {
    private static final Logger logger = LoggerFactory.getLogger(AspectProcessor.class);

    private final List<AspectInfo> aspectInfos = new ArrayList<>();
    // 缓存编译后的正则表达式Pattern
    private final Map<String, Pattern> patternCache = new ConcurrentHashMap<>();

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
     * 创建代理对象（支持JDK动态代理和CGLIB代理）
     * @param target 目标对象
     * @return 代理对象
     */
    public Object createProxy(Object target) {
        if (target == null) {
            throw new IllegalArgumentException("目标对象不能为null");
        }

        List<AspectInfo> matchedAspects = findMatchedAspects(target.getClass());
        if (matchedAspects.isEmpty()) {
            logger.debug("未找到匹配的切面，返回原始对象: {}", target.getClass().getSimpleName());
            return target;
        }

        logger.debug("为 {} 创建AOP代理，匹配 {} 个切面",
            target.getClass().getSimpleName(), matchedAspects.size());

        // 如果目标对象实现了接口，使用JDK动态代理
        Class<?>[] interfaces = target.getClass().getInterfaces();
        if (interfaces.length > 0) {
            logger.debug("使用JDK动态代理");
            return java.lang.reflect.Proxy.newProxyInstance(
                target.getClass().getClassLoader(),
                interfaces,
                new AopInvocationHandler(target, matchedAspects)
            );
        } else {
            // 否则使用CGLIB代理
            logger.debug("使用CGLIB代理");
            return createCglibProxy(target, matchedAspects);
        }
    }

    /**
     * 使用CGLIB创建代理对象
     */
    private Object createCglibProxy(Object target, List<AspectInfo> matchedAspects) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(target.getClass());
        enhancer.setCallback(new MethodInterceptor() {
            @Override
            public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                // 执行前置通知
                for (AspectInfo aspect : matchedAspects) {
                    if (aspect.getAspectType() == AspectType.BEFORE) {
                        aspect.getAdviceMethod().invoke(aspect.getAspectBean());
                    }
                }

                Object result = null;
                try {
                    // 执行目标方法
                    result = proxy.invokeSuper(obj, args);

                    // 执行后置通知
                    for (AspectInfo aspect : matchedAspects) {
                        if (aspect.getAspectType() == AspectType.AFTER) {
                            aspect.getAdviceMethod().invoke(aspect.getAspectBean());
                        }
                    }
                } catch (Exception e) {
                    logger.error("AOP方法执行失败", e);
                    throw e;
                }

                return result;
            }
        });

        return enhancer.create();
    }

    /**
     * 查找匹配的切面（使用缓存的Pattern提升性能）
     * @param targetClass 目标类
     * @return 匹配的切面列表
     */
    private List<AspectInfo> findMatchedAspects(Class<?> targetClass) {
        List<AspectInfo> matched = new ArrayList<>();
        String className = targetClass.getName();

        for (AspectInfo aspectInfo : aspectInfos) {
            String pointcut = aspectInfo.getPointcut();
            // 从缓存获取Pattern，如果不存在则编译并缓存
            Pattern pattern = patternCache.computeIfAbsent(pointcut,
                pc -> Pattern.compile(pc.replace("*", ".*")));

            if (pattern.matcher(className).matches()) {
                matched.add(aspectInfo);
            }
        }
        return matched;
    }
}