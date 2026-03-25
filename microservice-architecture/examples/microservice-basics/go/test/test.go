package main

import (
	"fmt"

	"microservice-basics-go/src"
)

func assertEqualString(expected string, actual string, message string) {
	if expected != actual {
		panic(fmt.Sprintf("%s expected=%s actual=%s", message, expected, actual))
	}
}

func assertEqualInt(expected int, actual int, message string) {
	if expected != actual {
		panic(fmt.Sprintf("%s expected=%d actual=%d", message, expected, actual))
	}
}

func main() {
	inventory := src.NewInventoryService()
	orderService := src.NewOrderService(inventory)

	success := orderService.CreateOrder("ORD-1001", "SKU-BOOK", 2)
	assertEqualString("CREATED", success.Status, "status should be CREATED")
	assertEqualInt(8, inventory.Available("SKU-BOOK"), "stock should decrease")

	failed := orderService.CreateOrder("ORD-1002", "SKU-PEN", 2)
	assertEqualString("REJECTED", failed.Status, "status should be REJECTED")
	assertEqualInt(1, inventory.Available("SKU-PEN"), "stock should remain")

	fmt.Println("microservice-basics(go) tests passed")
}
