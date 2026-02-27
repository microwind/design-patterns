package order

import (
	"context"
	"database/sql"
	"errors"
	"fmt"

	model "gin-mvc/internal/models/order"
	dbutil "gin-mvc/internal/repository/db"
)

type SQLRepository struct {
	db     *sql.DB
	driver string
}

func NewSQLRepository(db *sql.DB, driver string) *SQLRepository {
	return &SQLRepository{db: db, driver: driver}
}

func (r *SQLRepository) Create(ctx context.Context, o *model.Order) error {
	if r.driver == "postgres" {
		query := fmt.Sprintf("INSERT INTO orders (order_no, user_id, total_amount, status, created_at, updated_at) VALUES (%s, %s, %s, %s, %s, %s) RETURNING id",
			dbutil.Placeholder(r.driver, 1), dbutil.Placeholder(r.driver, 2), dbutil.Placeholder(r.driver, 3), dbutil.Placeholder(r.driver, 4), dbutil.Placeholder(r.driver, 5), dbutil.Placeholder(r.driver, 6))
		return r.db.QueryRowContext(ctx, query, o.OrderNo, o.UserID, o.TotalAmount, o.Status, o.CreatedAt, o.UpdatedAt).Scan(&o.ID)
	}

	query := "INSERT INTO orders (order_no, user_id, total_amount, status, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?)"
	res, err := r.db.ExecContext(ctx, query, o.OrderNo, o.UserID, o.TotalAmount, o.Status, o.CreatedAt, o.UpdatedAt)
	if err != nil {
		return err
	}
	id, err := res.LastInsertId()
	if err != nil {
		return err
	}
	o.ID = id
	return nil
}

func (r *SQLRepository) Update(ctx context.Context, o *model.Order) error {
	query := fmt.Sprintf("UPDATE orders SET order_no = %s, user_id = %s, total_amount = %s, status = %s, updated_at = %s WHERE id = %s",
		dbutil.Placeholder(r.driver, 1), dbutil.Placeholder(r.driver, 2), dbutil.Placeholder(r.driver, 3), dbutil.Placeholder(r.driver, 4), dbutil.Placeholder(r.driver, 5), dbutil.Placeholder(r.driver, 6))
	_, err := r.db.ExecContext(ctx, query, o.OrderNo, o.UserID, o.TotalAmount, o.Status, o.UpdatedAt, o.ID)
	return err
}

func (r *SQLRepository) Delete(ctx context.Context, id int64) error {
	query := fmt.Sprintf("DELETE FROM orders WHERE id = %s", dbutil.Placeholder(r.driver, 1))
	_, err := r.db.ExecContext(ctx, query, id)
	return err
}

func (r *SQLRepository) FindByID(ctx context.Context, id int64) (*model.Order, error) {
	query := fmt.Sprintf("SELECT id, order_no, user_id, total_amount, status, created_at, updated_at FROM orders WHERE id = %s", dbutil.Placeholder(r.driver, 1))
	return r.findOne(ctx, query, id)
}

func (r *SQLRepository) FindByOrderNo(ctx context.Context, orderNo string) (*model.Order, error) {
	query := fmt.Sprintf("SELECT id, order_no, user_id, total_amount, status, created_at, updated_at FROM orders WHERE order_no = %s", dbutil.Placeholder(r.driver, 1))
	return r.findOne(ctx, query, orderNo)
}

func (r *SQLRepository) FindByUserID(ctx context.Context, userID int64) ([]*model.Order, error) {
	query := fmt.Sprintf("SELECT id, order_no, user_id, total_amount, status, created_at, updated_at FROM orders WHERE user_id = %s ORDER BY created_at DESC", dbutil.Placeholder(r.driver, 1))
	rows, err := r.db.QueryContext(ctx, query, userID)
	if err != nil {
		return nil, err
	}
	defer rows.Close()
	return r.scan(rows)
}

func (r *SQLRepository) FindAll(ctx context.Context) ([]*model.Order, error) {
	rows, err := r.db.QueryContext(ctx, "SELECT id, order_no, user_id, total_amount, status, created_at, updated_at FROM orders ORDER BY created_at DESC")
	if err != nil {
		return nil, err
	}
	defer rows.Close()
	return r.scan(rows)
}

func (r *SQLRepository) FindByStatus(ctx context.Context, status model.Status) ([]*model.Order, error) {
	query := fmt.Sprintf("SELECT id, order_no, user_id, total_amount, status, created_at, updated_at FROM orders WHERE status = %s ORDER BY created_at DESC", dbutil.Placeholder(r.driver, 1))
	rows, err := r.db.QueryContext(ctx, query, status)
	if err != nil {
		return nil, err
	}
	defer rows.Close()
	return r.scan(rows)
}

func (r *SQLRepository) findOne(ctx context.Context, query string, arg interface{}) (*model.Order, error) {
	var o model.Order
	err := r.db.QueryRowContext(ctx, query, arg).Scan(&o.ID, &o.OrderNo, &o.UserID, &o.TotalAmount, &o.Status, &o.CreatedAt, &o.UpdatedAt)
	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			return nil, nil
		}
		return nil, err
	}
	return &o, nil
}

func (r *SQLRepository) scan(rows *sql.Rows) ([]*model.Order, error) {
	orders := make([]*model.Order, 0)
	for rows.Next() {
		var o model.Order
		if err := rows.Scan(&o.ID, &o.OrderNo, &o.UserID, &o.TotalAmount, &o.Status, &o.CreatedAt, &o.UpdatedAt); err != nil {
			return nil, err
		}
		orders = append(orders, &o)
	}
	if err := rows.Err(); err != nil {
		return nil, err
	}
	return orders, nil
}
