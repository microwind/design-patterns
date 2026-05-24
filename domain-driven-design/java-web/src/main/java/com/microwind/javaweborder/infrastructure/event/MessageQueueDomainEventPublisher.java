// 基础设施层(Infrastructure) - 领域事件发布器实现
//
// 通过 MessageQueueService 把领域事件转换为消息，发送出去。
// 真实工程里可以替换为 RocketMQ / Kafka / Spring ApplicationEventPublisher 等。
package com.microwind.javaweborder.infrastructure.event;

import com.microwind.javaweborder.domain.event.DomainEvent;
import com.microwind.javaweborder.domain.event.DomainEventPublisher;
import com.microwind.javaweborder.infrastructure.message.MessageQueueService;

import java.util.List;

public class MessageQueueDomainEventPublisher implements DomainEventPublisher {

    private final MessageQueueService messageQueueService;

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
