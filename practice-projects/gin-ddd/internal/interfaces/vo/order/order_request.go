package order

import "gin-ddd/internal/domain/model/order"

// CreateOrderRequest 创建订单请求
type CreateOrderRequest struct {
	UserID int64             `json:"user_id" binding:"required,gt=0"`
	Items  []OrderItemRequest `json:"items" binding:"required,dive"`
}

// OrderItemRequest 订单项请求
type OrderItemRequest struct {
	ProductID   int64   `json:"product_id" binding:"required,gt=0"`
	ProductName string  `json:"product_name" binding:"required"`
	Quantity    int     `json:"quantity" binding:"required,gt=0"`
	Price       float64 `json:"price" binding:"required,gt=0"`
}

// ToOrderItems 将请求转换为订单项
func (r *CreateOrderRequest) ToOrderItems() []order.OrderItem {
	items := make([]order.OrderItem, 0, len(r.Items))
	for _, item := range r.Items {
		items = append(items, order.NewOrderItem(
			item.ProductID,
			item.ProductName,
			item.Quantity,
			item.Price,
		))
	}
	return items
}
