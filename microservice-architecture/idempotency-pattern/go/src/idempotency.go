package src

import "fmt"

type OrderResponse struct {
	OrderID  string
	SKU      string
	Quantity int
	Status   string
	Replayed bool
}

type storedResult struct {
	fingerprint string
	response    OrderResponse
}

type IdempotencyOrderService struct {
	store map[string]storedResult
}

func NewIdempotencyOrderService() *IdempotencyOrderService {
	return &IdempotencyOrderService{store: map[string]storedResult{}}
}

func (s *IdempotencyOrderService) CreateOrder(idempotencyKey string, orderID string, sku string, quantity int) OrderResponse {
	fingerprint := requestFingerprint(orderID, sku, quantity)
	if existing, ok := s.store[idempotencyKey]; ok {
		if existing.fingerprint != fingerprint {
			return OrderResponse{
				OrderID:  orderID,
				SKU:      sku,
				Quantity: quantity,
				Status:   "CONFLICT",
				Replayed: false,
			}
		}

		response := existing.response
		response.Replayed = true
		return response
	}

	response := OrderResponse{
		OrderID:  orderID,
		SKU:      sku,
		Quantity: quantity,
		Status:   "CREATED",
		Replayed: false,
	}
	s.store[idempotencyKey] = storedResult{fingerprint: fingerprint, response: response}
	return response
}

func requestFingerprint(orderID string, sku string, quantity int) string {
	return fmt.Sprintf("%s|%s|%d", orderID, sku, quantity)
}
