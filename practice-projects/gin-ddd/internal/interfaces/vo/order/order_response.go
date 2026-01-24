package order

import "time"

// OrderResponse 订单响应
type OrderResponse struct {
	ID          int64              `json:"id"`
	OrderNo     string             `json:"order_no"`
	UserID      int64              `json:"user_id"`
	TotalAmount float64            `json:"total_amount"`
	Status      string             `json:"status"`
	CreatedAt   time.Time          `json:"created_at"`
	UpdatedAt   time.Time          `json:"updated_at"`
}
