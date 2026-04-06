package test

import (
	"strings"
	"testing"

	src "api-versioning-go/src"
)

func buildRouter() *src.VersionedRouter {
	router := src.NewVersionedRouter("v1")
	router.Register("v1", src.ProductHandlerV1)
	router.Register("v2", src.ProductHandlerV2)
	return router
}

func TestDefaultVersionFallsBackToV1(t *testing.T) {
	router := buildRouter()

	response := router.Handle(src.Request{
		Path:    "/products/P100",
		Headers: map[string]string{},
	})

	if response.Version != "v1" {
		t.Fatalf("expected v1, got %s", response.Version)
	}
	if !strings.Contains(response.Body, `"name":"Mechanical Keyboard"`) {
		t.Fatalf("expected v1 payload")
	}
}

func TestHeaderCanSelectV2(t *testing.T) {
	router := buildRouter()

	response := router.Handle(src.Request{
		Path: "/products/P100",
		Headers: map[string]string{
			"X-API-Version": "2",
		},
	})

	if response.Version != "v2" {
		t.Fatalf("expected v2, got %s", response.Version)
	}
	if !strings.Contains(response.Body, `"inventoryStatus":"IN_STOCK"`) {
		t.Fatalf("expected v2 payload")
	}
}

func TestUnsupportedVersionReturnsBadRequest(t *testing.T) {
	router := buildRouter()

	response := router.Handle(src.Request{
		Path: "/products/P100",
		Headers: map[string]string{
			"X-API-Version": "9",
		},
	})

	if response.StatusCode != 400 {
		t.Fatalf("expected 400, got %d", response.StatusCode)
	}
}
