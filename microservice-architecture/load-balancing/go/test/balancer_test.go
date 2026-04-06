package test

import (
	"testing"

	src "load-balancing-go/src"
)

func TestRoundRobinBalancer(t *testing.T) {
	balancer := src.NewRoundRobinBalancer([]src.Backend{
		{ID: "node-a"},
		{ID: "node-b"},
		{ID: "node-c"},
	})

	if balancer.Next().ID != "node-a" {
		t.Fatalf("expected first backend to be node-a")
	}
	if balancer.Next().ID != "node-b" {
		t.Fatalf("expected second backend to be node-b")
	}
	if balancer.Next().ID != "node-c" {
		t.Fatalf("expected third backend to be node-c")
	}
}

func TestWeightedRoundRobinBalancer(t *testing.T) {
	balancer := src.NewWeightedRoundRobinBalancer([]src.Backend{
		{ID: "node-a", Weight: 2},
		{ID: "node-b", Weight: 1},
	})

	counts := map[string]int{}
	for i := 0; i < 6; i++ {
		counts[balancer.Next().ID]++
	}

	if counts["node-a"] != 4 || counts["node-b"] != 2 {
		t.Fatalf("expected weighted distribution 4:2, got %+v", counts)
	}
}

func TestLeastConnectionsBalancer(t *testing.T) {
	balancer := src.NewLeastConnectionsBalancer([]src.Backend{
		{ID: "node-a", ActiveConnections: 2},
		{ID: "node-b", ActiveConnections: 0},
		{ID: "node-c", ActiveConnections: 1},
	})

	first := balancer.Acquire()
	if first.ID != "node-b" {
		t.Fatalf("expected node-b to be selected first, got %s", first.ID)
	}

	second := balancer.Acquire()
	if second.ID != "node-b" && second.ID != "node-c" {
		t.Fatalf("expected least-connections strategy to continue selecting the least busy backend, got %s", second.ID)
	}

	balancer.Release("node-b")
}
