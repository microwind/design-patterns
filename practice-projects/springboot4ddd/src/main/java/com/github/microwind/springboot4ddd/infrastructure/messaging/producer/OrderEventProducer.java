package com.github.microwind.springboot4ddd.infrastructure.messaging.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.microwind.springboot4ddd.domain.event.DomainEvent;
import com.github.microwind.springboot4ddd.domain.event.order.OrderCancelledEvent;
import com.github.microwind.springboot4ddd.domain.event.order.OrderCompletedEvent;
import com.github.microwind.springboot4ddd.domain.event.order.OrderCreatedEvent;
import com.github.microwind.springboot4ddd.domain.event.order.OrderPaidEvent;
import com.github.microwind.springboot4ddd.infrastructure.messaging.converter.OrderEventMessageMapper;
import com.github.microwind.springboot4ddd.infrastructure.messaging.message.OrderCancelledMessage;
import com.github.microwind.springboot4ddd.infrastructure.messaging.message.OrderCompletedMessage;
import com.github.microwind.springboot4ddd.infrastructure.messaging.message.OrderCreatedMessage;
import com.github.microwind.springboot4ddd.infrastructure.messaging.message.OrderPaidMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 订单事件生产者
 * 负责将订单领域事件发送到 RocketMQ
 *
 * @author jarry
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventProducer {

    private final RocketMQTemplate rocketMQTemplate;
    private final OrderEventMessageMapper messageMapper;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    /**
     * RocketMQ Topic
     */
    private static final String TOPIC = "order-events";

    /**
     * 发布单个领域事件
     *
     * @param event 领域事件
     */
    public void publishEvent(DomainEvent event) {
        try {
            Object message = convertToMessage(event);
            String tag = event.getEventType();
            String destination = TOPIC + ":" + tag;
            String messageBody = objectMapper.writeValueAsString(message);

            // 同步发送消息到 RocketMQ
            SendResult sendResult = rocketMQTemplate.syncSend(destination, messageBody);

            log.info("订单事件已发送到 RocketMQ，eventId={}, eventType={}, topic={}, tag={}, msgId={}",
                    event.getEventId(), event.getEventType(), TOPIC, tag, sendResult.getMsgId());

        } catch (JsonProcessingException e) {
            log.error("序列化订单事件消息失败，eventId={}, eventType={}",
                    event.getEventId(), event.getEventType(), e);
            throw new RuntimeException("发布订单事件失败: 序列化错误", e);
        } catch (Exception e) {
            log.error("发送订单事件到 RocketMQ 失败，eventId={}, eventType={}",
                    event.getEventId(), event.getEventType(), e);
            throw new RuntimeException("发布订单事件失败: 消息发送错误", e);
        }
    }

    /**
     * 批量发布领域事件
     *
     * @param events 领域事件列表
     */
    public void publishEvents(List<DomainEvent> events) {
        if (events == null || events.isEmpty()) {
            return;
        }

        for (DomainEvent event : events) {
            try {
                publishEvent(event);
            } catch (Exception e) {
                // 记录错误但不中断批量发送
                log.error("批量发送事件时失败，eventId={}, 继续处理下一个事件", event.getEventId(), e);
                // 根据业务需求决定是否重新抛出异常
                throw e;
            }
        }
    }

    /**
     * 将领域事件转换为消息对象
     *
     * @param event 领域事件
     * @return 消息对象
     */
    private Object convertToMessage(DomainEvent event) {
        if (event instanceof OrderCreatedEvent) {
            OrderCreatedMessage message = messageMapper.toMessage((OrderCreatedEvent) event);
            log.debug("转换订单创建事件为消息，orderNo={}", message.getOrderNo());
            return message;
        } else if (event instanceof OrderPaidEvent) {
            OrderPaidMessage message = messageMapper.toMessage((OrderPaidEvent) event);
            log.debug("转换订单支付事件为消息，orderNo={}", message.getOrderNo());
            return message;
        } else if (event instanceof OrderCancelledEvent) {
            OrderCancelledMessage message = messageMapper.toMessage((OrderCancelledEvent) event);
            log.debug("转换订单取消事件为消息，orderNo={}", message.getOrderNo());
            return message;
        } else if (event instanceof OrderCompletedEvent) {
            OrderCompletedMessage message = messageMapper.toMessage((OrderCompletedEvent) event);
            log.debug("转换订单完成事件为消息，orderNo={}", message.getOrderNo());
            return message;
        } else {
            throw new IllegalArgumentException("不支持的事件类型: " + event.getClass().getName());
        }
    }
}
