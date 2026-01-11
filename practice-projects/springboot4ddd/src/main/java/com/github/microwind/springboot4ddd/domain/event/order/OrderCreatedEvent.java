package com.github.microwind.springboot4ddd.domain.event.order;

import com.github.microwind.springboot4ddd.domain.event.DomainEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 订单创建事件
 *
 * @author jarry
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OrderCreatedEvent extends DomainEvent {

    private static final long serialVersionUID = 1L;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 订单总金额
     */
    private BigDecimal totalAmount;

    /**
     * 订单状态
     */
    private String status;

    /**
     * 构造函数
     *
     * @param orderId     订单ID
     * @param orderNo     订单号
     * @param userId      用户ID
     * @param totalAmount 订单总金额
     * @param status      订单状态
     */
    public OrderCreatedEvent(Long orderId, String orderNo, Long userId, BigDecimal totalAmount, String status) {
        super(orderId, "Order");
        this.orderNo = orderNo;
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.status = status;
    }
}
