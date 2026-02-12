package event

import "time"

// 订单事件类型常量
const (
	OrderCreatedEvent   = "order.created"
	OrderPaidEvent      = "order.paid"
	OrderShippedEvent   = "order.shipped"
	OrderDeliveredEvent = "order.delivered"
	OrderCancelledEvent = "order.cancelled"
	OrderRefundedEvent  = "order.refunded"
)

// OrderEvent 订单事件
type OrderEvent struct {
	BaseEvent
	OrderID     int64   `json:"order_id"`
	OrderNo     string  `json:"order_no"`
	UserID      int64   `json:"user_id"`
	UserEmail   string  `json:"user_email"`
	UserName    string  `json:"user_name"`
	TotalAmount float64 `json:"total_amount"`
	Status      string  `json:"status"`
}

// EventData 返回事件数据
func (e *OrderEvent) EventData() interface{} {
	return e
}

// NewOrderCreatedEvent 创建订单创建事件
func NewOrderCreatedEvent(orderID int64, orderNo string, userID int64, userEmail, userName string, totalAmount float64) *OrderEvent {
	return &OrderEvent{
		BaseEvent: BaseEvent{
			Type:      OrderCreatedEvent,
			Timestamp: time.Now(),
		},
		OrderID:     orderID,
		OrderNo:     orderNo,
		UserID:      userID,
		UserEmail:   userEmail,
		UserName:    userName,
		TotalAmount: totalAmount,
		Status:      "PENDING",
	}
}

// NewOrderPaidEvent 创建订单支付事件
func NewOrderPaidEvent(orderID int64, orderNo string, userID int64, userEmail, userName string, totalAmount float64) *OrderEvent {
	return &OrderEvent{
		BaseEvent: BaseEvent{
			Type:      OrderPaidEvent,
			Timestamp: time.Now(),
		},
		OrderID:     orderID,
		OrderNo:     orderNo,
		UserID:      userID,
		UserEmail:   userEmail,
		UserName:    userName,
		TotalAmount: totalAmount,
		Status:      "PAID",
	}
}

// NewOrderCancelledEvent 创建订单取消事件
func NewOrderCancelledEvent(orderID int64, orderNo string, userID int64, userEmail, userName string) *OrderEvent {
	return &OrderEvent{
		BaseEvent: BaseEvent{
			Type:      OrderCancelledEvent,
			Timestamp: time.Now(),
		},
		OrderID:   orderID,
		OrderNo:   orderNo,
		UserID:    userID,
		UserEmail: userEmail,
		UserName:  userName,
		Status:    "CANCELLED",
	}
}
