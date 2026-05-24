// 领域层(Domain) - 领域异常：订单未找到
//
// 当根据 ID 查找订单但仓储中不存在时抛出。
// 接口层会捕获并返回 HTTP 404。
package com.microwind.javaweborder.domain.exception;

import com.microwind.javaweborder.domain.order.OrderId;

public class OrderNotFoundException extends OrderDomainException {

    public OrderNotFoundException(OrderId orderId) {
        super("订单 " + orderId.value() + " 不存在");
    }

    public OrderNotFoundException(long id) {
        super("订单 " + id + " 不存在");
    }
}
