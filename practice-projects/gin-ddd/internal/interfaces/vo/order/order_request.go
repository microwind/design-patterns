package order

// CreateOrderRequest 创建订单请求
type CreateOrderRequest struct {
	UserID      int64   `json:"user_id" binding:"required,gt=0"`
	TotalAmount float64 `json:"total_amount" binding:"required,gt=0"`
}
