package mq

import (
	"context"
	"encoding/json"
	"fmt"
	"log"
	"sync"
	"time"

	"gin-ddd/internal/domain/event"

	"github.com/apache/rocketmq-client-go/v2"
	"github.com/apache/rocketmq-client-go/v2/consumer"
	"github.com/apache/rocketmq-client-go/v2/primitive"
)

// RocketMQConsumer RocketMQ 消费者
type RocketMQConsumer struct {
	consumer rocketmq.PushConsumer
	handlers map[string]event.EventHandler
	mu       sync.RWMutex
}

// NewRocketMQConsumer 创建 RocketMQ 消费者
func NewRocketMQConsumer(nameServer, groupName, instanceName string) (*RocketMQConsumer, error) {
	c, err := rocketmq.NewPushConsumer(
		consumer.WithNameServer([]string{nameServer}),
		consumer.WithGroupName(groupName),
		consumer.WithInstance(instanceName),
		consumer.WithConsumerModel(consumer.Clustering),
	)
	if err != nil {
		return nil, fmt.Errorf("创建 RocketMQ 消费者失败: %w", err)
	}

	return &RocketMQConsumer{
		consumer: c,
		handlers: make(map[string]event.EventHandler),
	}, nil
}

// Subscribe 订阅主题
func (c *RocketMQConsumer) Subscribe(topic string, handler event.EventHandler) error {
	c.mu.Lock()
	defer c.mu.Unlock()

	// 注册处理器
	c.handlers[topic] = handler

	// 订阅主题
	selector := consumer.MessageSelector{
		Type:       consumer.TAG,
		Expression: "*",
	}

	err := c.consumer.Subscribe(topic, selector, func(ctx context.Context, msgs ...*primitive.MessageExt) (consumer.ConsumeResult, error) {
		for _, msg := range msgs {
			log.Printf("收到消息 - Topic: %s, MessageID: %s, Tag: %s",
				msg.Topic, msg.MsgId, msg.GetTags())

			// 获取处理器
			c.mu.RLock()
			h, exists := c.handlers[msg.Topic]
			c.mu.RUnlock()

			if !exists {
				log.Printf("未找到主题 %s 的处理器", msg.Topic)
				continue
			}

			// 解析事件数据
			var eventData map[string]interface{}
			if err := json.Unmarshal(msg.Body, &eventData); err != nil {
				log.Printf("解析消息失败: %v", err)
				continue
			}

			// 创建领域事件（简化版，实际应根据 Tag 创建具体事件）
			domainEvent := &GenericEvent{
				Type: msg.GetTags(),
				Data: eventData,
			}

			// 调用处理器
			if err := h(ctx, domainEvent); err != nil {
				log.Printf("处理消息失败: %v", err)
				return consumer.ConsumeRetryLater, err
			}
		}
		return consumer.ConsumeSuccess, nil
	})

	if err != nil {
		return fmt.Errorf("订阅主题失败: %w", err)
	}

	log.Printf("成功订阅主题: %s", topic)
	return nil
}

// Start 启动消费者
func (c *RocketMQConsumer) Start() error {
	if err := c.consumer.Start(); err != nil {
		return fmt.Errorf("启动 RocketMQ 消费者失败: %w", err)
	}
	log.Println("RocketMQ 消费者启动成功")
	return nil
}

// Close 关闭消费者
func (c *RocketMQConsumer) Close() error {
	if err := c.consumer.Shutdown(); err != nil {
		return fmt.Errorf("关闭 RocketMQ 消费者失败: %w", err)
	}
	log.Println("RocketMQ 消费者已关闭")
	return nil
}

// GenericEvent 通用事件（用于消费者）
type GenericEvent struct {
	Type string
	Data map[string]interface{}
}

func (e *GenericEvent) EventType() string {
	return e.Type
}

func (e *GenericEvent) OccurredOn() time.Time {
	// 从数据中提取时间戳
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
