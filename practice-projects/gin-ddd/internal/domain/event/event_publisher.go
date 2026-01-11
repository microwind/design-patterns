package event

import "context"

// EventPublisher 事件发布器接口
type EventPublisher interface {
	// Publish 发布事件
	Publish(ctx context.Context, topic string, event DomainEvent) error

	// Close 关闭发布器
	Close() error
}

// EventConsumer 事件消费者接口
type EventConsumer interface {
	// Subscribe 订阅主题
	Subscribe(topic string, handler EventHandler) error

	// Start 启动消费者
	Start() error

	// Close 关闭消费者
	Close() error
}

// EventHandler 事件处理器
type EventHandler func(ctx context.Context, event DomainEvent) error
