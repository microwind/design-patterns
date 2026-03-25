package src

type InventoryService struct {
	stock map[string]int
}

func NewInventoryService() *InventoryService {
	return &InventoryService{stock: map[string]int{
		"SKU-BOOK": 10,
		"SKU-PEN":  1,
	}}
}

func (s *InventoryService) Reserve(sku string, quantity int) bool {
	available, ok := s.stock[sku]
	if !ok || available < quantity {
		return false
	}
	s.stock[sku] = available - quantity
	return true
}

func (s *InventoryService) Available(sku string) int {
	return s.stock[sku]
}
