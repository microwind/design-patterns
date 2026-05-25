package com.microwind.javaweborder.domain.event;

import java.util.List;

/**
 * 领域事件发布器抽象。
 *
 * <p>DDD 战术构件：把"事件发布"接口放在领域层、实现放在基础设施层，
 * 体现 <b>依赖倒置原则（DIP）</b>：高层模块定义抽象，低层模块实现抽象。
 *
 * <p>这样领域代码不依赖具体的消息中间件（RocketMQ、Kafka、Spring 事件等），
 * 可以按需替换实现。
 *
 * @see com.microwind.javaweborder.infrastructure.event.MessageQueueDomainEventPublisher
 */
public interface DomainEventPublisher {

    /**
     * 发布单个事件。
     *
     * @param event 领域事件
     */
    void publish(DomainEvent event);

    /**
     * 批量发布事件，通常用于发布聚合根在一次用例中累积的所有事件。
     *
     * @param events 事件列表
     */
    void publishAll(List<DomainEvent> events);
}
