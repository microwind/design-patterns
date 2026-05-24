package com.github.microwind.springboot4ddd.domain.exception;

/**
 * 唯一性约束违反异常
 *
 * <p>聚合根在校验自身不变量（如用户名 / 邮箱唯一）时抛出。
 *
 * @author jarry
 * @since 1.0.0
 */
public class UniquenessViolationException extends DomainException {

    public UniquenessViolationException(String message) {
        super(message);
    }
}
