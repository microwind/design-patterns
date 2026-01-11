package order

import (
	"context"
	"gin-ddd/internal/domain/model/order"
)

// OrderRepository 订单仓储接口
type OrderRepository interface {
	// Create 创建订单
	Create(ctx context.Context, order *order.Order) error

	// Update 更新订单
	Update(ctx context.Context, order *order.Order) error

	// Delete 删除订单
	Delete(ctx context.Context, id int64) error

	// FindByID 根据ID查询订单
	FindByID(ctx context.Context, id int64) (*order.Order, error)

	// FindByOrderNo 根据订单号查询订单
	FindByOrderNo(ctx context.Context, orderNo string) (*order.Order, error)

	// FindByUserID 根据用户ID查询订单列表
	FindByUserID(ctx context.Context, userID int64) ([]*order.Order, error)

	// FindAll 查询所有订单
	FindAll(ctx context.Context) ([]*order.Order, error)

	// FindByStatus 根据状态查询订单列表
	FindByStatus(ctx context.Context, status order.OrderStatus) ([]*order.Order, error)
}
