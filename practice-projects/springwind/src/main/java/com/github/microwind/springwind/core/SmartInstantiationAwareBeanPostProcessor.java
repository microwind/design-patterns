package com.github.microwind.springwind.core;

/**
 * 智能实例化感知的Bean后置处理器接口
 * 在Bean实例化早期提供干预机制，可以用于提前创建代理对象
 */
interface SmartInstantiationAwareBeanPostProcessor extends BeanPostProcessor {
    /**
     * 获取早期Bean引用，在Bean初始化前调用
     * 用于解决循环依赖问题，可以提前创建代理对象
     * 
     * @param bean 原始Bean对象
     * @param beanName Bean的名称
     * @return 早期引用（可能是代理对象）
     */
    default Object getEarlyBeanReference(Object bean, String beanName) {
        return bean;
    }
}
