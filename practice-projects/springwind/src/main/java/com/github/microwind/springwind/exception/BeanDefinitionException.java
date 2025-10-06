package com.github.microwind.springwind.exception;

/**
 * Bean定义异常
 * 当Bean定义存在问题时抛出此异常
 */
public class BeanDefinitionException extends RuntimeException {

    public BeanDefinitionException(String message) {
        super(message);
    }

    public BeanDefinitionException(String message, Throwable cause) {
        super(message, cause);
    }
}
