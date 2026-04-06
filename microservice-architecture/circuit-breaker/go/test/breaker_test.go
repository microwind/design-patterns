package test

import (
	"testing"

	src "circuit-breaker-go/src"
)

func TestCircuitBreakerStateMachine(t *testing.T) {
	breaker := src.NewCircuitBreaker(2)
	if breaker.State() != "closed" {
		t.Fatalf("expected initial state closed")
	}
	breaker.RecordFailure()
	breaker.RecordFailure()
	if breaker.State() != "open" {
		t.Fatalf("expected breaker to open")
	}
	breaker.Probe(true)
	if breaker.State() != "closed" {
		t.Fatalf("expected successful probe to close breaker")
	}
	breaker.RecordFailure()
	breaker.RecordFailure()
	breaker.Probe(false)
	if breaker.State() != "open" {
		t.Fatalf("expected failed probe to reopen breaker")
	}
}
