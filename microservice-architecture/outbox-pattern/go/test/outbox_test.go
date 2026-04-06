package test

import (
	"testing"

	src "outbox-pattern-go/src"
)

func TestOutboxPattern(t *testing.T) {
	service := src.NewOutboxService()
	broker := src.NewMemoryBroker()

	service.CreateOrder("ORD-1001")
	if len(service.Orders()) != 1 || len(service.Outbox()) != 1 || service.Outbox()[0].Status != "pending" {
		t.Fatalf("expected order and pending outbox event")
	}

	service.RelayPending(broker)
	if len(broker.Published()) != 1 || service.Outbox()[0].Status != "published" {
		t.Fatalf("expected event to be published once")
	}

	service.RelayPending(broker)
	if len(broker.Published()) != 1 {
		t.Fatalf("expected relay rerun to avoid duplicate publish")
	}
}
