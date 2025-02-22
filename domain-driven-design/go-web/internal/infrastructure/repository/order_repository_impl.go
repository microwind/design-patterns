// 基础设施层（Infrastructure）：订单仓储实现
package repository

import (
	"fmt"
	"go-web-order/internal/domain/order"
)

// OrderRepositoryImpl 订单仓储实现，具体存储操作
type OrderRepositoryImpl struct {
  orders map[int]*order.Order // 存储订单的内存映射
}

// NewOrderRepositoryImpl 创建新的订单仓储实现
func NewOrderRepositoryImpl() *OrderRepositoryImpl {
  return &OrderRepositoryImpl{orders: make(map[int]*order.Order)}
}

// Save 保存订单
func (r *OrderRepositoryImpl) Save(order *order.Order) error {
  r.orders[order.ID] = order // 存储订单到内存中
  return nil
}

// FindByID 根据ID查找订单
func (r *OrderRepositoryImpl) FindByID(id int) (*order.Order, error) {
  order, exists := r.orders[id]
  if !exists {
    return nil, fmt.Errorf("订单 ID %d 不存在", id)
  }
  return order, nil
}

// FindAll 查找所有订单
func (r *OrderRepositoryImpl) FindAll() ([]*order.Order, error) {
  orders := []*order.Order{}
  for _, order := range r.orders {
    orders = append(orders, order)
  }
  return orders, nil
}

// Delete 删除订单
func (r *OrderRepositoryImpl) Delete(id int) error {
  if _, exists := r.orders[id]; !exists {
    return fmt.Errorf("订单 ID %d 不存在，无法删除", id)
  }
  delete(r.orders, id)
  return nil
}

// FindByCustomerName 根据客户名称查找订单
func (r *OrderRepositoryImpl) FindByCustomerName(customerName string) ([]*order.Order, error) {
  var result []*order.Order
  for _, order := range r.orders {
    if order.CustomerName == customerName {
      result = append(result, order)
    }
  }
  if len(result) == 0 {
    return nil, fmt.Errorf("没有找到客户名称为 %s 的订单", customerName)
  }
  return result, nil
}
