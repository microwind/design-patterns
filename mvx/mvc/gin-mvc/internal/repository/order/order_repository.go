// repository/order_repository.go
package repository

import (
  models "gin-order/internal/models/order"

  "gorm.io/gorm"
)

// OrderRepository 接口定义
type OrderRepository interface {
  CreateOrder(newOrder models.Order) (*models.Order, error)
  GetByOrderNo(orderNo string) (*models.Order, error)
  GetAllOrders() []models.Order
  GetOrdersByUserID(userId string) []models.Order
  UpdateOrder(updatedOrder models.Order) (*models.Order, error)
  UpdateOrderStatus(orderNo, status string) (*models.Order, error)
  DeleteOrder(orderNo string) error
}

// orderRepository 实现结构体
type orderRepository struct {
  db *gorm.DB
}

// NewOrderRepository 构造函数
func NewOrderRepository(db *gorm.DB) OrderRepository {
  return &orderRepository{db: db}
}

// CreateOrder 创建订单
func (r *orderRepository) CreateOrder(newOrder models.Order) (*models.Order, error) {
  result := r.db.Create(&newOrder)
  if result.Error != nil {
    return nil, result.Error
  }
  return &newOrder, nil
}

// GetByOrderNo 通过ID获取订单
func (r *orderRepository) GetByOrderNo(orderNo string) (*models.Order, error) {
  var o models.Order
  result := r.db.Where("order_no = ?", orderNo).First(&o)
  if result.Error != nil {
    return nil, result.Error
  }
  return &o, nil
}

// GetAllOrders 获取所有订单
func (r *orderRepository) GetAllOrders() []models.Order {
  var orders []models.Order
  r.db.Find(&orders)
  return orders
}

// GetOrdersByUserID 获取用户订单
func (r *orderRepository) GetOrdersByUserID(userId string) []models.Order {
  var orders []models.Order
  r.db.Where("user_id = ?", userId).Find(&orders)
  return orders
}

// UpdateOrder 更新订单
func (r *orderRepository) UpdateOrder(updatedOrder models.Order) (*models.Order, error) {
  result := r.db.Save(&updatedOrder)
  if result.Error != nil {
    return nil, result.Error
  }
  return &updatedOrder, nil
}

// UpdateOrderStatus 更新订单状态
func (r *orderRepository) UpdateOrderStatus(orderNo, status string) (*models.Order, error) {
  var o models.Order
  result := r.db.Model(&o).Where("order_id = ?", orderNo).Update("status", status)
  if result.Error != nil {
    return nil, result.Error
  }
  if result.RowsAffected == 0 {
    return nil, gorm.ErrRecordNotFound
  }
  r.db.Where("order_no = ?", orderNo).First(&o)
  return &o, nil
}

// DeleteOrder 删除订单
func (r *orderRepository) DeleteOrder(orderNo string) error {
  var o models.Order
  result := r.db.Where("order_no = ?", orderNo).Delete(&o)
  return result.Error
}
