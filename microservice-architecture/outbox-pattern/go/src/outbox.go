// Package src 实现了 Outbox 模式（Outbox Pattern）的核心逻辑。
//
// 【设计模式】
//   - 观察者模式：outbox 事件被 relay 扫描并发布到 broker。
//   - 命令模式：OutboxEvent 将事件封装为数据对象，relay 异步执行发布。
//
// 【架构思想】
//   解决"写库成功 + 发消息失败"导致的数据与事件不一致问题。
//
// 【开源对比】
//   - Go 生态中通常用 Kafka + outbox 表 + 定时任务实现
//   - watermill-io/watermill：Go 消息框架，支持 outbox 模式
//   本示例用内存 slice 模拟。
package src

// Order 订单实体。
type Order struct {
	OrderID string // 订单ID
	Status  string // 订单状态
}

// OutboxEvent outbox 事件记录。
// 【设计模式】命令模式：将事件封装为数据对象，relay 异步发布。
type OutboxEvent struct {
	EventID     string // 事件唯一ID
	AggregateID string // 聚合根ID（订单ID）
	EventType   string // 事件类型
	Status      string // 发布状态：pending / published
}

// OutboxService Outbox 服务。
type OutboxService struct {
	orders []Order       // 模拟 orders 表
	outbox []OutboxEvent // 模拟 outbox 表
}

// MemoryBroker 内存消息代理（模拟 Kafka / RabbitMQ）。
type MemoryBroker struct {
	published []string // 已发布的事件ID列表
}

// NewOutboxService 创建 Outbox 服务。
func NewOutboxService() *OutboxService {
	return &OutboxService{}
}

// NewMemoryBroker 创建内存消息代理。
func NewMemoryBroker() *MemoryBroker {
	return &MemoryBroker{}
}

// CreateOrder 创建订单，同时写入 orders 和 outbox（模拟同一事务）。
func (s *OutboxService) CreateOrder(orderID string) {
	// 写入订单
	s.orders = append(s.orders, Order{OrderID: orderID, Status: "CREATED"})
	// 写入 outbox 事件（同一"事务"）
	s.outbox = append(s.outbox, OutboxEvent{
		EventID:     "EVT-" + orderID,
		AggregateID: orderID,
		EventType:   "order_created",
		Status:      "pending",
	})
}

// RelayPending relay 中继：扫描 pending 事件，发布后标记 published。
func (s *OutboxService) RelayPending(broker *MemoryBroker) {
	for i := range s.outbox {
		if s.outbox[i].Status == "pending" {
			// 发布到消息中间件
			broker.published = append(broker.published, s.outbox[i].EventID)
			// 标记为已发布
			s.outbox[i].Status = "published"
		}
	}
}

// Orders 获取所有订单。
func (s *OutboxService) Orders() []Order { return s.orders }

// Outbox 获取所有 outbox 事件。
func (s *OutboxService) Outbox() []OutboxEvent { return s.outbox }

// Published 获取已发布的事件ID列表。
func (b *MemoryBroker) Published() []string { return b.published }
