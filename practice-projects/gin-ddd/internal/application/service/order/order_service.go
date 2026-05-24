// Package order 订单应用服务。仅做用例编排与事件发布,业务规则全部下沉到聚合根。
package order

import (
	"context"

	orderDTO "gin-ddd/internal/application/dto/order"
	domainErrors "gin-ddd/internal/domain/errors"
	"gin-ddd/internal/domain/event"
	orderModel "gin-ddd/internal/domain/model/order"
	orderDomain "gin-ddd/internal/domain/repository/order"
	"gin-ddd/pkg/utils"
)

// OrderService 订单应用服务。
type OrderService struct {
	orderRepo      orderDomain.OrderRepository
	eventPublisher event.EventPublisher
	// orderTopic 用于发布订单领域事件的 MQ 主题,从配置传入,避免硬编码。
	orderTopic string
}

// NewOrderService 构造订单应用服务。orderTopic 为发布订单事件的主题。
func NewOrderService(orderRepo orderDomain.OrderRepository, eventPublisher event.EventPublisher, orderTopic string) *OrderService {
	return &OrderService{
		orderRepo:      orderRepo,
		eventPublisher: eventPublisher,
		orderTopic:     orderTopic,
	}
}

// CreateOrder 创建订单。
//
// 参数合法性校验、订单号生成、初始状态、创建事件全部在 orderModel.New +
// orderRepo.Create + Order.MarkCreated 内闭环完成,本方法仅负责编排。
func (s *OrderService) CreateOrder(ctx context.Context, userID int64, totalAmount float64) (*orderDTO.OrderDTO, error) {
	utils.GetLogger().Info("[OrderService] 创建订单: userId=%d, amount=%.2f", userID, totalAmount)

	newOrder, err := orderModel.New(userID, totalAmount)
	if err != nil {
		utils.GetLogger().Error("[OrderService] 创建订单实体失败: %v", err)
		return nil, err
	}

	if err := s.orderRepo.Create(ctx, newOrder); err != nil {
		utils.GetLogger().Error("[OrderService] 订单入库失败: %v", err)
		return nil, err
	}

	s.publishEvents(ctx, newOrder.PullEvents())
	utils.GetLogger().Info("[OrderService] 订单创建成功: orderId=%d, orderNo=%s", newOrder.OrderID, newOrder.OrderNo)
	return orderDTO.ToDTO(newOrder), nil
}

// GetOrderByID 根据 ID 获取订单。
func (s *OrderService) GetOrderByID(ctx context.Context, id int64) (*orderDTO.OrderDTO, error) {
	o, err := s.orderRepo.FindByID(ctx, id)
	if err != nil {
		return nil, err
	}
	if o == nil {
		return nil, domainErrors.NewNotFound("订单", "id", id)
	}
	return orderDTO.ToDTO(o), nil
}

// GetOrderByOrderNo 根据订单号获取订单。
func (s *OrderService) GetOrderByOrderNo(ctx context.Context, orderNo string) (*orderDTO.OrderDTO, error) {
	o, err := s.orderRepo.FindByOrderNo(ctx, orderNo)
	if err != nil {
		return nil, err
	}
	if o == nil {
		return nil, domainErrors.NewNotFound("订单", "orderNo", orderNo)
	}
	return orderDTO.ToDTO(o), nil
}

// GetUserOrders 获取用户的所有订单。
func (s *OrderService) GetUserOrders(ctx context.Context, userID int64) ([]*orderDTO.OrderDTO, error) {
	orders, err := s.orderRepo.FindByUserID(ctx, userID)
	if err != nil {
		return nil, err
	}
	return orderDTO.ToDTOs(orders), nil
}

// GetAllOrders 获取所有订单。
func (s *OrderService) GetAllOrders(ctx context.Context) ([]*orderDTO.OrderDTO, error) {
	orders, err := s.orderRepo.FindAll(ctx)
	if err != nil {
		return nil, err
	}
	return orderDTO.ToDTOs(orders), nil
}

// PayOrder 支付订单。
func (s *OrderService) PayOrder(ctx context.Context, id int64) error {
	return s.mutate(ctx, id, (*orderModel.Order).Pay)
}

// ShipOrder 发货。
func (s *OrderService) ShipOrder(ctx context.Context, id int64) error {
	return s.mutate(ctx, id, (*orderModel.Order).Ship)
}

// DeliverOrder 确认送达。
func (s *OrderService) DeliverOrder(ctx context.Context, id int64) error {
	return s.mutate(ctx, id, (*orderModel.Order).Deliver)
}

// CancelOrder 取消订单。
func (s *OrderService) CancelOrder(ctx context.Context, id int64) error {
	return s.mutate(ctx, id, (*orderModel.Order).Cancel)
}

// RefundOrder 退款。
func (s *OrderService) RefundOrder(ctx context.Context, id int64) error {
	return s.mutate(ctx, id, (*orderModel.Order).Refund)
}

// mutate 统一状态迁移用例:加载 → 调用聚合根行为 → 保存 → 发布事件。
func (s *OrderService) mutate(ctx context.Context, id int64, action func(*orderModel.Order) error) error {
	o, err := s.orderRepo.FindByID(ctx, id)
	if err != nil {
		return err
	}
	if o == nil {
		return domainErrors.NewNotFound("订单", "id", id)
	}
	if err := action(o); err != nil {
		return err
	}
	if err := s.orderRepo.Update(ctx, o); err != nil {
		return err
	}
	s.publishEvents(ctx, o.PullEvents())
	return nil
}

// publishEvents 批量发布领域事件,任一失败仅记日志,不阻断主业务流程。
func (s *OrderService) publishEvents(ctx context.Context, events []event.DomainEvent) {
	for _, e := range events {
		if err := s.eventPublisher.Publish(ctx, s.orderTopic, e); err != nil {
			utils.GetLogger().Error("[OrderService] 发布领域事件失败: type=%s, err=%v", e.EventType(), err)
		}
	}
}
