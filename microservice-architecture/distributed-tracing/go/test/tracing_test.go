package test

import (
	"testing"

	src "distributed-tracing-go/src"
)

func TestTracePropagation(t *testing.T) {
	gateway := src.GatewayEntry("TRACE-1001")
	order := src.ChildSpan(gateway, "order-service", "SPAN-ORDER")
	inventory := src.ChildSpan(order, "inventory-service", "SPAN-INVENTORY")

	if gateway.TraceID != order.TraceID || order.TraceID != inventory.TraceID {
		t.Fatalf("expected all spans to share trace id")
	}
	if order.ParentSpanID != gateway.SpanID {
		t.Fatalf("expected order parent to be gateway")
	}
	if inventory.ParentSpanID != order.SpanID {
		t.Fatalf("expected inventory parent to be order")
	}
}
