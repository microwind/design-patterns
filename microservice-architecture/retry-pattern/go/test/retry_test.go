package test

import (
	"testing"

	src "retry-pattern-go/src"
)

func TestRetrySucceedsEventually(t *testing.T) {
	op := src.NewScriptedOperation(2)
	ok, attempts := src.Retry(3, op.Call)
	if !ok || attempts != 3 {
		t.Fatalf("expected retry to succeed on third attempt")
	}
}

func TestRetryStopsAtMaxAttempts(t *testing.T) {
	op := src.NewScriptedOperation(5)
	ok, attempts := src.Retry(3, op.Call)
	if ok || attempts != 3 {
		t.Fatalf("expected retry to fail after max attempts")
	}
}
