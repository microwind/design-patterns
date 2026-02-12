package mq

import (
	"context"
	"encoding/json"
	"fmt"

	"gin-ddd/internal/domain/event"
	"gin-ddd/pkg/utils"

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

	utils.GetLogger().Info("RocketMQ 生产者启动成功: %s", nameServer)
	return &RocketMQProducer{
		producer: p,
	}, nil
}

// Publish 发布事件
func (p *RocketMQProducer) Publish(ctx context.Context, topic string, domainEvent event.DomainEvent) error {
	eventType := domainEvent.EventType()
	utils.GetLogger().Info("[RocketMQ Producer] 开始发布事件: topic=%s, eventType=%s", topic, eventType)

	// 序列化事件数据
	utils.GetLogger().Info("[RocketMQ Producer] 序列化事件数据...")
	data, err := json.Marshal(domainEvent.EventData())
	if err != nil {
		utils.GetLogger().Error("[RocketMQ Producer] 序列化失败: %v", err)
		return fmt.Errorf("序列化事件数据失败: %w", err)
	}
	utils.GetLogger().Info("[RocketMQ Producer] 序列化成功, 消息体大小: %d bytes", len(data))

	// 创建消息
	utils.GetLogger().Info("[RocketMQ Producer] 创建RocketMQ消息...")
	msg := &primitive.Message{
		Topic: topic,
		Body:  data,
	}
	msg.WithTag(eventType)
	msg.WithKeys([]string{eventType})

	// 发送消息
	utils.GetLogger().Info("[RocketMQ Producer] 发送消息到Broker...")
	result, err := p.producer.SendSync(ctx, msg)
	if err != nil {
		utils.GetLogger().Error("[RocketMQ Producer] 消息发送失败: %v", err)
		return fmt.Errorf("发送消息失败: %w", err)
	}

	utils.GetLogger().Info("[RocketMQ Producer] 消息发送成功: topic=%s, msgId=%s", topic, result.MsgID)
	return nil
}

// Close 关闭生产者
func (p *RocketMQProducer) Close() error {
	if err := p.producer.Shutdown(); err != nil {
		return fmt.Errorf("关闭 RocketMQ 生产者失败: %w", err)
	}
	utils.GetLogger().Info("RocketMQ 生产者已关闭")
	return nil
}
