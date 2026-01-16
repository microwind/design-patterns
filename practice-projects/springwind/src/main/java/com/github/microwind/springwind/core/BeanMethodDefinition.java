package com.github.microwind.springwind.core;

import java.lang.reflect.Method;

/**
 * Bean方法定义类，用于表示@Configuration类中标注@Bean的方法
 * 扩展BeanDefinition，额外记录方法信息和所在的配置类
 */
public class BeanMethodDefinition extends BeanDefinition {
    // @Bean方法
    private Method method;
    // 方法所在的配置类
    private Class<?> configClass;

    /**
     * 构造一个BeanMethodDefinition对象
     * @param beanName Bean的名称
     * @param beanClass Bean的类型（即方法的返回类型）
     */
    public BeanMethodDefinition(String beanName, Class<?> beanClass) {
        super(beanName, beanClass);
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Class<?> getConfigClass() {
        return configClass;
    }

    public void setConfigClass(Class<?> configClass) {
        this.configClass = configClass;
    }
}
