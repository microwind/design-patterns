package test

import (
	"testing"

	src "distributed-transactions-go/src"
)

func TestSagaSuccess(t *testing.T) {
	coordinator := src.NewSagaCoordinator(10, false)
	order := coordinator.Execute("ORD-1001", "SKU-BOOK", 2)
	if order.Status != "COMPLETED" || coordinator.AvailableStock() != 8 {
		t.Fatalf("expected completed order and reduced stock, got %+v stock=%d", order, coordinator.AvailableStock())
	}
}

func TestSagaCompensationOnPaymentFailure(t *testing.T) {
	coordinator := src.NewSagaCoordinator(10, true)
	order := coordinator.Execute("ORD-1002", "SKU-BOOK", 2)
	if order.Status != "CANCELLED" || coordinator.AvailableStock() != 10 {
		t.Fatalf("expected cancelled order and compensated stock, got %+v stock=%d", order, coordinator.AvailableStock())
	}
}
