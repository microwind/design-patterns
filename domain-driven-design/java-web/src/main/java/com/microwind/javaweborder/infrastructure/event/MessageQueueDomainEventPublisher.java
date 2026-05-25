package com.microwind.javaweborder.infrastructure.event;

import com.microwind.javaweborder.domain.event.DomainEvent;
import com.microwind.javaweborder.domain.event.DomainEventPublisher;
import com.microwind.javaweborder.infrastructure.message.MessageQueueService;

import java.util.List;

/**
 * 基于消息队列的领域事件发布器实现。
 *
 * <p>DDD 实践：领域层定义 {@link DomainEventPublisher} 抽象，
 * 本类位于<b>基础设施层</b>，把领域事件转换为消息送入 {@link MessageQueueService}。
 *
 * <p>真实工程可以替换为 RocketMQ / Kafka / Spring {@code ApplicationEventPublisher}
 * 等实现，领域代码不受影响。
 */
public class MessageQueueDomainEventPublisher implements DomainEventPublisher {

    private final MessageQueueService messageQueueService;

    /**
     * @param messageQueueService 底层消息中间件适配
     */
    public MessageQueueDomainEventPublisher(MessageQueueService messageQueueService) {
        this.messageQueueService = messageQueueService;
    }

    @Override
    public void publish(DomainEvent event) {
        if (event == null) return;
        String message = String.format("[%s] %s", event.eventType(), event);
        messageQueueService.sendMessage(message);
    }

    @Override
    public void publishAll(List<DomainEvent> events) {
        if (events == null) return;
        for (DomainEvent event : events) {
            publish(event);
        }
    }
}
