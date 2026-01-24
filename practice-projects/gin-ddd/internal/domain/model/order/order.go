package order

import (
	"errors"
	"time"
)

// Order 订单实体（聚合根）
type Order struct {
	OrderID     int64       `json:"order_id"`
	OrderNo     string      `json:"order_no"`
	UserID      int64       `json:"user_id"`
	TotalAmount float64     `json:"total_amount"`
	Status      OrderStatus `json:"status"`
	CreatedAt   time.Time   `json:"created_at"`
	UpdatedAt   time.Time   `json:"updated_at"`
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
func NewOrder(orderNo string, userID int64, totalAmount float64) (*Order, error) {
	if orderNo == "" {
		return nil, errors.New("订单号不能为空")
	}
	if userID <= 0 {
		return nil, errors.New("用户ID无效")
	}
	if totalAmount <= 0 {
		return nil, errors.New("订单金额无效")
	}

	now := time.Now()
	return &Order{
		OrderNo:     orderNo,
		UserID:      userID,
		TotalAmount: totalAmount,
		Status:      OrderStatusPending,
		CreatedAt:   now,
		UpdatedAt:   now,
	}, nil
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
