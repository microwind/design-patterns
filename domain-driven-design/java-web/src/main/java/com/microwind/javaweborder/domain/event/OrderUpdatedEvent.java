// 领域层(Domain) - 领域事件：订单已更新
package com.microwind.javaweborder.domain.event;

import com.microwind.javaweborder.domain.order.CustomerName;
import com.microwind.javaweborder.domain.order.Money;
import com.microwind.javaweborder.domain.order.OrderId;

import java.time.LocalDateTime;

public final class OrderUpdatedEvent implements DomainEvent {

    private final OrderId orderId;
    private final CustomerName customerName;
    private final Money amount;
    private final LocalDateTime occurredOn;

    public OrderUpdatedEvent(OrderId orderId, CustomerName customerName, Money amount) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.amount = amount;
        this.occurredOn = LocalDateTime.now();
    }

    public OrderId getOrderId() {
        return orderId;
    }

    public CustomerName getCustomerName() {
        return customerName;
    }

    public Money getAmount() {
        return amount;
    }

    @Override
    public LocalDateTime occurredOn() {
        return occurredOn;
    }

    @Override
    public String eventType() {
        return "OrderUpdated";
    }

    @Override
    public String toString() {
        return String.format("OrderUpdated{id=%d, customer=%s, amount=%s, occurredOn=%s}",
                orderId.value(), customerName.value(), amount, occurredOn);
    }
}
