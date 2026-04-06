package src

type OrderService struct {
	inventory InventoryClient
}

func NewOrderService(inventory InventoryClient) *OrderService {
	return &OrderService{inventory: inventory}
}

func (s *OrderService) CreateOrder(orderID string, sku string, quantity int) Order {
	if s.inventory.Reserve(sku, quantity) {
		return Order{OrderID: orderID, Sku: sku, Quantity: quantity, Status: "CREATED"}
	}
	return Order{OrderID: orderID, Sku: sku, Quantity: quantity, Status: "REJECTED"}
}
