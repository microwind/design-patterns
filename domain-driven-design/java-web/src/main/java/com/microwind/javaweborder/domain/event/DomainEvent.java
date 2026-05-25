package com.microwind.javaweborder.domain.event;

import java.time.LocalDateTime;

/**
 * 领域事件通用契约。
 *
 * <p>DDD 战术构件：<b>领域事件（Domain Event）</b>。当业务上发生某件
 * "值得记录的事"（订单创建、订单取消等）时，由聚合根累积事件，
 * 由应用层在事务边界外通过 {@link DomainEventPublisher} 一次性发布，
 * 用于解耦不同聚合、不同限界上下文之间的协作。
 *
 * <p>命名约定：具体事件类以"过去时"命名（OrderCreated、OrderCanceled），
 * 表示"已经发生的事实"，不可被否决或回滚。
 */
public interface DomainEvent {

    /**
     * @return 事件发生时间（构造时固化）
     */
    LocalDateTime occurredOn();

    /**
     * @return 事件类型标识，用于发布 / 订阅 / 日志聚合
     */
    String eventType();
}
