// Package order 订单仓储实现。
//
// 仓储层只读写 OrderDO,通过 OrderConverter 与领域聚合根互转,
// 保证 *order.Order 不携带任何持久化 tag/依赖。
package order

import (
	"context"
	"database/sql"
	"errors"

	orderModel "gin-ddd/internal/domain/model/order"
	"gin-ddd/pkg/utils"

	"github.com/jmoiron/sqlx"
)

// OrderRepositoryImpl 订单仓储实现(基于 PostgreSQL)。
type OrderRepositoryImpl struct {
	db *sqlx.DB
}

// NewOrderRepository 构造仓储实例。
func NewOrderRepository(db *sqlx.DB) *OrderRepositoryImpl {
	return &OrderRepositoryImpl{db: db}
}

// Create 持久化新订单,回填主键后由 Order.MarkCreated 记录创建事件。
func (r *OrderRepositoryImpl) Create(ctx context.Context, o *orderModel.Order) error {
	do := toDO(o)
	query := `
		INSERT INTO orders (order_no, user_id, total_amount, status, created_at, updated_at)
		VALUES ($1, $2, $3, $4, $5, $6)
		RETURNING id
	`
	utils.GetLogger().Debug("执行INSERT订单SQL: orderNo=%s, userId=%d, amount=%.2f", do.OrderNo, do.UserID, do.TotalAmount)
	var newID int64
	if err := r.db.QueryRowContext(ctx, query,
		do.OrderNo, do.UserID, do.TotalAmount, do.Status, do.CreatedAt, do.UpdatedAt).Scan(&newID); err != nil {
		utils.GetLogger().Error("插入订单失败: %v, orderNo=%s", err, do.OrderNo)
		return err
	}
	if err := o.MarkCreated(newID); err != nil {
		utils.GetLogger().Error("订单初始化标记失败: %v, orderId=%d", err, newID)
		return err
	}
	utils.GetLogger().Debug("订单插入成功: orderId=%d, orderNo=%s", newID, do.OrderNo)
	return nil
}

// Update 更新订单。
func (r *OrderRepositoryImpl) Update(ctx context.Context, o *orderModel.Order) error {
	do := toDO(o)
	query := `
		UPDATE orders
		SET order_no = $1, user_id = $2, total_amount = $3, status = $4, updated_at = $5
		WHERE id = $6
	`
	utils.GetLogger().Debug("执行UPDATE订单SQL: orderId=%d, status=%s", do.ID, do.Status)
	result, err := r.db.ExecContext(ctx, query,
		do.OrderNo, do.UserID, do.TotalAmount, do.Status, do.UpdatedAt, do.ID)
	if err != nil {
		utils.GetLogger().Error("更新订单失败: %v, orderId=%d", err, do.ID)
		return err
	}
	rowsAffected, _ := result.RowsAffected()
	utils.GetLogger().Debug("订单更新成功: orderId=%d, rowsAffected=%d", do.ID, rowsAffected)
	return nil
}

// Delete 删除订单。
func (r *OrderRepositoryImpl) Delete(ctx context.Context, id int64) error {
	query := `DELETE FROM orders WHERE id = $1`
	utils.GetLogger().Debug("执行DELETE订单SQL: orderId=%d", id)
	result, err := r.db.ExecContext(ctx, query, id)
	if err != nil {
		utils.GetLogger().Error("删除订单失败: %v, orderId=%d", err, id)
		return err
	}
	rowsAffected, _ := result.RowsAffected()
	utils.GetLogger().Debug("订单删除成功: orderId=%d, rowsAffected=%d", id, rowsAffected)
	return nil
}

// FindByID 根据 ID 查询订单。
func (r *OrderRepositoryImpl) FindByID(ctx context.Context, id int64) (*orderModel.Order, error) {
	const query = `
		SELECT id, order_no, user_id, total_amount, status, created_at, updated_at
		FROM orders WHERE id = $1
	`
	utils.GetLogger().Debug("执行SELECT订单SQL by ID: orderId=%d", id)
	var do OrderDO
	err := r.db.GetContext(ctx, &do, query, id)
	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			return nil, nil
		}
		utils.GetLogger().Error("查询订单失败: %v, orderId=%d", err, id)
		return nil, err
	}
	utils.GetLogger().Debug("订单查询成功: orderId=%d, orderNo=%s", do.ID, do.OrderNo)
	return toModel(&do), nil
}

// FindByOrderNo 根据订单号查询。
func (r *OrderRepositoryImpl) FindByOrderNo(ctx context.Context, orderNo string) (*orderModel.Order, error) {
	const query = `
		SELECT id, order_no, user_id, total_amount, status, created_at, updated_at
		FROM orders WHERE order_no = $1
	`
	utils.GetLogger().Debug("执行SELECT订单SQL by OrderNo: orderNo=%s", orderNo)
	var do OrderDO
	err := r.db.GetContext(ctx, &do, query, orderNo)
	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			return nil, nil
		}
		utils.GetLogger().Error("查询订单失败: %v, orderNo=%s", err, orderNo)
		return nil, err
	}
	return toModel(&do), nil
}

// FindByUserID 根据用户 ID 查询订单列表。
func (r *OrderRepositoryImpl) FindByUserID(ctx context.Context, userID int64) ([]*orderModel.Order, error) {
	const query = `
		SELECT id, order_no, user_id, total_amount, status, created_at, updated_at
		FROM orders WHERE user_id = $1 ORDER BY created_at DESC
	`
	utils.GetLogger().Debug("执行SELECT订单SQL by UserID: userId=%d", userID)
	var dos []*OrderDO
	if err := r.db.SelectContext(ctx, &dos, query, userID); err != nil {
		utils.GetLogger().Error("查询用户订单失败: %v, userId=%d", err, userID)
		return nil, err
	}
	utils.GetLogger().Debug("用户订单查询成功: userId=%d, 共%d条记录", userID, len(dos))
	return toModels(dos), nil
}

// FindAll 查询所有订单。
func (r *OrderRepositoryImpl) FindAll(ctx context.Context) ([]*orderModel.Order, error) {
	const query = `
		SELECT id, order_no, user_id, total_amount, status, created_at, updated_at
		FROM orders ORDER BY created_at DESC
	`
	utils.GetLogger().Debug("执行SELECT所有订单SQL")
	var dos []*OrderDO
	if err := r.db.SelectContext(ctx, &dos, query); err != nil {
		utils.GetLogger().Error("查询所有订单失败: %v", err)
		return nil, err
	}
	return toModels(dos), nil
}

// FindByStatus 根据状态查询订单列表。
func (r *OrderRepositoryImpl) FindByStatus(ctx context.Context, status orderModel.OrderStatus) ([]*orderModel.Order, error) {
	const query = `
		SELECT id, order_no, user_id, total_amount, status, created_at, updated_at
		FROM orders WHERE status = $1 ORDER BY created_at DESC
	`
	utils.GetLogger().Debug("执行SELECT订单SQL by Status: status=%s", status)
	var dos []*OrderDO
	if err := r.db.SelectContext(ctx, &dos, query, string(status)); err != nil {
		utils.GetLogger().Error("查询订单失败: %v, status=%s", err, status)
		return nil, err
	}
	return toModels(dos), nil
}
