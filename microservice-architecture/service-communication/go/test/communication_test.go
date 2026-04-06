package test

import (
	"testing"

	src "service-communication-go/src"
)

func TestSynchronousWorkflow(t *testing.T) {
	inventory := src.NewInventoryService(map[string]int{"SKU-BOOK": 5})
	payment := src.NewPaymentService(nil)
	service := src.NewSynchronousOrderService(inventory, payment)

	order := service.PlaceOrder("ORD-1001", "SKU-BOOK", 2)
	if order.Status != "CREATED" {
		t.Fatalf("expected order to be created, got %s", order.Status)
	}
}

func TestAsynchronousWorkflowTransitionsFromPendingToCreated(t *testing.T) {
	inventory := src.NewInventoryService(map[string]int{"SKU-BOOK": 5})
	payment := src.NewPaymentService(nil)
	bus := src.NewEventBus()
	store := src.NewOrderStore()
	src.RegisterAsyncWorkflow(bus, store, inventory, payment)

	service := src.NewAsyncOrderService(bus, store)
	initial := service.PlaceOrder("ORD-2001", "SKU-BOOK", 2)
	if initial.Status != "PENDING" {
		t.Fatalf("expected initial status to be PENDING, got %s", initial.Status)
	}

	bus.Drain()
	final := store.Get("ORD-2001")
	if final.Status != "CREATED" {
		t.Fatalf("expected final status to be CREATED, got %s", final.Status)
	}
}

func TestAsynchronousWorkflowCanFailPayment(t *testing.T) {
	inventory := src.NewInventoryService(map[string]int{"SKU-BOOK": 5})
	payment := src.NewPaymentService([]string{"ORD-2002"})
	bus := src.NewEventBus()
	store := src.NewOrderStore()
	src.RegisterAsyncWorkflow(bus, store, inventory, payment)

	service := src.NewAsyncOrderService(bus, store)
	service.PlaceOrder("ORD-2002", "SKU-BOOK", 1)
	bus.Drain()

	final := store.Get("ORD-2002")
	if final.Status != "PAYMENT_FAILED" {
		t.Fatalf("expected status PAYMENT_FAILED, got %s", final.Status)
	}
}
