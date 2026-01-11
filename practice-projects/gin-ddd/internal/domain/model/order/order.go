package order

import (
	"errors"
	"time"
)

// Order 订单实体（聚合根）
type Order struct {
	ID          int64       `json:"id"`
	OrderNo     string      `json:"order_no"`
	UserID      int64       `json:"user_id"`
	TotalAmount float64     `json:"total_amount"`
	Status      OrderStatus `json:"status"`
	Items       []OrderItem `json:"items"`
	CreatedAt   time.Time   `json:"created_at"`
	UpdatedAt   time.Time   `json:"updated_at"`
}

// OrderItem 订单项（值对象）
type OrderItem struct {
	ProductID   int64   `json:"product_id"`
	ProductName string  `json:"product_name"`
	Quantity    int     `json:"quantity"`
	Price       float64 `json:"price"`
	Subtotal    float64 `json:"subtotal"`
}

// OrderStatus 订单状态
type OrderStatus string

const (
	OrderStatusPending   OrderStatus = "PENDING"   // 待支付
	OrderStatusPaid      OrderStatus = "PAID"      // 已支付
	OrderStatusShipped   OrderStatus = "SHIPPED"   // 已发货
	OrderStatusDelivered OrderStatus = "DELIVERED" // 已送达
	OrderStatusCancelled OrderStatus = "CANCELLED" // 已取消
	OrderStatusRefunded  OrderStatus = "REFUNDED"  // 已退款
)

// NewOrder 创建新订单
func NewOrder(orderNo string, userID int64, items []OrderItem) (*Order, error) {
	if orderNo == "" {
		return nil, errors.New("订单号不能为空")
	}
	if userID <= 0 {
		return nil, errors.New("用户ID无效")
	}
	if len(items) == 0 {
		return nil, errors.New("订单项不能为空")
	}

	// 计算总金额
	totalAmount := 0.0
	for _, item := range items {
		totalAmount += item.Subtotal
	}

	now := time.Now()
	return &Order{
		OrderNo:     orderNo,
		UserID:      userID,
		TotalAmount: totalAmount,
		Status:      OrderStatusPending,
		Items:       items,
		CreatedAt:   now,
		UpdatedAt:   now,
	}, nil
}

// NewOrderItem 创建订单项
func NewOrderItem(productID int64, productName string, quantity int, price float64) OrderItem {
	return OrderItem{
		ProductID:   productID,
		ProductName: productName,
		Quantity:    quantity,
		Price:       price,
		Subtotal:    float64(quantity) * price,
	}
}

// Pay 支付订单
func (o *Order) Pay() error {
	if o.Status != OrderStatusPending {
		return errors.New("只有待支付订单可以支付")
	}
	o.Status = OrderStatusPaid
	o.UpdatedAt = time.Now()
	return nil
}

// Ship 发货
func (o *Order) Ship() error {
	if o.Status != OrderStatusPaid {
		return errors.New("只有已支付订单可以发货")
	}
	o.Status = OrderStatusShipped
	o.UpdatedAt = time.Now()
	return nil
}

// Deliver 确认送达
func (o *Order) Deliver() error {
	if o.Status != OrderStatusShipped {
		return errors.New("只有已发货订单可以确认送达")
	}
	o.Status = OrderStatusDelivered
	o.UpdatedAt = time.Now()
	return nil
}

// Cancel 取消订单
func (o *Order) Cancel() error {
	if o.Status != OrderStatusPending {
		return errors.New("只有待支付订单可以取消")
	}
	o.Status = OrderStatusCancelled
	o.UpdatedAt = time.Now()
	return nil
}

// Refund 退款
func (o *Order) Refund() error {
	if o.Status != OrderStatusPaid && o.Status != OrderStatusShipped {
		return errors.New("只有已支付或已发货的订单可以退款")
	}
	o.Status = OrderStatusRefunded
	o.UpdatedAt = time.Now()
	return nil
}

// AddItem 添加订单项
func (o *Order) AddItem(item OrderItem) {
	o.Items = append(o.Items, item)
	o.TotalAmount += item.Subtotal
	o.UpdatedAt = time.Now()
}

// IsPending 判断是否待支付
func (o *Order) IsPending() bool {
	return o.Status == OrderStatusPending
}

// IsPaid 判断是否已支付
func (o *Order) IsPaid() bool {
	return o.Status == OrderStatusPaid
}

// CanCancel 判断是否可取消
func (o *Order) CanCancel() bool {
	return o.Status == OrderStatusPending
}
