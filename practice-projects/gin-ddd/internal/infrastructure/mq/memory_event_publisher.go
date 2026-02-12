package mq

import (
	"context"

	"gin-ddd/internal/domain/event"
	"gin-ddd/pkg/utils"
)

// MemoryEventPublisher 内存事件发布器（用于 RocketMQ 不可用时的备选方案）
type MemoryEventPublisher struct {
	events []event.DomainEvent
}

// NewMemoryEventPublisher 创建内存事件发布器
func NewMemoryEventPublisher() *MemoryEventPublisher {
	return &MemoryEventPublisher{
		events: make([]event.DomainEvent, 0),
	}
}

// Publish 发布事件到内存
func (p *MemoryEventPublisher) Publish(ctx context.Context, topic string, domainEvent event.DomainEvent) error {
	p.events = append(p.events, domainEvent)
	utils.GetLogger().Info("[MemoryEventPublisher] 发布事件到内存: topic=%s, eventType=%s, 已缓存事件数=%d",
		topic, domainEvent.EventType(), len(p.events))
	return nil
}

// Close 关闭发布器
func (p *MemoryEventPublisher) Close() error {
	utils.GetLogger().Info("[MemoryEventPublisher] 内存事件发布器已关闭，共缓存了 %d 个事件", len(p.events))
	return nil
}

// GetEvents 获取所有已发布的事件（用于测试）
func (p *MemoryEventPublisher) GetEvents() []event.DomainEvent {
	return p.events
}
