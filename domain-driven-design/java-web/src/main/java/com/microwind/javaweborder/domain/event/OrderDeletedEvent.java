// 领域层(Domain) - 领域事件：订单已删除
package com.microwind.javaweborder.domain.event;

import com.microwind.javaweborder.domain.order.OrderId;

import java.time.LocalDateTime;

public final class OrderDeletedEvent implements DomainEvent {

    private final OrderId orderId;
    private final LocalDateTime occurredOn;

    public OrderDeletedEvent(OrderId orderId) {
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
        return "OrderDeleted";
    }

    @Override
    public String toString() {
        return String.format("OrderDeleted{id=%d, occurredOn=%s}", orderId.value(), occurredOn);
    }
}
