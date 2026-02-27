package mq

import (
	"context"
	"encoding/json"
	"fmt"
	"strings"
	"sync"
	"time"

	"gin-mvc/internal/models/event"
	"gin-mvc/pkg/logger"

	"github.com/apache/rocketmq-client-go/v2"
	"github.com/apache/rocketmq-client-go/v2/consumer"
	"github.com/apache/rocketmq-client-go/v2/primitive"
)

type Consumer struct {
	consumer rocketmq.PushConsumer
	handlers map[string]event.Handler
	mu       sync.RWMutex
}

func NewConsumer(nameServer, groupName, instanceName string) (*Consumer, error) {
	c, err := rocketmq.NewPushConsumer(
		consumer.WithNameServer([]string{nameServer}),
		consumer.WithGroupName(groupName),
		consumer.WithInstance(instanceName),
		consumer.WithConsumerModel(consumer.Clustering),
	)
	if err != nil {
		return nil, fmt.Errorf("new consumer failed: %w", err)
	}
	return &Consumer{consumer: c, handlers: make(map[string]event.Handler)}, nil
}

func (c *Consumer) Subscribe(topic string, handler event.Handler) error {
	c.mu.Lock()
	c.handlers[topic] = handler
	c.mu.Unlock()

	selector := consumer.MessageSelector{Type: consumer.TAG, Expression: "*"}
	return c.consumer.Subscribe(topic, selector, func(ctx context.Context, msgs ...*primitive.MessageExt) (consumer.ConsumeResult, error) {
		for _, msg := range msgs {
			c.mu.RLock()
			h := c.handlers[msg.Topic]
			c.mu.RUnlock()
			if h == nil {
				logger.Ctx(ctx).Warn("mq handler not found", "topic", msg.Topic)
				continue
			}

			domainEvent, err := decodeDomainEvent(msg.GetTags(), msg.Body)
			if err != nil {
				logger.Ctx(ctx).Error("decode mq event failed", "err", err)
				continue
			}

			if err := h(ctx, domainEvent); err != nil {
				logger.Ctx(ctx).Error("handle mq event failed", "event", domainEvent.EventType(), "err", err)
				return consumer.ConsumeRetryLater, err
			}
		}
		return consumer.ConsumeSuccess, nil
	})
}

func (c *Consumer) Start() error {
	if err := c.consumer.Start(); err != nil {
		return fmt.Errorf("start consumer failed: %w", err)
	}
	logger.L().Info("rocketmq consumer started")
	return nil
}

func (c *Consumer) Close() error {
	if c == nil || c.consumer == nil {
		return nil
	}
	if err := c.consumer.Shutdown(); err != nil {
		return fmt.Errorf("shutdown consumer failed: %w", err)
	}
	return nil
}

type GenericEvent struct {
	Type string
	Data map[string]interface{}
}

func (e *GenericEvent) EventType() string {
	return e.Type
}

func (e *GenericEvent) OccurredOn() time.Time {
	if ts, ok := e.Data["timestamp"].(string); ok {
		if t, err := time.Parse(time.RFC3339, ts); err == nil {
			return t
		}
	}
	return time.Now()
}

func (e *GenericEvent) EventData() interface{} {
	return e.Data
}

func decodeDomainEvent(tag string, body []byte) (event.DomainEvent, error) {
	switch {
	case strings.HasPrefix(tag, "order."):
		var evt event.OrderEvent
		if err := json.Unmarshal(body, &evt); err != nil {
			return nil, err
		}
		if evt.Type == "" {
			evt.Type = tag
		}
		if evt.Timestamp.IsZero() {
			evt.Timestamp = time.Now()
		}
		return &evt, nil
	case strings.HasPrefix(tag, "user."):
		var evt event.UserEvent
		if err := json.Unmarshal(body, &evt); err != nil {
			return nil, err
		}
		if evt.Type == "" {
			evt.Type = tag
		}
		if evt.Timestamp.IsZero() {
			evt.Timestamp = time.Now()
		}
		return &evt, nil
	default:
		var data map[string]interface{}
		if err := json.Unmarshal(body, &data); err != nil {
			return nil, err
		}
		return &GenericEvent{Type: tag, Data: data}, nil
	}
}
