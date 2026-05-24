// 领域层(Domain) - 事件发布抽象
//
// 把"事件发布"抽象成接口，放在领域层，是为了让领域代码不依赖
// 具体的消息中间件（RocketMQ、Kafka、内存队列等）。
// 这就是 DIP（依赖倒置原则）：高层模块定义抽象，低层模块实现抽象。
//
// 实现类放在基础设施层 infrastructure/event/，便于按需替换。
package com.microwind.javaweborder.domain.event;

import java.util.List;

public interface DomainEventPublisher {

    void publish(DomainEvent event);

    void publishAll(List<DomainEvent> events);
}
