package com.microwind.javaweborder.domain.exception;

import com.microwind.javaweborder.domain.order.OrderId;

/**
 * 订单未找到异常。
 *
 * <p>由仓储查询未命中或应用服务加载聚合失败时抛出。
 * 接口层捕获后返回 HTTP 404。
 */
public class OrderNotFoundException extends OrderDomainException {

    public OrderNotFoundException(OrderId orderId) {
        super("订单 " + orderId.value() + " 不存在");
    }

    public OrderNotFoundException(long id) {
        super("订单 " + id + " 不存在");
    }
}
