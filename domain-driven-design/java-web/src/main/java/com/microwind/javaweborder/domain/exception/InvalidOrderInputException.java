// 领域层(Domain) - 领域异常：订单输入不合法
//
// 值对象构造时（OrderId 非正、Money 为负、CustomerName 为空 …）抛出。
// 接口层会捕获并返回 HTTP 400 Bad Request。
package com.microwind.javaweborder.domain.exception;

public class InvalidOrderInputException extends OrderDomainException {

    public InvalidOrderInputException(String message) {
        super(message);
    }
}
