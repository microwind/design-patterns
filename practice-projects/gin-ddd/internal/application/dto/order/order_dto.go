package order

import (
	"gin-ddd/internal/domain/model/order"
	"time"
)

// OrderDTO 订单数据传输对象
type OrderDTO struct {
	OrderID     int64          `json:"order_id"`
	OrderNo     string         `json:"order_no"`
	UserID      int64          `json:"user_id"`
	TotalAmount float64        `json:"total_amount"`
	Status      string         `json:"status"`
	CreatedAt   time.Time      `json:"created_at"`
	UpdatedAt   time.Time      `json:"updated_at"`
}

// ToDTO 将订单实体转换为DTO
func ToDTO(o *order.Order) *OrderDTO {
	if o == nil {
		return nil
	}

	return &OrderDTO{
		OrderID:     o.OrderID,
		OrderNo:     o.OrderNo,
		UserID:      o.UserID,
		TotalAmount: o.TotalAmount,
		Status:      string(o.Status),
		CreatedAt:   o.CreatedAt,
		UpdatedAt:   o.UpdatedAt,
	}
}

// ToDTOs 批量转换订单实体为DTO
func ToDTOs(orders []*order.Order) []*OrderDTO {
	dtos := make([]*OrderDTO, 0, len(orders))
	for _, o := range orders {
		dtos = append(dtos, ToDTO(o))
	}
	return dtos
}

// ToEntity 将DTO转换为订单实体
func ToEntity(dto *OrderDTO) *order.Order {
	if dto == nil {
		return nil
	}

	return &order.Order{
		OrderID:     dto.OrderID,
		OrderNo:     dto.OrderNo,
		UserID:      dto.UserID,
		TotalAmount: dto.TotalAmount,
		Status:      order.OrderStatus(dto.Status),
		CreatedAt:   dto.CreatedAt,
		UpdatedAt:   dto.UpdatedAt,
	}
}
