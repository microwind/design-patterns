package order

import "time"

// OrderResponse 订单响应
type OrderResponse struct {
	ID          int64              `json:"id"`
	OrderNo     string             `json:"order_no"`
	UserID      int64              `json:"user_id"`
	TotalAmount float64            `json:"total_amount"`
	Status      string             `json:"status"`
	Items       []OrderItemResponse `json:"items"`
	CreatedAt   time.Time          `json:"created_at"`
	UpdatedAt   time.Time          `json:"updated_at"`
}

// OrderItemResponse 订单项响应
type OrderItemResponse struct {
	ProductID   int64   `json:"product_id"`
	ProductName string  `json:"product_name"`
	Quantity    int     `json:"quantity"`
	Price       float64 `json:"price"`
	Subtotal    float64 `json:"subtotal"`
}
