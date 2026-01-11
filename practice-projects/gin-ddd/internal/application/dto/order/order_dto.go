package order

import (
	"gin-ddd/internal/domain/model/order"
	"time"
)

// OrderDTO 订单数据传输对象
type OrderDTO struct {
	ID          int64          `json:"id"`
	OrderNo     string         `json:"order_no"`
	UserID      int64          `json:"user_id"`
	TotalAmount float64        `json:"total_amount"`
	Status      string         `json:"status"`
	Items       []OrderItemDTO `json:"items"`
	CreatedAt   time.Time      `json:"created_at"`
	UpdatedAt   time.Time      `json:"updated_at"`
}

// OrderItemDTO 订单项数据传输对象
type OrderItemDTO struct {
	ProductID   int64   `json:"product_id"`
	ProductName string  `json:"product_name"`
	Quantity    int     `json:"quantity"`
	Price       float64 `json:"price"`
	Subtotal    float64 `json:"subtotal"`
}

// ToDTO 将订单实体转换为DTO
func ToDTO(o *order.Order) *OrderDTO {
	if o == nil {
		return nil
	}

	items := make([]OrderItemDTO, 0, len(o.Items))
	for _, item := range o.Items {
		items = append(items, OrderItemDTO{
			ProductID:   item.ProductID,
			ProductName: item.ProductName,
			Quantity:    item.Quantity,
			Price:       item.Price,
			Subtotal:    item.Subtotal,
		})
	}

	return &OrderDTO{
		ID:          o.ID,
		OrderNo:     o.OrderNo,
		UserID:      o.UserID,
		TotalAmount: o.TotalAmount,
		Status:      string(o.Status),
		Items:       items,
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

	items := make([]order.OrderItem, 0, len(dto.Items))
	for _, item := range dto.Items {
		items = append(items, order.OrderItem{
			ProductID:   item.ProductID,
			ProductName: item.ProductName,
			Quantity:    item.Quantity,
			Price:       item.Price,
			Subtotal:    item.Subtotal,
		})
	}

	return &order.Order{
		ID:          dto.ID,
		OrderNo:     dto.OrderNo,
		UserID:      dto.UserID,
		TotalAmount: dto.TotalAmount,
		Status:      order.OrderStatus(dto.Status),
		Items:       items,
		CreatedAt:   dto.CreatedAt,
		UpdatedAt:   dto.UpdatedAt,
	}
}
