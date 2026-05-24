// Package event 定义领域事件与发布/订阅端口。
//
// 领域事件表达"已发生的事实",字段公开符合 Go 习惯,但仅由各事件工厂函数
// 构造,创建后不应再修改——这是领域事件不可变性的契约约定。
package event

import "time"

// DomainEvent 领域事件接口。
type DomainEvent interface {
	// EventType 事件类型(过去时态语义)。
	EventType() string

	// OccurredOn 事件发生时间。
	OccurredOn() time.Time

	// EventData 事件数据载荷。
	EventData() interface{}
}

// BaseEvent 基础事件,提供事件类型与时间戳的公共字段。
//
// 字段公开仅供 JSON 序列化使用,业务代码不应直接构造或修改 BaseEvent,
// 而应通过具体事件类型的 NewXxxEvent 工厂函数。
type BaseEvent struct {
	Type      string    `json:"type"`
	Timestamp time.Time `json:"timestamp"`
}

// EventType 返回事件类型。
func (e *BaseEvent) EventType() string { return e.Type }

// OccurredOn 返回事件发生时间。
func (e *BaseEvent) OccurredOn() time.Time { return e.Timestamp }
