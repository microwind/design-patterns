package test

import (
	"testing"

	src "service-discovery-go/src"
)

func TestRegistryStoresAndReturnsInstances(t *testing.T) {
	registry := src.NewServiceRegistry()
	registry.Register("inventory-service", src.ServiceInstance{ID: "inventory-a", Address: "10.0.0.1:8081"})
	registry.Register("inventory-service", src.ServiceInstance{ID: "inventory-b", Address: "10.0.0.2:8081"})

	instances := registry.Instances("inventory-service")
	if len(instances) != 2 {
		t.Fatalf("expected 2 instances, got %d", len(instances))
	}
}

func TestRoundRobinDiscovererCyclesThroughInstances(t *testing.T) {
	registry := src.NewServiceRegistry()
	registry.Register("inventory-service", src.ServiceInstance{ID: "inventory-a", Address: "10.0.0.1:8081"})
	registry.Register("inventory-service", src.ServiceInstance{ID: "inventory-b", Address: "10.0.0.2:8081"})

	discoverer := src.NewRoundRobinDiscoverer(registry)

	first, ok := discoverer.Next("inventory-service")
	if !ok || first.ID != "inventory-a" {
		t.Fatalf("expected first instance inventory-a, got %+v", first)
	}

	second, ok := discoverer.Next("inventory-service")
	if !ok || second.ID != "inventory-b" {
		t.Fatalf("expected second instance inventory-b, got %+v", second)
	}

	third, ok := discoverer.Next("inventory-service")
	if !ok || third.ID != "inventory-a" {
		t.Fatalf("expected round robin to cycle back to inventory-a, got %+v", third)
	}
}

func TestRegistryDeregisterRemovesInstance(t *testing.T) {
	registry := src.NewServiceRegistry()
	registry.Register("inventory-service", src.ServiceInstance{ID: "inventory-a", Address: "10.0.0.1:8081"})
	registry.Register("inventory-service", src.ServiceInstance{ID: "inventory-b", Address: "10.0.0.2:8081"})

	removed := registry.Deregister("inventory-service", "inventory-a")
	if !removed {
		t.Fatalf("expected deregister to succeed")
	}

	instances := registry.Instances("inventory-service")
	if len(instances) != 1 || instances[0].ID != "inventory-b" {
		t.Fatalf("expected only inventory-b to remain, got %+v", instances)
	}
}
