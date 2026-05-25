package com.microwind.javaweborder.domain.exception;

/**
 * 订单领域异常基类。
 *
 * <p>DDD 实践：自建领域异常体系替代平台异常（{@code IllegalArgumentException}、
 * {@code IllegalStateException} 等），使接口层能按异常类型精确映射 HTTP 状态码，
 * 并让代码里出现的异常名本身就讲业务故事。
 *
 * <h3>子类映射</h3>
 * <ul>
 *   <li>{@link OrderNotFoundException}      → HTTP 404</li>
 *   <li>{@link InvalidOrderStateException}  → HTTP 409</li>
 *   <li>{@link InvalidOrderInputException}  → HTTP 400</li>
 * </ul>
 */
public abstract class OrderDomainException extends RuntimeException {

    protected OrderDomainException(String message) {
        super(message);
    }

    protected OrderDomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
