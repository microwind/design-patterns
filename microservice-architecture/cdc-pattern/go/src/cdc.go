// Package src 实现了变更数据捕获（CDC Pattern）的核心逻辑。
//
// 【设计模式】
//   - 观察者模式（Observer Pattern）：Broker 接收变更事件并分发给下游订阅者。
//   - 代理模式（Proxy Pattern）：RelayChanges 作为中间代理，解耦 DataStore 与 Broker。
//
// 【架构思想】
//   业务写入时同步追加变更日志，Connector 扫描未处理变更并发布到 Broker，
//   避免双写不一致。
//
// 【开源对比】
//   - Go 生态中通常使用 Debezium + Kafka Connect 或 Maxwell
//   本示例用切片 + 内存 Broker 简化，省略了数据库日志解析。
package src

// ChangeRecord 表示一条变更记录，processed 标记是否已发布。
type ChangeRecord struct {
	ChangeID    string
	AggregateID string
	ChangeType  string
	Processed   bool
}

// DataStore 模拟数据库，业务写入时自动追加变更记录。
type DataStore struct {
	changes []ChangeRecord
}

// Broker 模拟消息代理（Kafka / RabbitMQ）。
type Broker struct {
	published []string
}

func NewDataStore() *DataStore {
	return &DataStore{}
}

func NewBroker() *Broker {
	return &Broker{}
}

// CreateOrder 创建订单，同时追加一条 order_created 变更记录。
func (d *DataStore) CreateOrder(orderID string) {
	d.changes = append(d.changes, ChangeRecord{
		ChangeID:    "CHG-" + orderID,
		AggregateID: orderID,
		ChangeType:  "order_created",
		Processed:   false,
	})
}

// RelayChanges 扫描未处理变更，发布到 Broker 并标记为已处理（Connector 角色）。
func (d *DataStore) RelayChanges(b *Broker) {
	for i := range d.changes {
		if !d.changes[i].Processed {
			b.published = append(b.published, d.changes[i].ChangeID)
			d.changes[i].Processed = true
		}
	}
}

func (d *DataStore) Changes() []ChangeRecord {
	return d.changes
}

func (b *Broker) Published() []string {
	return b.published
}
