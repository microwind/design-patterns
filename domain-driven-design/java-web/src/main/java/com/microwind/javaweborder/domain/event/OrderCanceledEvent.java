package com.microwind.javaweborder.domain.event;

import com.microwind.javaweborder.domain.order.OrderId;

import java.time.LocalDateTime;

/**
 * 领域事件：订单已取消。
 */
public final class OrderCanceledEvent implements DomainEvent {

    private final OrderId orderId;
    private final LocalDateTime occurredOn;

    public OrderCanceledEvent(OrderId orderId) {
        this.orderId = orderId;
        this.occurredOn = LocalDateTime.now();
    }

    public OrderId getOrderId() {
        return orderId;
    }

    @Override
    public LocalDateTime occurredOn() {
        return occurredOn;
    }

    @Override
    public String eventType() {
        return "OrderCanceled";
    }

    @Override
    public String toString() {
        return String.format("OrderCanceled{id=%d, occurredOn=%s}", orderId.value(), occurredOn);
    }
}
