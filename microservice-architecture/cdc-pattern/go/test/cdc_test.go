package test

import (
	"testing"

	src "cdc-pattern-go/src"
)

func TestCDCConnector(t *testing.T) {
	store := src.NewDataStore()
	broker := src.NewBroker()
	store.CreateOrder("ORD-1001")
	if len(store.Changes()) != 1 || store.Changes()[0].Processed {
		t.Fatalf("expected unprocessed change")
	}
	store.RelayChanges(broker)
	if len(broker.Published()) != 1 || !store.Changes()[0].Processed {
		t.Fatalf("expected published and marked processed")
	}
	store.RelayChanges(broker)
	if len(broker.Published()) != 1 {
		t.Fatalf("expected no duplicate publish")
	}
}
