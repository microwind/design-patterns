package order

import (
	"context"
	"errors"
	"fmt"
	"gin-ddd/internal/application/dto/order"
	"gin-ddd/internal/domain/event"
	orderModel "gin-ddd/internal/domain/model/order"
	orderDomain "gin-ddd/internal/domain/repository/order"
	userDomain "gin-ddd/internal/domain/repository/user"
	"gin-ddd/pkg/utils"
	"time"
)

// OrderService 订单应用服务
type OrderService struct {
	orderRepo      orderDomain.OrderRepository
	userRepo       userDomain.UserRepository
	eventPublisher event.EventPublisher
}

// NewOrderService 创建订单应用服务
func NewOrderService(orderRepo orderDomain.OrderRepository, eventPublisher event.EventPublisher) *OrderService {
	return &OrderService{
		orderRepo:      orderRepo,
		eventPublisher: eventPublisher,
	}
}

// NewOrderServiceWithUserRepo 创建订单应用服务（包含用户仓储）
func NewOrderServiceWithUserRepo(orderRepo orderDomain.OrderRepository, userRepo userDomain.UserRepository, eventPublisher event.EventPublisher) *OrderService {
	return &OrderService{
		orderRepo:      orderRepo,
		userRepo:       userRepo,
		eventPublisher: eventPublisher,
	}
}

// CreateOrder 创建订单
func (s *OrderService) CreateOrder(ctx context.Context, userID int64, totalAmount float64) (*order.OrderDTO, error) {
	if userID <= 0 {
		return nil, errors.New("用户ID无效")
	}
	if totalAmount <= 0 {
		return nil, errors.New("订单金额无效")
	}

	// 生成订单号
	orderNo := s.generateOrderNo()
	utils.GetLogger().Info("[OrderService] 开始创建订单: orderNo=%s, userId=%d, amount=%.2f", orderNo, userID, totalAmount)

	// 创建订单实体
	newOrder, err := orderModel.NewOrder(orderNo, userID, totalAmount)
	if err != nil {
		utils.GetLogger().Error("[OrderService] 创建订单实体失败: %v", err)
		return nil, err
	}

	// 持久化订单
	utils.GetLogger().Info("[OrderService] 持久化订单到数据库...")
	if err := s.orderRepo.Create(ctx, newOrder); err != nil {
		utils.GetLogger().Error("[OrderService] 订单入库失败: %v", err)
		return nil, err
	}
	utils.GetLogger().Info("[OrderService] 订单入库成功: orderId=%d", newOrder.OrderID)

	// 发布订单创建事件
	if s.eventPublisher != nil {
		utils.GetLogger().Info("[OrderService] 开始发送订单事件到MQ...")
		userEmail, userName := s.getUserInfo(ctx, userID)
		utils.GetLogger().Info("[OrderService] 获取用户信息: email=%s, name=%s", userEmail, userName)

		orderEvent := event.NewOrderCreatedEvent(newOrder.OrderID, newOrder.OrderNo, newOrder.UserID, userEmail, userName, newOrder.TotalAmount)
		utils.GetLogger().Info("[OrderService] 创建订单事件: type=%s", orderEvent.EventType())

		if err := s.eventPublisher.Publish(ctx, "order-event-topic", orderEvent); err != nil {
			// 事件发布失败不影响主流程，记录日志即可
			utils.GetLogger().Error("[OrderService] 发送订单事件失败: %v (不影响订单创建)", err)
		} else {
			utils.GetLogger().Info("[OrderService] 订单事件发送到MQ成功")
		}
	} else {
		utils.GetLogger().Info("[OrderService] 事件发布器未初始化，跳过事件发送")
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
		userEmail, userName := s.getUserInfo(ctx, o.UserID)
		orderEvent := event.NewOrderPaidEvent(o.OrderID, o.OrderNo, o.UserID, userEmail, userName, o.TotalAmount)
		if err := s.eventPublisher.Publish(ctx, "order-event-topic", orderEvent); err != nil {
			utils.GetLogger().Error("发布订单支付事件失败: %v", err)
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
		userEmail, userName := s.getUserInfo(ctx, o.UserID)
		orderEvent := event.NewOrderCancelledEvent(o.OrderID, o.OrderNo, o.UserID, userEmail, userName)
		if err := s.eventPublisher.Publish(ctx, "order-event-topic", orderEvent); err != nil {
			utils.GetLogger().Error("发布订单取消事件失败: %v", err)
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

// getUserInfo 获取用户信息
func (s *OrderService) getUserInfo(ctx context.Context, userID int64) (email, name string) {
	// 如果没有注入 UserRepository，返回空值
	if s.userRepo == nil {
		return "", ""
	}

	// 查询用户信息
	user, err := s.userRepo.FindByID(ctx, userID)
	if err != nil {
		utils.GetLogger().Error("查询用户信息失败: %v", err)
		return "", ""
	}

	if user == nil {
		return "", ""
	}

	return user.Email, user.Name
}

