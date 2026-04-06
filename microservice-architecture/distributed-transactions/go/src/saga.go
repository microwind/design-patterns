package src

type SagaOrder struct {
	OrderID string
	Status  string
}

type InventoryService struct {
	bookStock int
}

type PaymentService struct {
	fail bool
}

type SagaCoordinator struct {
	inventory *InventoryService
	payment   *PaymentService
}

func NewSagaCoordinator(stock int, paymentFails bool) *SagaCoordinator {
	return &SagaCoordinator{
		inventory: &InventoryService{bookStock: stock},
		payment:   &PaymentService{fail: paymentFails},
	}
}

func (s *SagaCoordinator) Execute(orderID string, sku string, quantity int) SagaOrder {
	order := SagaOrder{OrderID: orderID, Status: "PENDING"}

	if !s.inventory.reserve(sku, quantity) {
		order.Status = "CANCELLED"
		return order
	}

	if !s.payment.charge(orderID) {
		s.inventory.release(sku, quantity)
		order.Status = "CANCELLED"
		return order
	}

	order.Status = "COMPLETED"
	return order
}

func (s *SagaCoordinator) AvailableStock() int {
	return s.inventory.bookStock
}

func (i *InventoryService) reserve(sku string, quantity int) bool {
	if sku != "SKU-BOOK" || quantity <= 0 || i.bookStock < quantity {
		return false
	}
	i.bookStock -= quantity
	return true
}

func (i *InventoryService) release(sku string, quantity int) {
	if sku == "SKU-BOOK" && quantity > 0 {
		i.bookStock += quantity
	}
}

func (p *PaymentService) charge(orderID string) bool {
	return !p.fail
}
