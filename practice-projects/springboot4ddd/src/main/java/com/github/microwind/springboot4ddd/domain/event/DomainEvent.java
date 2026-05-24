package com.github.microwind.springboot4ddd.domain.event;

import lombok.Getter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 领域事件基类
 *
 * <p>领域事件表示"已发生的事实"，必须不可变：所有字段 final，
 * 仅暴露 getter，不允许下游消费方篡改聚合 id 或发生时间。
 *
 * @author jarry
 * @since 1.0.0
 */
@Getter
public abstract class DomainEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String eventId;
    private final Long aggregateId;
    private final String aggregateType;
    private final LocalDateTime occurredAt;

    protected DomainEvent(Long aggregateId, String aggregateType) {
        this.eventId = UUID.randomUUID().toString();
        this.aggregateId = aggregateId;
        this.aggregateType = aggregateType;
        this.occurredAt = LocalDateTime.now();
    }

    public String getEventType() {
        return this.getClass().getSimpleName();
    }
}
