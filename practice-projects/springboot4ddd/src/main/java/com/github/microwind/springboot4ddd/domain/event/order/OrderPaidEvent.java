package com.github.microwind.springboot4ddd.domain.event.order;

import com.github.microwind.springboot4ddd.domain.event.DomainEvent;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * 订单支付事件（不可变）
 *
 * @author jarry
 * @since 1.0.0
 */
@Getter
public final class OrderPaidEvent extends DomainEvent {

    private static final long serialVersionUID = 1L;

    private final String orderNo;
    private final Long userId;
    private final BigDecimal totalAmount;
    private final String status;

    public OrderPaidEvent(Long orderId, String orderNo, Long userId, BigDecimal totalAmount, String status) {
        super(orderId, "Order");
        this.orderNo = orderNo;
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.status = status;
    }
}
