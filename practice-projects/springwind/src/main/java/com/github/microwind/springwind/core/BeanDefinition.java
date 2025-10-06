package com.github.microwind.springwind.core;

import java.lang.reflect.Method;
import java.util.*;

/**
 * Bean定义类，封装Bean的元数据信息
 * 仿照Spring IoC容器的核心组件之一，用于存储和管理Bean的配置信息
 * 在容器初始化阶段，扫描到的带注解的类会被转换成BeanDefinition对象
 */
public class BeanDefinition {
    // Bean的唯一标识符，在IoC容器中通过此名称查找Bean
    private String beanName;
    // Bean的类型信息，用于实例化Bean
    private Class<?> beanClass;
    // Bean的实例对象，单例模式下会缓存此实例
    private Object beanInstance;
    // Bean的作用域，默认为单例模式
    // singleton: 单例模式，容器中只存在一个实例，所有请求共享
    // prototype: 原型模式，每次请求都会创建新的实例，数据不共享
    private String scope = "singleton";
    // Bean的初始化方法，在Bean实例化和依赖注入完成后调用
    private Method initMethod;
    // Bean的销毁方法，在容器关闭前调用
    private Method destroyMethod;
    // Bean的属性值列表，用于存储需要注入的属性信息
    private List<PropertyValue> propertyValues = new ArrayList<>();

    /**
     * 构造一个BeanDefinition对象
     * @param beanName Bean的名称
     * @param beanClass Bean的类对象
     */
    public BeanDefinition(String beanName, Class<?> beanClass) {
        this.beanName = beanName;
        this.beanClass = beanClass;
    }

    // getter和setter方法
    public String getBeanName() { return beanName; }
    public void setBeanName(String beanName) { this.beanName = beanName; }
    public Class<?> getBeanClass() { return beanClass; }
    public void setBeanClass(Class<?> beanClass) { this.beanClass = beanClass; }
    public Object getBeanInstance() { return beanInstance; }
    public void setBeanInstance(Object beanInstance) { this.beanInstance = beanInstance; }
    public String getScope() { return scope; }
    public void setScope(String scope) { this.scope = scope; }
    public Method getInitMethod() { return initMethod; }
    public void setInitMethod(Method initMethod) { this.initMethod = initMethod; }
    public Method getDestroyMethod() { return destroyMethod; }
    public void setDestroyMethod(Method destroyMethod) { this.destroyMethod = destroyMethod; }
    public List<PropertyValue> getPropertyValues() { return propertyValues; }
    public void setPropertyValues(List<PropertyValue> propertyValues) { this.propertyValues = propertyValues; }
}
