package test

import (
	"testing"

	src "rate-limiting-go/src"
)

func TestFixedWindowLimiter(t *testing.T) {
	limiter := src.NewFixedWindowLimiter(3)
	if !limiter.Allow() || !limiter.Allow() || !limiter.Allow() {
		t.Fatalf("expected first three requests to pass")
	}
	if limiter.Allow() {
		t.Fatalf("expected fourth request to be rejected")
	}
	limiter.AdvanceWindow()
	if !limiter.Allow() {
		t.Fatalf("expected request after window advance to pass")
	}
}
