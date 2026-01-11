package event

import "time"

// DomainEvent 领域事件接口
type DomainEvent interface {
	// EventType 事件类型
	EventType() string

	// OccurredOn 事件发生时间
	OccurredOn() time.Time

	// EventData 事件数据
	EventData() interface{}
}

// BaseEvent 基础事件
type BaseEvent struct {
	Type      string    `json:"type"`
	Timestamp time.Time `json:"timestamp"`
}

// EventType 返回事件类型
func (e *BaseEvent) EventType() string {
	return e.Type
}

// OccurredOn 返回事件发生时间
func (e *BaseEvent) OccurredOn() time.Time {
	return e.Timestamp
}
