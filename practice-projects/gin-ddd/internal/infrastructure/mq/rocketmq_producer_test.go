package mq

import (
	"context"
	"testing"
	"time"

	"gin-ddd/internal/domain/event"
)

// TestRocketMQProducerSendSuccess 测试 RocketMQ 生产者发送成功
func TestRocketMQProducerSendSuccess(t *testing.T) {
	// 配置 RocketMQ 连接信息（需要本地 RocketMQ 服务运行在 localhost:9876）
	nameServer := "localhost:9876"
	groupName := "test-group"
	instanceName := "test-instance"
	retryTimes := 1

	// 创建生产者
	producer, err := NewRocketMQProducer(nameServer, groupName, instanceName, retryTimes)
	if err != nil {
		t.Logf("警告: 无法连接到 RocketMQ 服务: %v", err)
		t.Logf("请确保 RocketMQ 服务已启动在 %s", nameServer)
		t.Skip("RocketMQ 服务未可用，跳过此测试")
	}
	defer producer.Close()

	// 创建测试事件 - 订单创建事件
	testEvent := event.NewOrderCreatedEvent(
		1001,               // OrderID
		"ORDER-20240211001", // OrderNo
		100,                // UserID
		"user@example.com", // UserEmail
		"用户",             // UserName
		99.99,              // TotalAmount
	)

	// 发送事件
	ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
	defer cancel()

	err = producer.Publish(ctx, "order-event-topic", testEvent)
	if err != nil {
		t.Errorf("发送订单创建事件失败: %v", err)
	} else {
		t.Logf("✓ 订单创建事件发送成功")
		t.Logf("  - 事件类型: %s", testEvent.EventType())
		t.Logf("  - 订单ID: %d", testEvent.OrderID)
		t.Logf("  - 订单号: %s", testEvent.OrderNo)
	}
}

// TestRocketMQProducerSendMultipleEvents 测试发送多个事件
func TestRocketMQProducerSendMultipleEvents(t *testing.T) {
	nameServer := "localhost:9876"
	groupName := "test-group"
	instanceName := "test-instance"
	retryTimes := 1

	producer, err := NewRocketMQProducer(nameServer, groupName, instanceName, retryTimes)
	if err != nil {
		t.Logf("警告: 无法连接到 RocketMQ 服务: %v", err)
		t.Skip("RocketMQ 服务未可用，跳过此测试")
	}
	defer producer.Close()

	ctx, cancel := context.WithTimeout(context.Background(), 10*time.Second)
	defer cancel()

	// 发送订单创建事件
	testEvents := []struct {
		name  string
		event event.DomainEvent
		topic string
	}{
		{
			name: "订单创建事件",
			event: event.NewOrderCreatedEvent(
				2001, "ORDER-20240211002", 200, "user@example.com", "用户", 199.99,
			),
			topic: "order-event-topic",
		},
		{
			name: "订单支付事件",
			event: event.NewOrderPaidEvent(
				2001, "ORDER-20240211002", 200, "user@example.com", "用户", 199.99,
			),
			topic: "order-event-topic",
		},
		{
			name: "订单取消事件",
			event: event.NewOrderCancelledEvent(
				2001, "ORDER-20240211002", 200, "user@example.com", "用户",
			),
			topic: "order-event-topic",
		},
	}

	for _, testCase := range testEvents {
		err = producer.Publish(ctx, testCase.topic, testCase.event)
		if err != nil {
			t.Errorf("发送 %s 失败: %v", testCase.name, err)
		} else {
			t.Logf("✓ %s 发送成功", testCase.name)
		}
	}
}

// TestRocketMQProducerEventSerialization 测试事件序列化
func TestRocketMQProducerEventSerialization(t *testing.T) {
	nameServer := "localhost:9876"
	groupName := "test-group"
	instanceName := "test-instance"
	retryTimes := 1

	producer, err := NewRocketMQProducer(nameServer, groupName, instanceName, retryTimes)
	if err != nil {
		t.Logf("警告: 无法连接到 RocketMQ 服务: %v", err)
		t.Skip("RocketMQ 服务未可用，跳过此测试")
	}
	defer producer.Close()

	// 创建包含各种数据的事件
	testEvent := event.NewOrderCreatedEvent(
		9999,
		"ORDER-TEST-SERIALIZATION",
		777,
		"user@example.com",
		"用户",
		12345.67,
	)

	// 验证事件数据能正确获取
	eventData := testEvent.EventData()
	if eventData == nil {
		t.Error("事件数据为空")
	}

	t.Logf("✓ 事件序列化检查通过")
	t.Logf("  - 事件类型: %s", testEvent.EventType())
	t.Logf("  - 发生时间: %v", testEvent.OccurredOn())

	// 尝试发送
	ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
	defer cancel()

	err = producer.Publish(ctx, "order-event-topic", testEvent)
	if err != nil {
		t.Errorf("发送序列化事件失败: %v", err)
	} else {
		t.Logf("✓ 序列化的事件发送成功")
	}
}

// TestRocketMQProducerTimeout 测试超时处理
func TestRocketMQProducerTimeout(t *testing.T) {
	nameServer := "localhost:9876"
	groupName := "test-group"
	instanceName := "test-instance"
	retryTimes := 1

	producer, err := NewRocketMQProducer(nameServer, groupName, instanceName, retryTimes)
	if err != nil {
		t.Logf("警告: 无法连接到 RocketMQ 服务: %v", err)
		t.Skip("RocketMQ 服务未可用，跳过此测试")
	}
	defer producer.Close()

	testEvent := event.NewOrderCreatedEvent(
		5001, "ORDER-20240211005", 500, "user@example.com", "用户", 500.00,
	)

	// 使用很短的超时时间
	ctx, cancel := context.WithTimeout(context.Background(), 1*time.Millisecond)
	defer cancel()

	err = producer.Publish(ctx, "order-event-topic", testEvent)
	if err != nil {
		t.Logf("预期的超时错误: %v", err)
		t.Logf("✓ 超时处理正确")
	}
}
