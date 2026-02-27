package event

import "time"

const (
	OrderCreatedEvent   = "order.created"
	OrderPaidEvent      = "order.paid"
	OrderShippedEvent   = "order.shipped"
	OrderDeliveredEvent = "order.delivered"
	OrderCancelledEvent = "order.cancelled"
	OrderRefundedEvent  = "order.refunded"
)

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

func (e *OrderEvent) EventData() interface{} {
	return e
}

func NewOrderCreated(orderID int64, orderNo string, userID int64, userEmail, userName string, totalAmount float64) *OrderEvent {
	return &OrderEvent{BaseEvent: BaseEvent{Type: OrderCreatedEvent, Timestamp: time.Now()}, OrderID: orderID, OrderNo: orderNo, UserID: userID, UserEmail: userEmail, UserName: userName, TotalAmount: totalAmount, Status: "PENDING"}
}

func NewOrderPaid(orderID int64, orderNo string, userID int64, userEmail, userName string, totalAmount float64) *OrderEvent {
	return &OrderEvent{BaseEvent: BaseEvent{Type: OrderPaidEvent, Timestamp: time.Now()}, OrderID: orderID, OrderNo: orderNo, UserID: userID, UserEmail: userEmail, UserName: userName, TotalAmount: totalAmount, Status: "PAID"}
}

func NewOrderCancelled(orderID int64, orderNo string, userID int64, userEmail, userName string) *OrderEvent {
	return &OrderEvent{BaseEvent: BaseEvent{Type: OrderCancelledEvent, Timestamp: time.Now()}, OrderID: orderID, OrderNo: orderNo, UserID: userID, UserEmail: userEmail, UserName: userName, Status: "CANCELLED"}
}
