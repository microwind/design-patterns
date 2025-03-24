// repository/order_gorm_repository.go
// order_mysql_repository与order_mysql_repository二选一即可
package repository

import (
  "errors"
  "fmt"
  models "gin-order/internal/models/order"

  "gorm.io/gorm"
)

// orderGormRepository 结构体 实现 OrderRepository接口
type orderGormRepository struct {
  db *gorm.DB
}

// NewOrderGormRepository 构造函数
func NewOrderGormRepository(db *gorm.DB) OrderRepository {
  return &orderGormRepository{db: db}
}

// CreateOrder 创建订单
func (r *orderGormRepository) CreateOrder(newOrder models.Order) (*models.Order, error) {
  result := r.db.Create(&newOrder)
  if result.Error != nil {
    return nil, result.Error
  }
  return &newOrder, nil
}

// GetByOrderNo 通过ID获取订单
func (r *orderGormRepository) GetByOrderNo(orderNo string) (*models.Order, error) {
  var o models.Order
  result := r.db.Where("order_no = ?", orderNo).First(&o)
  if result.Error != nil {
    return nil, result.Error
  }
  return &o, nil
}

// GetAllOrders 获取所有订单（支持分页）
func (r *orderGormRepository) GetAllOrders(page, pageSize int) ([]models.Order, int, error) {
  var orders []models.Order

  // 计算 OFFSET 和 LIMIT
  offset := (page - 1) * pageSize

  // 获取分页数据
  if err := r.db.Limit(pageSize).Offset(offset).Find(&orders).Error; err != nil {
    return nil, 0, fmt.Errorf("GetAllOrders query error: %w", err)
  }

  // 获取总记录数
  var total int64
  if err := r.db.Model(&models.Order{}).Count(&total).Error; err != nil {
    return nil, 0, fmt.Errorf("GetAllOrders count query error: %w", err)
  }

  return orders, int(total), nil
}

// GetOrdersByUserID 根据用户ID获取订单（支持分页）
func (r *orderGormRepository) GetOrdersByUserID(userId string, page, pageSize int) ([]models.Order, int, error) {
  var orders []models.Order

  // 计算 OFFSET 和 LIMIT
  offset := (page - 1) * pageSize

  // 获取分页数据
  if err := r.db.Where("user_id = ?", userId).Limit(pageSize).Offset(offset).Find(&orders).Error; err != nil {
    return nil, 0, fmt.Errorf("GetOrdersByUserID query error: %w", err)
  }

  // 获取总记录数
  var total int64
  if err := r.db.Model(&models.Order{}).Where("user_id = ?", userId).Count(&total).Error; err != nil {
    return nil, 0, fmt.Errorf("GetOrdersByUserID count query error: %w", err)
  }

  return orders, int(total), nil
}

// UpdateOrder 更新订单
func (r *orderGormRepository) UpdateOrder(updatedOrder models.Order) (*models.Order, error) {
  result := r.db.Model(&models.Order{}).
    Where("order_no = ?", updatedOrder.OrderNo).
    Updates(updatedOrder) // 只更新非零值字段

  if result.Error != nil {
    return nil, result.Error
  }
  if result.RowsAffected == 0 {
    return nil, errors.New("order not found")
  }
  return &updatedOrder, nil
}

// UpdateOrderStatus 更新订单状态
func (r *orderGormRepository) UpdateOrderStatus(orderNo, status string) (*models.Order, error) {
  var o models.Order
  result := r.db.Model(&o).Where("order_no = ?", orderNo).Update("status", status)
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
func (r *orderGormRepository) DeleteOrder(orderNo string) error {
  var o models.Order
  result := r.db.Where("order_no = ?", orderNo).Delete(&o)
  return result.Error
}
