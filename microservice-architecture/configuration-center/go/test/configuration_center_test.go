package test

import (
	"testing"

	src "configuration-center-go/src"
)

func TestConfigCenterLoadAndRefresh(t *testing.T) {
	center := src.NewConfigCenter()
	center.Put(src.ServiceConfig{
		ServiceName:       "order-service",
		Environment:       "prod",
		Version:           1,
		DbHost:            "db.prod.internal",
		TimeoutMs:         300,
		FeatureOrderAudit: false,
	})

	client := src.NewConfigClient(center, "order-service", "prod")
	loaded, ok := client.Load()
	if !ok || loaded.Version != 1 || loaded.TimeoutMs != 300 {
		t.Fatalf("expected initial config version 1, got %+v ok=%v", loaded, ok)
	}

	center.Put(src.ServiceConfig{
		ServiceName:       "order-service",
		Environment:       "prod",
		Version:           2,
		DbHost:            "db.prod.internal",
		TimeoutMs:         500,
		FeatureOrderAudit: true,
	})

	refreshed, ok := client.Refresh()
	if !ok || refreshed.Version != 2 || !refreshed.FeatureOrderAudit {
		t.Fatalf("expected refreshed config version 2, got %+v ok=%v", refreshed, ok)
	}
}
