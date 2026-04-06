package test

import (
	"errors"
	"testing"
	"time"

	src "resilience-patterns-go/src"
)

func TestRetryEventuallySucceeds(t *testing.T) {
	dependency := src.NewScriptedDependency([]src.Result{
		{Err: errors.New("temporary failure")},
		{Err: errors.New("temporary failure")},
		{Value: "OK"},
	})

	value, attempts, err := src.Retry(3, dependency.Call)
	if err != nil {
		t.Fatalf("expected retry to succeed, got %v", err)
	}
	if value != "OK" || attempts != 3 {
		t.Fatalf("expected success on third attempt, got value=%s attempts=%d", value, attempts)
	}
}

func TestTimeoutFailsSlowDependency(t *testing.T) {
	dependency := src.NewScriptedDependency([]src.Result{
		{Value: "SLOW_OK", Delay: 100 * time.Millisecond},
	})

	_, err := src.CallWithTimeout(10*time.Millisecond, dependency.Call)
	if !errors.Is(err, src.ErrTimeout) {
		t.Fatalf("expected timeout error, got %v", err)
	}
}

func TestCircuitBreakerOpensAfterRepeatedFailures(t *testing.T) {
	breaker := src.NewCircuitBreaker(2)
	failingDependency := src.NewScriptedDependency([]src.Result{
		{Err: errors.New("dependency down")},
		{Err: errors.New("dependency still down")},
		{Value: "RECOVERED"},
	})

	first, err := breaker.Execute(failingDependency.Call, "FALLBACK")
	if first != "FALLBACK" || err == nil {
		t.Fatalf("expected fallback on first failure")
	}

	second, err := breaker.Execute(failingDependency.Call, "FALLBACK")
	if second != "FALLBACK" || err == nil {
		t.Fatalf("expected fallback on second failure")
	}

	third, err := breaker.Execute(failingDependency.Call, "FALLBACK")
	if third != "FALLBACK" {
		t.Fatalf("expected open breaker to return fallback")
	}
	if !errors.Is(err, src.ErrCircuitOpen) {
		t.Fatalf("expected circuit open error, got %v", err)
	}

	breaker.Reset()
	fourth, err := breaker.Execute(failingDependency.Call, "FALLBACK")
	if err != nil || fourth != "RECOVERED" {
		t.Fatalf("expected successful recovery after reset, got value=%s err=%v", fourth, err)
	}
}
