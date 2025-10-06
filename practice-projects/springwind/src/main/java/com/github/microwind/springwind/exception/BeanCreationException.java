package com.github.microwind.springwind.exception;

/**
 * Bean创建异常
 * 当Bean无法被正确创建时抛出此异常
 */
public class BeanCreationException extends RuntimeException {

    private final String beanName;

    public BeanCreationException(String beanName, String message) {
        super(message);
        this.beanName = beanName;
    }

    public BeanCreationException(String beanName, String message, Throwable cause) {
        super(message, cause);
        this.beanName = beanName;
    }

    public String getBeanName() {
        return beanName;
    }

    @Override
    public String getMessage() {
        return "Bean创建失败 [" + beanName + "]: " + super.getMessage();
    }
}
