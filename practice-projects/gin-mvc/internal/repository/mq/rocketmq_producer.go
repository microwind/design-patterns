package mq

import (
	"context"
	"encoding/json"
	"fmt"

	"gin-mvc/internal/models/event"
	"gin-mvc/pkg/logger"

	"github.com/apache/rocketmq-client-go/v2"
	"github.com/apache/rocketmq-client-go/v2/primitive"
	"github.com/apache/rocketmq-client-go/v2/producer"
)

type Producer struct {
	producer rocketmq.Producer
}

func NewProducer(nameServer, groupName, instanceName string, retryTimes int) (*Producer, error) {
	p, err := rocketmq.NewProducer(
		producer.WithNameServer([]string{nameServer}),
		producer.WithGroupName(groupName),
		producer.WithInstanceName(instanceName),
		producer.WithRetry(retryTimes),
	)
	if err != nil {
		return nil, fmt.Errorf("new producer failed: %w", err)
	}
	if err := p.Start(); err != nil {
		return nil, fmt.Errorf("start producer failed: %w", err)
	}
	logger.L().Info("rocketmq producer started", "nameserver", nameServer)
	return &Producer{producer: p}, nil
}

func (p *Producer) Publish(ctx context.Context, topic string, evt event.DomainEvent) error {
	body, err := json.Marshal(evt.EventData())
	if err != nil {
		return fmt.Errorf("marshal event failed: %w", err)
	}
	msg := &primitive.Message{Topic: topic, Body: body}
	msg.WithTag(evt.EventType())
	msg.WithKeys([]string{evt.EventType()})

	result, err := p.producer.SendSync(ctx, msg)
	if err != nil {
		return fmt.Errorf("send mq message failed: %w", err)
	}
	logger.Ctx(ctx).Info("mq event published", "topic", topic, "event", evt.EventType(), "msg_id", result.MsgID)
	return nil
}

func (p *Producer) Close() error {
	if p == nil || p.producer == nil {
		return nil
	}
	if err := p.producer.Shutdown(); err != nil {
		return fmt.Errorf("shutdown producer failed: %w", err)
	}
	return nil
}
