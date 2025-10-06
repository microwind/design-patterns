package com.github.microwind.springwind.core;

/**
 * Bean后置处理器接口
 * 用于在Bean初始化前后进行自定义处理
 */
interface BeanPostProcessor {
    /**
     * 在Bean初始化之前进行自定义处理
     * @param bean
     * @param beanName
     * @return 处理后的Bean
     */
    default Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    /**
     * 在Bean初始化之后进行自定义处理
     * @param bean
     * @param beanName
     * @return 处理后的Bean
     */
    default Object postProcessAfterInitialization(Object bean, String beanName) {
        return bean;
    }
}