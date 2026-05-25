package com.microwind.javaweborder.domain.exception;

/**
 * 订单状态非法异常。
 *
 * <p>业务规则被违反时抛出（如：试图取消一个已取消的订单、
 * 修改一个已取消的订单）。接口层捕获后返回 HTTP 409 Conflict。
 */
public class InvalidOrderStateException extends OrderDomainException {

    public InvalidOrderStateException(String message) {
        super(message);
    }
}
