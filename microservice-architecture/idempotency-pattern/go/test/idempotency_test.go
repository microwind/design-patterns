package test

import (
	"testing"

	src "idempotency-pattern-go/src"
)

func TestIdempotencyPattern(t *testing.T) {
	service := src.NewIdempotencyOrderService()

	first := service.CreateOrder("IDEMP-ORDER-1001", "ORD-1001", "SKU-BOOK", 1)
	if first.Status != "CREATED" || first.Replayed {
		t.Fatalf("expected first request to create order, got %+v", first)
	}

	second := service.CreateOrder("IDEMP-ORDER-1001", "ORD-1001", "SKU-BOOK", 1)
	if second.Status != "CREATED" || !second.Replayed {
		t.Fatalf("expected duplicate request to replay result, got %+v", second)
	}

	conflict := service.CreateOrder("IDEMP-ORDER-1001", "ORD-1001", "SKU-BOOK", 2)
	if conflict.Status != "CONFLICT" || conflict.Replayed {
		t.Fatalf("expected conflicting request to be rejected, got %+v", conflict)
	}
}
