package services

import (
  "errors"
  "fmt"
  models "gin-order/internal/models/order"
  repository "gin-order/internal/repository/order"
  "strings"

  "github.com/google/uuid"
  "github.com/shopspring/decimal"
)

// GenerateOrderNo 使用 UUID 生成订单编号
func GenerateOrderNo() string {
  return "ORD-" + strings.ReplaceAll(uuid.New().String(), "-", "")[:16]
}

// OrderService 定义订单服务接口[可选]
type OrderService interface {
  CreateOrder(o models.Order) (*models.Order, error)
  GetByOrderNo(orderNo string) (*models.Order, error)
  GetAllOrders(page, pageSize int) ([]models.Order, int, error)
  GetOrdersByUserID(userId string, page, pageSize int) ([]models.Order, int, error)
  UpdateOrder(o models.Order) (*models.Order, error)
  UpdateOrderStatus(orderNo, status string) (*models.Order, error)
  DeleteOrder(orderNo string) error
}

// OrderServiceImpl 订单服务实现 OrderService 接口
// 如不实现接口，也可以只定义struct，接口会更加利于扩展
type OrderServiceImpl struct {
  repo repository.OrderRepository
}

// NewOrderService 创建订单服务实例，并返回 OrderServiceInterface 接口
func NewOrderServiceImpl(repo repository.OrderRepository) OrderService {
  return &OrderServiceImpl{repo: repo}
}

// CreateOrder 创建订单
func (s *OrderServiceImpl) CreateOrder(o models.Order) (*models.Order, error) {
  zero := decimal.NewFromInt(0)
  o.OrderNo = GenerateOrderNo()
  if o.OrderName == "" || o.Amount.LessThanOrEqual(zero) || o.UserID == 0 {
    return nil, errors.New("无效的订单数据")
  }
  fmt.Println("CreateOrder:", o)
  return s.repo.CreateOrder(o)
}

// GetByOrderNo 通过订单编号获取订单
func (s *OrderServiceImpl) GetByOrderNo(orderNo string) (*models.Order, error) {
  return s.repo.GetByOrderNo(orderNo)
}

// GetAllOrders 获取所有订单（支持分页）
func (s *OrderServiceImpl) GetAllOrders(page, pageSize int) ([]models.Order, int, error) {
  orders, total, err := s.repo.GetAllOrders(page, pageSize)
  if err != nil {
    return nil, 0, fmt.Errorf("GetAllOrders service error: %w", err)
  }
  return orders, total, nil
}

// GetOrdersByUserID 获取用户的订单（支持分页）
func (s *OrderServiceImpl) GetOrdersByUserID(userId string, page, pageSize int) ([]models.Order, int, error) {
  orders, total, err := s.repo.GetOrdersByUserID(userId, page, pageSize)
  if err != nil {
    return nil, 0, fmt.Errorf("GetOrdersByUserID service error: %w", err)
  }
  return orders, total, nil
}

// UpdateOrder 更新订单
func (s *OrderServiceImpl) UpdateOrder(o models.Order) (*models.Order, error) {
  if o.OrderNo == "" {
    return nil, errors.New("订单ID是必需的")
  }
  return s.repo.UpdateOrder(o)
}

// UpdateOrderStatus 修改订单状态
func (s *OrderServiceImpl) UpdateOrderStatus(orderNo, status string) (*models.Order, error) {
  return s.repo.UpdateOrderStatus(orderNo, status)
}

// DeleteOrder 删除订单
func (s *OrderServiceImpl) DeleteOrder(orderNo string) error {
  return s.repo.DeleteOrder(orderNo)
}
