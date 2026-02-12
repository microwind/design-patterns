package order

import (
	"context"
	"database/sql"
	"errors"
	"gin-ddd/internal/domain/model/order"
	"gin-ddd/pkg/utils"

	"github.com/jmoiron/sqlx"
)

// OrderRepositoryImpl 订单仓储实现
type OrderRepositoryImpl struct {
	db *sqlx.DB
}

// NewOrderRepository 创建订单仓储实例
func NewOrderRepository(db *sqlx.DB) *OrderRepositoryImpl {
	return &OrderRepositoryImpl{
		db: db,
	}
}

// Create 创建订单
func (r *OrderRepositoryImpl) Create(ctx context.Context, o *order.Order) error {
	query := `
		INSERT INTO orders (order_no, user_id, total_amount, status, created_at, updated_at)
		VALUES ($1, $2, $3, $4, $5, $6)
		RETURNING id
	`
	utils.GetLogger().Debug("执行INSERT订单SQL: orderNo=%s, userId=%d, amount=%.2f", o.OrderNo, o.UserID, o.TotalAmount)
	if err := r.db.QueryRowContext(ctx, query,
		o.OrderNo, o.UserID, o.TotalAmount, o.Status, o.CreatedAt, o.UpdatedAt).Scan(&o.OrderID); err != nil {
		utils.GetLogger().Error("插入订单失败: %v, orderNo=%s", err, o.OrderNo)
		return err
	}
	utils.GetLogger().Debug("订单插入成功: orderId=%d, orderNo=%s", o.OrderID, o.OrderNo)
	return nil
}

// Update 更新订单
func (r *OrderRepositoryImpl) Update(ctx context.Context, o *order.Order) error {
	query := `
		UPDATE orders
		SET order_no = $1, user_id = $2, total_amount = $3, status = $4, updated_at = $5
		WHERE id = $6
	`
	utils.GetLogger().Debug("执行UPDATE订单SQL: orderId=%d, status=%s", o.OrderID, o.Status)
	result, err := r.db.ExecContext(ctx, query,
		o.OrderNo, o.UserID, o.TotalAmount, o.Status, o.UpdatedAt, o.OrderID)
	if err != nil {
		utils.GetLogger().Error("更新订单失败: %v, orderId=%d", err, o.OrderID)
		return err
	}

	rowsAffected, err := result.RowsAffected()
	if err != nil {
		utils.GetLogger().Error("获取更新行数失败: %v", err)
		return err
	}
	utils.GetLogger().Debug("订单更新成功: orderId=%d, rowsAffected=%d", o.OrderID, rowsAffected)
	return nil
}

// Delete 删除订单
func (r *OrderRepositoryImpl) Delete(ctx context.Context, id int64) error {
	query := `DELETE FROM orders WHERE id = $1`
	utils.GetLogger().Debug("执行DELETE订单SQL: orderId=%d", id)
	result, err := r.db.ExecContext(ctx, query, id)
	if err != nil {
		utils.GetLogger().Error("删除订单失败: %v, orderId=%d", err, id)
		return err
	}

	rowsAffected, err := result.RowsAffected()
	if err != nil {
		utils.GetLogger().Error("获取删除行数失败: %v", err)
		return err
	}
	utils.GetLogger().Debug("订单删除成功: orderId=%d, rowsAffected=%d", id, rowsAffected)
	return nil
}

// FindByID 根据ID查询订单
func (r *OrderRepositoryImpl) FindByID(ctx context.Context, id int64) (*order.Order, error) {
	query := `
		SELECT id, order_no, user_id, total_amount, status, created_at, updated_at
		FROM orders WHERE id = $1
	`
	utils.GetLogger().Debug("执行SELECT订单SQL by ID: orderId=%d", id)
	var o order.Order
	err := r.db.GetContext(ctx, &o, query, id)
	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			utils.GetLogger().Debug("查询订单: 订单不存在, orderId=%d", id)
			return nil, nil
		}
		utils.GetLogger().Error("查询订单失败: %v, orderId=%d", err, id)
		return nil, err
	}

	utils.GetLogger().Debug("订单查询成功: orderId=%d, orderNo=%s", o.OrderID, o.OrderNo)
	return &o, nil
}

// FindByOrderNo 根据订单号查询订单
func (r *OrderRepositoryImpl) FindByOrderNo(ctx context.Context, orderNo string) (*order.Order, error) {
	query := `
		SELECT id, order_no, user_id, total_amount, status, created_at, updated_at
		FROM orders WHERE order_no = $1
	`
	utils.GetLogger().Debug("执行SELECT订单SQL by OrderNo: orderNo=%s", orderNo)
	var o order.Order
	err := r.db.GetContext(ctx, &o, query, orderNo)
	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			utils.GetLogger().Debug("查询订单: 订单不存在, orderNo=%s", orderNo)
			return nil, nil
		}
		utils.GetLogger().Error("查询订单失败: %v, orderNo=%s", err, orderNo)
		return nil, err
	}

	utils.GetLogger().Debug("订单查询成功: orderNo=%s, orderId=%d", orderNo, o.OrderID)
	return &o, nil
}

// FindByUserID 根据用户ID查询订单列表
func (r *OrderRepositoryImpl) FindByUserID(ctx context.Context, userID int64) ([]*order.Order, error) {
	query := `
		SELECT id, order_no, user_id, total_amount, status, created_at, updated_at
		FROM orders WHERE user_id = $1 ORDER BY created_at DESC
	`
	utils.GetLogger().Debug("执行SELECT订单SQL by UserID: userId=%d", userID)
	var orders []*order.Order
	err := r.db.SelectContext(ctx, &orders, query, userID)
	if err != nil {
		utils.GetLogger().Error("查询用户订单失败: %v, userId=%d", err, userID)
		return nil, err
	}

	utils.GetLogger().Debug("用户订单查询成功: userId=%d, 共%d条记录", userID, len(orders))
	return orders, nil
}

// FindAll 查询所有订单
func (r *OrderRepositoryImpl) FindAll(ctx context.Context) ([]*order.Order, error) {
	query := `
		SELECT id, order_no, user_id, total_amount, status, created_at, updated_at
		FROM orders ORDER BY created_at DESC
	`
	utils.GetLogger().Debug("执行SELECT所有订单SQL")
	var orders []*order.Order
	err := r.db.SelectContext(ctx, &orders, query)
	if err != nil {
		utils.GetLogger().Error("查询所有订单失败: %v", err)
		return nil, err
	}

	utils.GetLogger().Debug("所有订单查询成功: 共%d条记录", len(orders))
	return orders, nil
}

// FindByStatus 根据状态查询订单列表
func (r *OrderRepositoryImpl) FindByStatus(ctx context.Context, status order.OrderStatus) ([]*order.Order, error) {
	query := `
		SELECT id, order_no, user_id, total_amount, status, created_at, updated_at
		FROM orders WHERE status = $1 ORDER BY created_at DESC
	`
	utils.GetLogger().Debug("执行SELECT订单SQL by Status: status=%s", status)
	var orders []*order.Order
	err := r.db.SelectContext(ctx, &orders, query, status)
	if err != nil {
		utils.GetLogger().Error("查询订单失败: %v, status=%s", err, status)
		return nil, err
	}

	utils.GetLogger().Debug("订单查询成功: status=%s, 共%d条记录", status, len(orders))
	return orders, nil
}

