// 领域层(Domain) - 领域异常：订单状态非法
//
// 业务规则被违反（如：试图取消一个已取消的订单、修改一个已取消的订单）时抛出。
// 接口层会捕获并返回 HTTP 409 Conflict。
package com.microwind.javaweborder.domain.exception;

public class InvalidOrderStateException extends OrderDomainException {

    public InvalidOrderStateException(String message) {
        super(message);
    }
}
