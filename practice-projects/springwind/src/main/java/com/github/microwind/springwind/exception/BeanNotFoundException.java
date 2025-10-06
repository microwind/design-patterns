package com.github.microwind.springwind.exception;

/**
 * Bean未找到异常
 * 当请求的Bean不存在时抛出此异常
 */
public class BeanNotFoundException extends RuntimeException {

    private final String beanName;
    private final Class<?> requiredType;

    public BeanNotFoundException(String beanName) {
        super("未找到Bean: " + beanName);
        this.beanName = beanName;
        this.requiredType = null;
    }

    public BeanNotFoundException(Class<?> requiredType) {
        super("未找到类型为 " + requiredType.getName() + " 的Bean");
        this.beanName = null;
        this.requiredType = requiredType;
    }

    public BeanNotFoundException(String beanName, Class<?> requiredType) {
        super("未找到Bean: " + beanName + ", 类型: " + requiredType.getName());
        this.beanName = beanName;
        this.requiredType = requiredType;
    }

    public String getBeanName() {
        return beanName;
    }

    public Class<?> getRequiredType() {
        return requiredType;
    }
}
