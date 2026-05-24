// 领域层(Domain) - 领域事件：订单已创建
//
// 该事件表达"订单已被成功创建"这一既成事实。
// 由 Order 聚合根在创建时记录，由 OrderService 在事务结束后统一发布。
package com.microwind.javaweborder.domain.event;

import com.microwind.javaweborder.domain.order.CustomerName;
import com.microwind.javaweborder.domain.order.Money;
import com.microwind.javaweborder.domain.order.OrderId;

import java.time.LocalDateTime;

public final class OrderCreatedEvent implements DomainEvent {

    private final OrderId orderId;
    private final CustomerName customerName;
    private final Money amount;
    private final LocalDateTime occurredOn;

    public OrderCreatedEvent(OrderId orderId, CustomerName customerName, Money amount) {
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
        return "OrderCreated";
    }

    @Override
    public String toString() {
        return String.format("OrderCreated{id=%d, customer=%s, amount=%s, occurredOn=%s}",
                orderId.value(), customerName.value(), amount, occurredOn);
    }
}
