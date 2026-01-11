package com.github.microwind.springboot4ddd.infrastructure.messaging.converter;

import com.github.microwind.springboot4ddd.domain.event.order.OrderCancelledEvent;
import com.github.microwind.springboot4ddd.domain.event.order.OrderCompletedEvent;
import com.github.microwind.springboot4ddd.domain.event.order.OrderCreatedEvent;
import com.github.microwind.springboot4ddd.domain.event.order.OrderPaidEvent;
import com.github.microwind.springboot4ddd.infrastructure.messaging.message.OrderCancelledMessage;
import com.github.microwind.springboot4ddd.infrastructure.messaging.message.OrderCompletedMessage;
import com.github.microwind.springboot4ddd.infrastructure.messaging.message.OrderCreatedMessage;
import com.github.microwind.springboot4ddd.infrastructure.messaging.message.OrderPaidMessage;
import org.springframework.stereotype.Component;

/**
 * 订单事件消息转换器
 * 负责将领域事件转换为消息对象
 *
 * @author jarry
 * @since 1.0.0
 */
@Component
public class OrderEventMessageMapper {

    /**
     * 将订单创建事件转换为消息
     *
     * @param event 订单创建事件
     * @return 订单创建消息
     */
    public OrderCreatedMessage toMessage(OrderCreatedEvent event) {
        return OrderCreatedMessage.builder()
                .eventId(event.getEventId())
                .orderId(event.getAggregateId())
                .orderNo(event.getOrderNo())
                .userId(event.getUserId())
                .totalAmount(event.getTotalAmount())
                .status(event.getStatus())
                .occurredAt(event.getOccurredAt())
                .build();
    }

    /**
     * 将订单支付事件转换为消息
     *
     * @param event 订单支付事件
     * @return 订单支付消息
     */
    public OrderPaidMessage toMessage(OrderPaidEvent event) {
        return OrderPaidMessage.builder()
                .eventId(event.getEventId())
                .orderId(event.getAggregateId())
                .orderNo(event.getOrderNo())
                .userId(event.getUserId())
                .totalAmount(event.getTotalAmount())
                .status(event.getStatus())
                .occurredAt(event.getOccurredAt())
                .build();
    }

    /**
     * 将订单取消事件转换为消息
     *
     * @param event 订单取消事件
     * @return 订单取消消息
     */
    public OrderCancelledMessage toMessage(OrderCancelledEvent event) {
        return OrderCancelledMessage.builder()
                .eventId(event.getEventId())
                .orderId(event.getAggregateId())
                .orderNo(event.getOrderNo())
                .userId(event.getUserId())
                .totalAmount(event.getTotalAmount())
                .status(event.getStatus())
                .occurredAt(event.getOccurredAt())
                .build();
    }

    /**
     * 将订单完成事件转换为消息
     *
     * @param event 订单完成事件
     * @return 订单完成消息
     */
    public OrderCompletedMessage toMessage(OrderCompletedEvent event) {
        return OrderCompletedMessage.builder()
                .eventId(event.getEventId())
                .orderId(event.getAggregateId())
                .orderNo(event.getOrderNo())
                .userId(event.getUserId())
                .totalAmount(event.getTotalAmount())
                .status(event.getStatus())
                .occurredAt(event.getOccurredAt())
                .build();
    }
}
