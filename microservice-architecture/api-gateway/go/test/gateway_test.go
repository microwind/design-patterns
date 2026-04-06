package test

import (
	"testing"

	src "api-gateway-go/src"
)

func TestGatewayRoutesAndAddsCorrelationID(t *testing.T) {
	gateway := src.NewAPIGateway()
	gateway.Use(src.RequireUserHeader("/api/orders", "X-User"))
	gateway.Register("/api/orders", src.OrderServiceHandler())
	gateway.Register("/api/inventory", src.InventoryServiceHandler())

	response := gateway.Handle(src.Request{
		Method: "GET",
		Path:   "/api/orders/ORD-1001",
		Headers: map[string]string{
			"X-User":           "jarry",
			"X-Correlation-ID": "trace-1001",
		},
	})

	if response.StatusCode != 200 {
		t.Fatalf("expected 200, got %d", response.StatusCode)
	}
	if response.Headers["X-Correlation-ID"] != "trace-1001" {
		t.Fatalf("expected correlation id to be preserved")
	}
	if response.Headers["X-Upstream-Service"] != "order-service" {
		t.Fatalf("expected request to be routed to order-service")
	}
}

func TestGatewayRejectsUnauthorizedProtectedRequest(t *testing.T) {
	gateway := src.NewAPIGateway()
	gateway.Use(src.RequireUserHeader("/api/orders", "X-User"))
	gateway.Register("/api/orders", src.OrderServiceHandler())

	response := gateway.Handle(src.Request{
		Method:  "GET",
		Path:    "/api/orders/ORD-1001",
		Headers: map[string]string{},
	})

	if response.StatusCode != 401 {
		t.Fatalf("expected 401, got %d", response.StatusCode)
	}
}

func TestGatewayAllowsPublicInventoryRoute(t *testing.T) {
	gateway := src.NewAPIGateway()
	gateway.Use(src.RequireUserHeader("/api/orders", "X-User"))
	gateway.Register("/api/inventory", src.InventoryServiceHandler())

	response := gateway.Handle(src.Request{
		Method:  "GET",
		Path:    "/api/inventory/SKU-BOOK",
		Headers: map[string]string{},
	})

	if response.StatusCode != 200 {
		t.Fatalf("expected 200, got %d", response.StatusCode)
	}
	if response.Headers["X-Upstream-Service"] != "inventory-service" {
		t.Fatalf("expected request to be routed to inventory-service")
	}
}

func TestGatewayReturnsNotFoundForUnknownRoute(t *testing.T) {
	gateway := src.NewAPIGateway()

	response := gateway.Handle(src.Request{
		Method:  "GET",
		Path:    "/api/unknown",
		Headers: map[string]string{},
	})

	if response.StatusCode != 404 {
		t.Fatalf("expected 404, got %d", response.StatusCode)
	}
}
