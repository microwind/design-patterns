// Package order 定义订单聚合根。
//
// 聚合根仅承载领域行为与不变量,不耦合任何持久化或框架细节。
// 持久化映射由 infrastructure 层的 OrderDO + Converter 完成。
package order

import (
	"fmt"
	"time"

	"gin-ddd/internal/domain/errors"
	"gin-ddd/internal/domain/event"
)

// Order 订单聚合根。字段保留公开(Go 风格),通过行为方法控制状态迁移,
// 外部代码不应直接赋值 Status / UpdatedAt 等字段。
type Order struct {
	OrderID     int64
	OrderNo     string
	UserID      int64
	TotalAmount float64
	Status      OrderStatus
	CreatedAt   time.Time
	UpdatedAt   time.Time

	// events 收集行为方法产生的领域事件,由应用层 PullEvents 后统一发布。
	// 字段私有,外部不可直接读写。
	events []event.DomainEvent
}

// OrderStatus 订单状态。
type OrderStatus string

const (
	OrderStatusPending   OrderStatus = "PENDING"   // 待支付
	OrderStatusPaid      OrderStatus = "PAID"      // 已支付
	OrderStatusShipped   OrderStatus = "SHIPPED"   // 已发货
	OrderStatusDelivered OrderStatus = "DELIVERED" // 已送达
	OrderStatusCancelled OrderStatus = "CANCELLED" // 已取消
	OrderStatusRefunded  OrderStatus = "REFUNDED"  // 已退款
)

// New 创建新订单。校验入参合法性,内部生成订单号,状态置为 PENDING。
// 不发布 OrderCreatedEvent——id 尚未生成,事件由 MarkCreated 在持久化后补登。
func New(userID int64, totalAmount float64) (*Order, error) {
	if userID <= 0 {
		return nil, errors.NewInvalidArgument("用户ID无效")
	}
	if totalAmount <= 0 {
		return nil, errors.NewInvalidArgument("订单金额无效")
	}

	now := time.Now()
	return &Order{
		OrderNo:     generateOrderNo(),
		UserID:      userID,
		TotalAmount: totalAmount,
		Status:      OrderStatusPending,
		CreatedAt:   now,
		UpdatedAt:   now,
	}, nil
}

// Restore 从持久化数据重建订单,仅供 infrastructure 层的 Converter 调用。
// 重建过程不产生领域事件。
func Restore(id int64, orderNo string, userID int64, totalAmount float64,
	status OrderStatus, createdAt, updatedAt time.Time) *Order {
	return &Order{
		OrderID:     id,
		OrderNo:     orderNo,
		UserID:      userID,
		TotalAmount: totalAmount,
		Status:      status,
		CreatedAt:   createdAt,
		UpdatedAt:   updatedAt,
	}
}

// MarkCreated 在仓储 save 完成、数据库回填主键后由仓储调用一次。
// 合并"赋 ID + 记录 OrderCreatedEvent",对齐 DDD"事件由聚合根记录"的约定。
func (o *Order) MarkCreated(id int64) error {
	if o.OrderID != 0 {
		return errors.NewInvalidState("订单 ID 已存在,不可重复初始化")
	}
	o.OrderID = id
	o.recordEvent(event.NewOrderCreatedEvent(o.OrderID, o.OrderNo, o.UserID, o.TotalAmount, string(o.Status)))
	return nil
}

// Pay 支付订单。
func (o *Order) Pay() error {
	if o.Status != OrderStatusPending {
		return errors.NewInvalidState("只有待支付订单可以支付")
	}
	o.Status = OrderStatusPaid
	o.UpdatedAt = time.Now()
	o.recordEvent(event.NewOrderPaidEvent(o.OrderID, o.OrderNo, o.UserID, o.TotalAmount, string(o.Status)))
	return nil
}

// Ship 发货。
func (o *Order) Ship() error {
	if o.Status != OrderStatusPaid {
		return errors.NewInvalidState("只有已支付订单可以发货")
	}
	o.Status = OrderStatusShipped
	o.UpdatedAt = time.Now()
	o.recordEvent(event.NewOrderShippedEvent(o.OrderID, o.OrderNo, o.UserID, o.TotalAmount, string(o.Status)))
	return nil
}

// Deliver 确认送达。
func (o *Order) Deliver() error {
	if o.Status != OrderStatusShipped {
		return errors.NewInvalidState("只有已发货订单可以确认送达")
	}
	o.Status = OrderStatusDelivered
	o.UpdatedAt = time.Now()
	o.recordEvent(event.NewOrderDeliveredEvent(o.OrderID, o.OrderNo, o.UserID, o.TotalAmount, string(o.Status)))
	return nil
}

// Cancel 取消订单。
func (o *Order) Cancel() error {
	if o.Status != OrderStatusPending {
		return errors.NewInvalidState("只有待支付订单可以取消")
	}
	o.Status = OrderStatusCancelled
	o.UpdatedAt = time.Now()
	o.recordEvent(event.NewOrderCancelledEvent(o.OrderID, o.OrderNo, o.UserID, o.TotalAmount, string(o.Status)))
	return nil
}

// Refund 退款。
func (o *Order) Refund() error {
	if o.Status != OrderStatusPaid && o.Status != OrderStatusShipped {
		return errors.NewInvalidState("只有已支付或已发货的订单可以退款")
	}
	o.Status = OrderStatusRefunded
	o.UpdatedAt = time.Now()
	o.recordEvent(event.NewOrderRefundedEvent(o.OrderID, o.OrderNo, o.UserID, o.TotalAmount, string(o.Status)))
	return nil
}

// IsPending 判断是否待支付。
func (o *Order) IsPending() bool { return o.Status == OrderStatusPending }

// IsPaid 判断是否已支付。
func (o *Order) IsPaid() bool { return o.Status == OrderStatusPaid }

// CanCancel 判断是否可取消。
func (o *Order) CanCancel() bool { return o.Status == OrderStatusPending }

// PullEvents 返回本次行为累积的领域事件,并清空内部列表。
// 应用层在调用 repository.Update 后通过此方法拿事件交给 EventPublisher。
func (o *Order) PullEvents() []event.DomainEvent {
	events := o.events
	o.events = nil
	return events
}

func (o *Order) recordEvent(e event.DomainEvent) {
	o.events = append(o.events, e)
}

func generateOrderNo() string {
	return fmt.Sprintf("ORD%d", time.Now().UnixNano())
}
