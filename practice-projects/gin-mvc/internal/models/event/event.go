package event

import (
	"context"
	"time"
)

type DomainEvent interface {
	EventType() string
	OccurredOn() time.Time
	EventData() interface{}
}

type BaseEvent struct {
	Type      string    `json:"type"`
	Timestamp time.Time `json:"timestamp"`
}

func (e *BaseEvent) EventType() string {
	return e.Type
}

func (e *BaseEvent) OccurredOn() time.Time {
	return e.Timestamp
}

type Publisher interface {
	Publish(ctx context.Context, topic string, evt DomainEvent) error
	Close() error
}

type Consumer interface {
	Subscribe(topic string, handler Handler) error
	Start() error
	Close() error
}

type Handler func(ctx context.Context, evt DomainEvent) error
