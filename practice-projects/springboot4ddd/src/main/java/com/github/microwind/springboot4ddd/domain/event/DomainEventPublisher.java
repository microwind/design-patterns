package com.github.microwind.springboot4ddd.domain.event;

import java.util.List;

/**
 * 领域事件发布端口
 *
 * <p>application 层通过该接口发布事件；具体投递实现（RocketMQ / Kafka / Outbox 等）
 * 由 infrastructure 提供，避免 application 依赖具体消息中间件。
 *
 * @author jarry
 * @since 1.0.0
 */
public interface DomainEventPublisher {

    void publishEvent(DomainEvent event);

    void publishEvents(List<DomainEvent> events);
}
