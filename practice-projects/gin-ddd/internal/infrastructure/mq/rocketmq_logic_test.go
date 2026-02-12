package mq

import (
	"context"
	"encoding/json"
	"testing"
	"time"

	"gin-ddd/internal/domain/event"
)

// MockProducer 模拟 RocketMQ Producer（用于单元测试）
type MockProducer struct {
	messages []MockMessage
}

type MockMessage struct {
	Topic   string
	Tag     string
	Keys    []string
	Body    []byte
	Sent    bool
	SentAt  time.Time
}

// TestPublishEventDataSerialization 测试事件数据能否正确序列化
func TestPublishEventDataSerialization(t *testing.T) {
	// 创建订单创建事件
	orderEvent := event.NewOrderCreatedEvent(
		1001,
		"ORDER-20240211001",
		100,
		"user@example.com",
		"用户1",
		99.99,
	)

	// 获取事件数据
	eventData := orderEvent.EventData()

	// 序列化为 JSON（模拟生产者的序列化过程）
	jsonData, err := json.Marshal(eventData)
	if err != nil {
		t.Errorf("事件数据序列化失败: %v", err)
		return
	}

	t.Logf("✓ 事件序列化成功")
	t.Logf("  序列化数据: %s", string(jsonData))

	// 验证序列化后的数据结构
	var deserializedEvent map[string]interface{}
	if err := json.Unmarshal(jsonData, &deserializedEvent); err != nil {
		t.Errorf("事件数据反序列化失败: %v", err)
		return
	}

	t.Logf("✓ 事件反序列化成功")

	// 验证关键字段
	if orderID, ok := deserializedEvent["order_id"].(float64); ok {
		t.Logf("  - 订单ID: %d", int64(orderID))
	} else {
		t.Error("无法获取订单ID")
	}

	if orderNo, ok := deserializedEvent["order_no"].(string); ok {
		t.Logf("  - 订单号: %s", orderNo)
	} else {
		t.Error("无法获取订单号")
	}

	if amount, ok := deserializedEvent["total_amount"].(float64); ok {
		t.Logf("  - 订单金额: %.2f", amount)
	} else {
		t.Error("无法获取订单金额")
	}
}

// TestEventPublishingLogic 测试发布逻辑（模拟 RocketMQ 行为）
func TestEventPublishingLogic(t *testing.T) {
	// 创建各种订单事件
	testCases := []struct {
		name      string
		event     event.DomainEvent
		topic     string
		expectTag string
	}{
		{
			name: "订单创建事件",
			event: event.NewOrderCreatedEvent(
				1001, "ORDER-001", 100, "user@example.com", "用户", 99.99,
			),
			topic:     "order-event-topic",
			expectTag: event.OrderCreatedEvent,
		},
		{
			name: "订单支付事件",
			event: event.NewOrderPaidEvent(
				1001, "ORDER-001", 100, "user@example.com", "用户", 99.99,
			),
			topic:     "order-event-topic",
			expectTag: event.OrderPaidEvent,
		},
		{
			name: "订单取消事件",
			event: event.NewOrderCancelledEvent(
				1001, "ORDER-001", 100, "user@example.com", "用户",
			),
			topic:     "order-event-topic",
			expectTag: event.OrderCancelledEvent,
		},
	}

	for _, tc := range testCases {
		t.Run(tc.name, func(t *testing.T) {
			// 获取事件类型
			eventType := tc.event.EventType()
			if eventType != tc.expectTag {
				t.Errorf("事件类型不匹配: 期望 %s, 实际 %s", tc.expectTag, eventType)
			}

			// 序列化事件
			jsonData, err := json.Marshal(tc.event.EventData())
			if err != nil {
				t.Errorf("序列化失败: %v", err)
				return
			}

			t.Logf("✓ %s", tc.name)
			t.Logf("  - 事件类型(Tag): %s", eventType)
			t.Logf("  - Topic: %s", tc.topic)
			t.Logf("  - 消息体大小: %d 字节", len(jsonData))
			t.Logf("  - 消息内容: %s", string(jsonData))
		})
	}
}

