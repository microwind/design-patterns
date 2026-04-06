package test

import (
	"testing"

	src "feature-flag-go/src"
)

func TestFeatureFlag(t *testing.T) {
	service := src.NewFeatureFlagService()
	service.Set("new-checkout", src.FeatureFlag{
		DefaultEnabled: false,
		Allowlist:      map[string]bool{"user-1": true},
	})
	if !service.Enabled("new-checkout", "user-1") {
		t.Fatalf("expected allowlisted user to be enabled")
	}
	if service.Enabled("new-checkout", "user-2") {
		t.Fatalf("expected non-allowlisted user to be disabled")
	}
}
