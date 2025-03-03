// 应用层（协调领域逻辑，处理业务用例）：订单应用服务
package services

import (
  "fmt"
  "go-web-order/internal/domain/order"
  "math/rand"
  "sync"
  "time"
)

var (
  rng    = rand.New(rand.NewSource(time.Now().UnixNano()))
  rngMux sync.Mutex
)

// GenerateOrderID 生成时间戳 + 随机数的唯一订单号 (int)
func GenerateOrderID() int64 {
  rngMux.Lock()
  defer rngMux.Unlock()

  timestamp := time.Now().UnixMilli() // 毫秒时间戳
  random := rng.Intn(1000)            // 0-999 的随机数
  return int64(timestamp*1000) + int64(random)
}

// OrderService 订单应用服务，协调领域逻辑和业务用例
type OrderService struct {
  OrderRepository order.OrderRepository // 使用订单仓储接口
  // Repository order.OrderRepository // 使用通用接口声明
}

// 创建OrderService实例对象
func NewOrderService(orderRepo order.OrderRepository) *OrderService {
  return &OrderService{orderRepo}
}

// CreateOrder 创建订单并保存到仓储中
func (s *OrderService) CreateOrder(customerName string, amount float64) (*order.Order, error) {
  // 自动生成订单 ID ，实际应用中会采用分布式ID或者采用数据库自增键
  orderID := GenerateOrderID()
  newOrder := order.NewOrder(orderID, customerName, amount)
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
func (s *OrderService) CancelOrder(id int64) error {
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
func (s *OrderService) GetOrder(id int64) (*order.Order, error) {
  return s.OrderRepository.FindByID(id)
}

// GetAllOrders 列出全部订单，此处省略分页
func (s *OrderService) GetAllOrders(userId int) ([]*order.Order, error) {
  return s.OrderRepository.FindAll(userId)
}

// UpdateOrder 更新订单的客户信息和金额
func (s *OrderService) UpdateOrder(id int64, customerName string, amount float64) (*order.Order, error) {
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
func (s *OrderService) DeleteOrder(id int64) error {
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
