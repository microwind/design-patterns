package event

import "time"

// 订单领域事件类型常量(过去时态,表达"已发生的事实")。
const (
	OrderCreatedEvent   = "order.created"
	OrderPaidEvent      = "order.paid"
	OrderShippedEvent   = "order.shipped"
	OrderDeliveredEvent = "order.delivered"
	OrderCancelledEvent = "order.cancelled"
	OrderRefundedEvent  = "order.refunded"
)

// OrderEvent 订单领域事件。
//
// 仅携带订单聚合根自身的状态,不包含跨上下文信息(如用户邮箱/姓名)。
// 下游消费者若需用户信息,通过 UserID 经防腐层(UserInfoQueryClient)查询。
//
// 字段保留公开符合 Go 习惯,但仅工厂函数构造,创建后请勿修改——
// 这是领域事件不可变性的契约约定。
type OrderEvent struct {
	BaseEvent
	OrderID     int64   `json:"order_id"`
	OrderNo     string  `json:"order_no"`
	UserID      int64   `json:"user_id"`
	TotalAmount float64 `json:"total_amount"`
	Status      string  `json:"status"`
}

// EventData 返回事件数据。
func (e *OrderEvent) EventData() interface{} { return e }

// NewOrderCreatedEvent 构造订单创建事件。
func NewOrderCreatedEvent(orderID int64, orderNo string, userID int64, totalAmount float64, status string) *OrderEvent {
	return newOrderEvent(OrderCreatedEvent, orderID, orderNo, userID, totalAmount, status)
}

// NewOrderPaidEvent 构造订单支付事件。
func NewOrderPaidEvent(orderID int64, orderNo string, userID int64, totalAmount float64, status string) *OrderEvent {
	return newOrderEvent(OrderPaidEvent, orderID, orderNo, userID, totalAmount, status)
}

// NewOrderShippedEvent 构造订单发货事件。
func NewOrderShippedEvent(orderID int64, orderNo string, userID int64, totalAmount float64, status string) *OrderEvent {
	return newOrderEvent(OrderShippedEvent, orderID, orderNo, userID, totalAmount, status)
}

// NewOrderDeliveredEvent 构造订单送达事件。
func NewOrderDeliveredEvent(orderID int64, orderNo string, userID int64, totalAmount float64, status string) *OrderEvent {
	return newOrderEvent(OrderDeliveredEvent, orderID, orderNo, userID, totalAmount, status)
}

// NewOrderCancelledEvent 构造订单取消事件。
func NewOrderCancelledEvent(orderID int64, orderNo string, userID int64, totalAmount float64, status string) *OrderEvent {
	return newOrderEvent(OrderCancelledEvent, orderID, orderNo, userID, totalAmount, status)
}

// NewOrderRefundedEvent 构造订单退款事件。
func NewOrderRefundedEvent(orderID int64, orderNo string, userID int64, totalAmount float64, status string) *OrderEvent {
	return newOrderEvent(OrderRefundedEvent, orderID, orderNo, userID, totalAmount, status)
}

func newOrderEvent(eventType string, orderID int64, orderNo string, userID int64, totalAmount float64, status string) *OrderEvent {
	return &OrderEvent{
		BaseEvent: BaseEvent{
			Type:      eventType,
			Timestamp: time.Now(),
		},
		OrderID:     orderID,
		OrderNo:     orderNo,
		UserID:      userID,
		TotalAmount: totalAmount,
		Status:      status,
	}
}
