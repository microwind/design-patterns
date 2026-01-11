package order

import (
	"context"
	"errors"
	"fmt"
	"gin-ddd/internal/application/dto/order"
	"gin-ddd/internal/domain/event"
	orderModel "gin-ddd/internal/domain/model/order"
	"gin-ddd/internal/domain/repository/order"
	"time"
)

// OrderService 订单应用服务
type OrderService struct {
	orderRepo      order.OrderRepository
	eventPublisher event.EventPublisher
}

// NewOrderService 创建订单应用服务
func NewOrderService(orderRepo order.OrderRepository, eventPublisher event.EventPublisher) *OrderService {
	return &OrderService{
		orderRepo:      orderRepo,
		eventPublisher: eventPublisher,
	}
}

// CreateOrder 创建订单
func (s *OrderService) CreateOrder(ctx context.Context, userID int64, items []orderModel.OrderItem) (*order.OrderDTO, error) {
	if userID <= 0 {
		return nil, errors.New("用户ID无效")
	}
	if len(items) == 0 {
		return nil, errors.New("订单项不能为空")
	}

	// 生成订单号
	orderNo := s.generateOrderNo()

	// 创建订单实体
	newOrder, err := orderModel.NewOrder(orderNo, userID, items)
	if err != nil {
		return nil, err
	}

	// 持久化订单
	if err := s.orderRepo.Create(ctx, newOrder); err != nil {
		return nil, err
	}

	// 发布订单创建事件
	if s.eventPublisher != nil {
		orderEvent := event.NewOrderCreatedEvent(newOrder.ID, newOrder.OrderNo, newOrder.UserID, newOrder.TotalAmount)
		if err := s.eventPublisher.Publish(ctx, "order-event-topic", orderEvent); err != nil {
			// 事件发布失败不影响主流程，记录日志即可
			fmt.Printf("发布订单创建事件失败: %v\n", err)
		}
	}

	return order.ToDTO(newOrder), nil
}

// GetOrderByID 根据ID获取订单
func (s *OrderService) GetOrderByID(ctx context.Context, id int64) (*order.OrderDTO, error) {
	o, err := s.orderRepo.FindByID(ctx, id)
	if err != nil {
		return nil, err
	}
	if o == nil {
		return nil, errors.New("订单不存在")
	}
	return order.ToDTO(o), nil
}

// GetOrderByOrderNo 根据订单号获取订单
func (s *OrderService) GetOrderByOrderNo(ctx context.Context, orderNo string) (*order.OrderDTO, error) {
	o, err := s.orderRepo.FindByOrderNo(ctx, orderNo)
	if err != nil {
		return nil, err
	}
	if o == nil {
		return nil, errors.New("订单不存在")
	}
	return order.ToDTO(o), nil
}

// GetUserOrders 获取用户的所有订单
func (s *OrderService) GetUserOrders(ctx context.Context, userID int64) ([]*order.OrderDTO, error) {
	orders, err := s.orderRepo.FindByUserID(ctx, userID)
	if err != nil {
		return nil, err
	}
	return order.ToDTOs(orders), nil
}

// GetAllOrders 获取所有订单
func (s *OrderService) GetAllOrders(ctx context.Context) ([]*order.OrderDTO, error) {
	orders, err := s.orderRepo.FindAll(ctx)
	if err != nil {
		return nil, err
	}
	return order.ToDTOs(orders), nil
}

// PayOrder 支付订单
func (s *OrderService) PayOrder(ctx context.Context, id int64) error {
	o, err := s.orderRepo.FindByID(ctx, id)
	if err != nil {
		return err
	}
	if o == nil {
		return errors.New("订单不存在")
	}

	if err := o.Pay(); err != nil {
		return err
	}

	if err := s.orderRepo.Update(ctx, o); err != nil {
		return err
	}

	// 发布订单支付事件
	if s.eventPublisher != nil {
		orderEvent := event.NewOrderPaidEvent(o.ID, o.OrderNo, o.UserID, o.TotalAmount)
		if err := s.eventPublisher.Publish(ctx, "order-event-topic", orderEvent); err != nil {
			fmt.Printf("发布订单支付事件失败: %v\n", err)
		}
	}

	return nil
}

// ShipOrder 发货
func (s *OrderService) ShipOrder(ctx context.Context, id int64) error {
	o, err := s.orderRepo.FindByID(ctx, id)
	if err != nil {
		return err
	}
	if o == nil {
		return errors.New("订单不存在")
	}

	if err := o.Ship(); err != nil {
		return err
	}

	return s.orderRepo.Update(ctx, o)
}

// DeliverOrder 确认送达
func (s *OrderService) DeliverOrder(ctx context.Context, id int64) error {
	o, err := s.orderRepo.FindByID(ctx, id)
	if err != nil {
		return err
	}
	if o == nil {
		return errors.New("订单不存在")
	}

	if err := o.Deliver(); err != nil {
		return err
	}

	return s.orderRepo.Update(ctx, o)
}

// CancelOrder 取消订单
func (s *OrderService) CancelOrder(ctx context.Context, id int64) error {
	o, err := s.orderRepo.FindByID(ctx, id)
	if err != nil {
		return err
	}
	if o == nil {
		return errors.New("订单不存在")
	}

	if err := o.Cancel(); err != nil {
		return err
	}

	if err := s.orderRepo.Update(ctx, o); err != nil {
		return err
	}

	// 发布订单取消事件
	if s.eventPublisher != nil {
		orderEvent := event.NewOrderCancelledEvent(o.ID, o.OrderNo, o.UserID)
		if err := s.eventPublisher.Publish(ctx, "order-event-topic", orderEvent); err != nil {
			fmt.Printf("发布订单取消事件失败: %v\n", err)
		}
	}

	return nil
}

// RefundOrder 退款
func (s *OrderService) RefundOrder(ctx context.Context, id int64) error {
	o, err := s.orderRepo.FindByID(ctx, id)
	if err != nil {
		return err
	}
	if o == nil {
		return errors.New("订单不存在")
	}

	if err := o.Refund(); err != nil {
		return err
	}

	return s.orderRepo.Update(ctx, o)
}

// generateOrderNo 生成订单号
func (s *OrderService) generateOrderNo() string {
	// 简单实现：时间戳 + 随机数
	// 生产环境建议使用更复杂的算法（如雪花算法）
	return fmt.Sprintf("ORD%d", time.Now().UnixNano())
}
