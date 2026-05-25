package com.microwind.javaweborder.domain.exception;

/**
 * 订单输入非法异常。
 *
 * <p>值对象构造时（OrderId 非正、Money 为负、CustomerName 为空等）
 * 或接口层参数校验失败时抛出。接口层捕获后返回 HTTP 400 Bad Request。
 */
public class InvalidOrderInputException extends OrderDomainException {

    public InvalidOrderInputException(String message) {
        super(message);
    }
}
