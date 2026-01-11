package mq

import (
	"context"
	"encoding/json"
	"fmt"
	"log"

	"gin-ddd/internal/domain/event"

	"github.com/apache/rocketmq-client-go/v2"
	"github.com/apache/rocketmq-client-go/v2/primitive"
	"github.com/apache/rocketmq-client-go/v2/producer"
)

// RocketMQProducer RocketMQ 生产者
type RocketMQProducer struct {
	producer rocketmq.Producer
}

// NewRocketMQProducer 创建 RocketMQ 生产者
func NewRocketMQProducer(nameServer, groupName, instanceName string, retryTimes int) (*RocketMQProducer, error) {
	p, err := rocketmq.NewProducer(
		producer.WithNameServer([]string{nameServer}),
		producer.WithGroupName(groupName),
		producer.WithInstanceName(instanceName),
		producer.WithRetry(retryTimes),
	)
	if err != nil {
		return nil, fmt.Errorf("创建 RocketMQ 生产者失败: %w", err)
	}

	if err := p.Start(); err != nil {
		return nil, fmt.Errorf("启动 RocketMQ 生产者失败: %w", err)
	}

	log.Printf("RocketMQ 生产者启动成功: %s", nameServer)
	return &RocketMQProducer{
		producer: p,
	}, nil
}

// Publish 发布事件
func (p *RocketMQProducer) Publish(ctx context.Context, topic string, domainEvent event.DomainEvent) error {
	// 序列化事件数据
	data, err := json.Marshal(domainEvent.EventData())
	if err != nil {
		return fmt.Errorf("序列化事件数据失败: %w", err)
	}

	// 创建消息
	msg := &primitive.Message{
		Topic: topic,
		Body:  data,
	}
	msg.WithTag(domainEvent.EventType())
	msg.WithKeys([]string{domainEvent.EventType()})

	// 发送消息
	result, err := p.producer.SendSync(ctx, msg)
	if err != nil {
		return fmt.Errorf("发送消息失败: %w", err)
	}

	log.Printf("消息发送成功 - Topic: %s, MessageID: %s, EventType: %s",
		topic, result.MsgID, domainEvent.EventType())
	return nil
}

// Close 关闭生产者
func (p *RocketMQProducer) Close() error {
	if err := p.producer.Shutdown(); err != nil {
		return fmt.Errorf("关闭 RocketMQ 生产者失败: %w", err)
	}
	log.Println("RocketMQ 生产者已关闭")
	return nil
}
