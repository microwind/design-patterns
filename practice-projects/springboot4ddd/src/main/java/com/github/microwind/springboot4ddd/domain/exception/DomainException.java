package com.github.microwind.springboot4ddd.domain.exception;

/**
 * 领域异常基类
 *
 * <p>放在 domain 包下，零框架依赖。代表领域规则被破坏的情形，
 * 由 infrastructure 层的全局异常处理器统一映射为外部协议响应。
 *
 * @author jarry
 * @since 1.0.0
 */
public abstract class DomainException extends RuntimeException {

    protected DomainException(String message) {
        super(message);
    }

    protected DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