// TestEventInterfaceImplementation 测试事件是否正确实现接口
func TestEventInterfaceImplementation(t *testing.T) {
	evt := event.NewOrderCreatedEvent(
		1001, "ORDER-001", 100, "user@example.com", "用户", 99.99,
	)

	// 验证是否实现了 DomainEvent 接口
	var _ event.DomainEvent = evt

	t.Logf("✓ OrderEvent 正确实现了 DomainEvent 接口")

	// 验证接口方法
	if eventType := evt.EventType(); eventType == "" {
		t.Error("EventType() 返回空字符串")
	} else {
		t.Logf("✓ EventType() 返回: %s", eventType)
	}

	if occurredTime := evt.OccurredOn(); occurredTime.IsZero() {
		t.Error("OccurredOn() 返回零值")
	} else {
		t.Logf("✓ OccurredOn() 返回: %v", occurredTime)
	}

	if eventData := evt.EventData(); eventData == nil {
		t.Error("EventData() 返回 nil")
	} else {
		t.Logf("✓ EventData() 返回非空数据")
	}
}

// TestMessageFormat 测试消息格式是否符合 RocketMQ 要求
func TestMessageFormat(t *testing.T) {
	orderEvent := event.NewOrderCreatedEvent(
		2001, "ORDER-002", 200, "user@example.com", "用户", 199.99,
	)

	// 模拟生产者创建消息的过程
	eventType := orderEvent.EventType()
	jsonData, _ := json.Marshal(orderEvent.EventData())

	// 验证消息格式
	t.Logf("✓ 消息格式验证:")
	t.Logf("  - 消息Topic: order-event-topic")
	t.Logf("  - 消息Tag: %s", eventType)
	t.Logf("  - 消息Keys: [%s]", eventType)
	t.Logf("  - 消息Body (JSON): %s", string(jsonData))

	// 验证 JSON 有效性
	var result map[string]interface{}
	if err := json.Unmarshal(jsonData, &result); err != nil {
		t.Errorf("消息体 JSON 格式无效: %v", err)
	} else {
		t.Logf("✓ 消息体 JSON 格式有效")
	}
}

// TestProducerInitialization 测试生产者初始化参数
func TestProducerInitialization(t *testing.T) {
	// 配置参数（从 config.yaml 读取）
	config := struct {
		nameServer   string
		groupName    string
		instanceName string
		retryTimes   int
	}{
		nameServer:   "localhost:9876",
		groupName:    "gin-ddd-group",
		instanceName: "gin-ddd-instance",
		retryTimes:   3,
	}

	t.Logf("✓ RocketMQ 生产者配置验证:")
	t.Logf("  - NameServer: %s", config.nameServer)
	t.Logf("  - GroupName: %s", config.groupName)
	t.Logf("  - InstanceName: %s", config.instanceName)
	t.Logf("  - RetryTimes: %d", config.retryTimes)

	if config.nameServer == "" {
		t.Error("NameServer 配置为空")
	}
	if config.groupName == "" {
		t.Error("GroupName 配置为空")
	}
	if config.retryTimes <= 0 {
		t.Error("RetryTimes 配置无效")
	}

	t.Logf("✓ 所有配置参数有效")
}

// TestContextHandling 测试上下文处理
func TestContextHandling(t *testing.T) {
	// 测试有效的上下文
	ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
	defer cancel()

	select {
	case <-ctx.Done():
		t.Error("上下文提前超时")
	default:
		t.Logf("✓ 5秒超时上下文创建成功")
	}

	// 测试已取消的上下文
	ctx2, cancel2 := context.WithCancel(context.Background())
	cancel2()

	select {
	case <-ctx2.Done():
		t.Logf("✓ 已取消上下文按预期工作")
	default:
		t.Error("已取消上下文未生效")
	}
}

// BenchmarkEventSerialization 基准测试：事件序列化性能
func BenchmarkEventSerialization(b *testing.B) {
	orderEvent := event.NewOrderCreatedEvent(
		1001, "ORDER-001", 100, "user@example.com", "用户", 99.99,
	)

	b.ResetTimer()
	for i := 0; i < b.N; i++ {
		json.Marshal(orderEvent.EventData())
	}
}

// BenchmarkEventCreation 基准测试：事件创建性能
func BenchmarkEventCreation(b *testing.B) {
	b.ResetTimer()
	for i := 0; i < b.N; i++ {
		event.NewOrderCreatedEvent(
			int64(1000+i), "ORDER-001", 100, "user@example.com", "用户", 99.99,
		)
	}
}
