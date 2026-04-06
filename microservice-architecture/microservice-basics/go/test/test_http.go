package main

import (
	"fmt"
	"net/http"
	"net/http/httptest"
	"strconv"

	"microservice-basics-go/src"
)

func assertEqualString(expected string, actual string, message string) {
	if expected != actual {
		panic(fmt.Sprintf("%s expected=%s actual=%s", message, expected, actual))
	}
}

func main() {
	stock := 2

	server := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		sku := r.URL.Query().Get("sku")
		qty, _ := strconv.Atoi(r.URL.Query().Get("quantity"))

		if sku == "SKU-BOOK" && qty > 0 && stock >= qty {
			stock -= qty
			w.WriteHeader(http.StatusOK)
			_, _ = w.Write([]byte("OK"))
			return
		}

		w.WriteHeader(http.StatusConflict)
		_, _ = w.Write([]byte("NO_STOCK"))
	}))
	defer server.Close()

	client := src.NewHttpInventoryClient(server.URL)
	service := src.NewOrderService(client)

	success := service.CreateOrder("ORD-2001", "SKU-BOOK", 1)
	assertEqualString("CREATED", success.Status, "http status should be CREATED")

	failed := service.CreateOrder("ORD-2002", "SKU-BOOK", 2)
	assertEqualString("REJECTED", failed.Status, "http status should be REJECTED")

	fmt.Println("microservice-basics(go/http) tests passed")
}
