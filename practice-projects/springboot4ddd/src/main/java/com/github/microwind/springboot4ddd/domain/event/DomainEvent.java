package com.github.microwind.springboot4ddd.domain.event;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 领域事件基类
 * 所有领域事件都应继承此类
 *
 * @author jarry
 * @since 1.0.0
 */
@Data
public abstract class DomainEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 事件ID（用于去重和追踪）
     */
    private String eventId;

    /**
     * 聚合根ID
     */
    private Long aggregateId;

    /**
     * 聚合根类型
     */
    private String aggregateType;

    /**
     * 事件发生时间
     */
    private LocalDateTime occurredAt;

    /**
     * 构造函数
     *
     * @param aggregateId   聚合根ID
     * @param aggregateType 聚合根类型
     */
    protected DomainEvent(Long aggregateId, String aggregateType) {
        this.eventId = UUID.randomUUID().toString();
        this.aggregateId = aggregateId;
        this.aggregateType = aggregateType;
        this.occurredAt = LocalDateTime.now();
    }

    /**
     * 获取事件类型名称
     *
     * @return 事件类型名称
     */
    public String getEventType() {
        return this.getClass().getSimpleName();
    }
}
