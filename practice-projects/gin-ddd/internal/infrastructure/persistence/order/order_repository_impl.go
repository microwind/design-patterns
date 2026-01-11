package order

import (
	"context"
	"database/sql"
	"encoding/json"
	"errors"
	"gin-ddd/internal/domain/model/order"
)

// OrderRepositoryImpl 订单仓储实现
type OrderRepositoryImpl struct {
	db *sql.DB
}

// NewOrderRepository 创建订单仓储实例
func NewOrderRepository(db *sql.DB) *OrderRepositoryImpl {
	return &OrderRepositoryImpl{
		db: db,
	}
}

// Create 创建订单
func (r *OrderRepositoryImpl) Create(ctx context.Context, o *order.Order) error {
	// 将订单项序列化为 JSON
	itemsJSON, err := json.Marshal(o.Items)
	if err != nil {
		return err
	}

	query := `
		INSERT INTO orders (order_no, user_id, total_amount, status, items, created_at, updated_at)
		VALUES (?, ?, ?, ?, ?, ?, ?)
	`
	result, err := r.db.ExecContext(ctx, query,
		o.OrderNo, o.UserID, o.TotalAmount, o.Status, itemsJSON, o.CreatedAt, o.UpdatedAt)
	if err != nil {
		return err
	}

	id, err := result.LastInsertId()
	if err != nil {
		return err
	}
	o.ID = id
	return nil
}

// Update 更新订单
func (r *OrderRepositoryImpl) Update(ctx context.Context, o *order.Order) error {
	// 将订单项序列化为 JSON
	itemsJSON, err := json.Marshal(o.Items)
	if err != nil {
		return err
	}

	query := `
		UPDATE orders
		SET order_no = ?, user_id = ?, total_amount = ?, status = ?, items = ?, updated_at = ?
		WHERE id = ?
	`
	_, err = r.db.ExecContext(ctx, query,
		o.OrderNo, o.UserID, o.TotalAmount, o.Status, itemsJSON, o.UpdatedAt, o.ID)
	return err
}

// Delete 删除订单
func (r *OrderRepositoryImpl) Delete(ctx context.Context, id int64) error {
	query := `DELETE FROM orders WHERE id = ?`
	_, err := r.db.ExecContext(ctx, query, id)
	return err
}

// FindByID 根据ID查询订单
func (r *OrderRepositoryImpl) FindByID(ctx context.Context, id int64) (*order.Order, error) {
	query := `
		SELECT id, order_no, user_id, total_amount, status, items, created_at, updated_at
		FROM orders WHERE id = ?
	`
	var o order.Order
	var itemsJSON []byte
	err := r.db.QueryRowContext(ctx, query, id).Scan(
		&o.ID, &o.OrderNo, &o.UserID, &o.TotalAmount, &o.Status, &itemsJSON, &o.CreatedAt, &o.UpdatedAt)
	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			return nil, nil
		}
		return nil, err
	}

	// 反序列化订单项
	if err := json.Unmarshal(itemsJSON, &o.Items); err != nil {
		return nil, err
	}

	return &o, nil
}

// FindByOrderNo 根据订单号查询订单
func (r *OrderRepositoryImpl) FindByOrderNo(ctx context.Context, orderNo string) (*order.Order, error) {
	query := `
		SELECT id, order_no, user_id, total_amount, status, items, created_at, updated_at
		FROM orders WHERE order_no = ?
	`
	var o order.Order
	var itemsJSON []byte
	err := r.db.QueryRowContext(ctx, query, orderNo).Scan(
		&o.ID, &o.OrderNo, &o.UserID, &o.TotalAmount, &o.Status, &itemsJSON, &o.CreatedAt, &o.UpdatedAt)
	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			return nil, nil
		}
		return nil, err
	}

	// 反序列化订单项
	if err := json.Unmarshal(itemsJSON, &o.Items); err != nil {
		return nil, err
	}

	return &o, nil
}

// FindByUserID 根据用户ID查询订单列表
func (r *OrderRepositoryImpl) FindByUserID(ctx context.Context, userID int64) ([]*order.Order, error) {
	query := `
		SELECT id, order_no, user_id, total_amount, status, items, created_at, updated_at
		FROM orders WHERE user_id = ? ORDER BY created_at DESC
	`
	rows, err := r.db.QueryContext(ctx, query, userID)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	return r.scanOrders(rows)
}

// FindAll 查询所有订单
func (r *OrderRepositoryImpl) FindAll(ctx context.Context) ([]*order.Order, error) {
	query := `
		SELECT id, order_no, user_id, total_amount, status, items, created_at, updated_at
		FROM orders ORDER BY created_at DESC
	`
	rows, err := r.db.QueryContext(ctx, query)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	return r.scanOrders(rows)
}

// FindByStatus 根据状态查询订单列表
func (r *OrderRepositoryImpl) FindByStatus(ctx context.Context, status order.OrderStatus) ([]*order.Order, error) {
	query := `
		SELECT id, order_no, user_id, total_amount, status, items, created_at, updated_at
		FROM orders WHERE status = ? ORDER BY created_at DESC
	`
	rows, err := r.db.QueryContext(ctx, query, status)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	return r.scanOrders(rows)
}

// scanOrders 扫描订单结果集
func (r *OrderRepositoryImpl) scanOrders(rows *sql.Rows) ([]*order.Order, error) {
	var orders []*order.Order
	for rows.Next() {
		var o order.Order
		var itemsJSON []byte
		if err := rows.Scan(&o.ID, &o.OrderNo, &o.UserID, &o.TotalAmount, &o.Status, &itemsJSON, &o.CreatedAt, &o.UpdatedAt); err != nil {
			return nil, err
		}

		// 反序列化订单项
		if err := json.Unmarshal(itemsJSON, &o.Items); err != nil {
			return nil, err
		}

		orders = append(orders, &o)
	}
	return orders, nil
}
