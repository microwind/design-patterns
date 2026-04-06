package src

type InventoryClient interface {
	Reserve(sku string, quantity int) bool
}
