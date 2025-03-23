// services/order_service.go
package services

import (
  "errors"
  "fmt"
  models "gin-order/internal/models/order"
  repository "gin-order/internal/repository/order"

  "github.com/shopspring/decimal"
)

// OrderService 订单服务结构
type OrderService struct {
  repo repository.OrderRepository
}

// NewOrderService 创建订单服务实例
func NewOrderService(repo repository.OrderRepository) *OrderService {
  return &OrderService{repo: repo}
}

// CreateOrder 创建订单
func (s *OrderService) CreateOrder(o models.Order) (*models.Order, error) {
  zero := decimal.NewFromInt(0)
  if o.OrderName == "" || o.Amount.LessThanOrEqual(zero) || o.UserID == 0 {
    return nil, errors.New("无效的订单数据")
  }
  fmt.Println("CreateOrder:", o)
  return s.repo.CreateOrder(o)
}

// GetByOrderNo 通过ID获取订单
func (s *OrderService) GetByOrderNo(orderNo string) (*models.Order, error) {
  return s.repo.GetByOrderNo(orderNo)
}

// GetAllOrders 获取所有订单
func (s *OrderService) GetAllOrders() []models.Order {
  return s.repo.GetAllOrders()
}

// GetOrdersByUserID 获取用户订单
func (s *OrderService) GetOrdersByUserID(userId string) []models.Order {
  return s.repo.GetOrdersByUserID(userId)
}

// UpdateOrder 更新订单
func (s *OrderService) UpdateOrder(o models.Order) (*models.Order, error) {
  if o.OrderNo == "" {
    return nil, errors.New("订单ID是必需的")
  }
  return s.repo.UpdateOrder(o)
}

// UpdateOrderStatus 修改订单状态
func (s *OrderService) UpdateOrderStatus(orderNo, status string) (*models.Order, error) {
  return s.repo.UpdateOrderStatus(orderNo, status)
}

// DeleteOrder 删除订单
func (s *OrderService) DeleteOrder(orderNo string) error {
  return s.repo.DeleteOrder(orderNo)
}
