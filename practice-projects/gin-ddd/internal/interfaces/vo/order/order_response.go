package order

import (
	orderDTO "gin-ddd/internal/application/dto/order"
	"time"
)

// OrderResponse 订单响应
type OrderResponse struct {
	ID          int64     `json:"id"`
	OrderNo     string    `json:"order_no"`
	UserID      int64     `json:"user_id"`
	TotalAmount float64   `json:"total_amount"`
	Status      string    `json:"status"`
	CreatedAt   time.Time `json:"created_at"`
	UpdatedAt   time.Time `json:"updated_at"`
}

// FromOrderDTO 将订单DTO转换为响应VO
func FromOrderDTO(dto *orderDTO.OrderDTO) *OrderResponse {
	if dto == nil {
		return nil
	}
	return &OrderResponse{
		ID:          dto.OrderID,
		OrderNo:     dto.OrderNo,
		UserID:      dto.UserID,
		TotalAmount: dto.TotalAmount,
		Status:      dto.Status,
		CreatedAt:   dto.CreatedAt,
		UpdatedAt:   dto.UpdatedAt,
	}
}

// FromOrderDTOs 批量将订单DTO转换为响应VO
func FromOrderDTOs(dtos []*orderDTO.OrderDTO) []*OrderResponse {
	responses := make([]*OrderResponse, 0, len(dtos))
	for _, dto := range dtos {
		responses = append(responses, FromOrderDTO(dto))
	}
	return responses
}
