// 应用层（协调领域逻辑，处理业务用例）：订单应用服务
package services

import (
	"fmt"
	"go-web-order/internal/domain/order"
)

// OrderService 订单应用服务，协调领域逻辑和业务用例
type OrderService struct {
  OrderRepository order.OrderRepository // 订单仓储接口
}

// 创建OrderService实例对象
func NewOrderService(orderRepo order.OrderRepository) *OrderService {
  return &OrderService{orderRepo}
}

// CreateOrder 创建订单并保存到仓储中
func (s *OrderService) CreateOrder(id int, customerName string, amount float64) (*order.Order, error) {
  // 创建订单
  newOrder := order.NewOrder(id, customerName, amount)
  if newOrder == nil {
    return nil, fmt.Errorf("订单创建失败")
  }

  // 保存订单
  err := s.OrderRepository.Save(newOrder)
  if err != nil {
    return nil, fmt.Errorf("订单保存失败: %v", err)
  }

  return newOrder, nil
}

// CancelOrder 取消订单
func (s *OrderService) CancelOrder(id int) error {
  order, err := s.OrderRepository.FindByID(id) // 获取订单
  if err != nil {
    return fmt.Errorf("订单取消失败：%v", err)
  }

  order.Cancel()                      // 执行领域逻辑：取消订单
  err = s.OrderRepository.Save(order) // 保存更新后的订单
  if err != nil {
    return fmt.Errorf("订单取消失败：%v", err)
  }
  return nil
}

// GetOrder 查询订单
func (s *OrderService) GetOrder(id int) (*order.Order, error) {
  return s.OrderRepository.FindByID(id)
}

// UpdateOrder 更新订单的客户信息和金额
func (s *OrderService) UpdateOrder(id int, customerName string, amount float64) (*order.Order, error) {
  // 获取订单
  order, err := s.OrderRepository.FindByID(id)
  if err != nil {
    return nil, fmt.Errorf("订单未找到: %v", err)
  }

  // 更新订单的客户信息和金额
  order.UpdateCustomerInfo(customerName)
  order.UpdateAmount(amount)

  // 保存更新后的订单
  err = s.OrderRepository.Save(order)
  if err != nil {
    return nil, fmt.Errorf("更新订单失败: %v", err)
  }

  return order, nil
}

// DeleteOrder 删除订单
func (s *OrderService) DeleteOrder(id int) error {
  // 获取订单
  order, err := s.OrderRepository.FindByID(id)
  if err != nil {
    return fmt.Errorf("订单未找到: %v", err)
  }
  // 从仓储中删除订单
  err = s.OrderRepository.Delete(order.ID)
  if err != nil {
    return fmt.Errorf("删除订单失败: %v", err)
  }

  return nil
}
