// repository/order_repository.go
package repository

import (
  models "gin-order/internal/models/order"
)

// OrderRepository 接口定义
type OrderRepository interface {
  CreateOrder(newOrder models.Order) (*models.Order, error)
  GetByOrderNo(orderNo string) (*models.Order, error)
  GetAllOrders(page int, pageSize int) ([]models.Order, int, error)
  GetOrdersByUserID(userId string, page, pageSize int) ([]models.Order, int, error)
  UpdateOrder(updatedOrder models.Order) (*models.Order, error)
  UpdateOrderStatus(orderNo, status string) (*models.Order, error)
  DeleteOrder(orderNo string) error
}
