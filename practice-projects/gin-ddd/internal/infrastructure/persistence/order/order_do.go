// Package order 提供订单聚合根的持久化适配。
//
// OrderDO 承担"与 orders 表字段一对一映射"的职责,所有 db tag 仅集中于此,
// 领域模型 *order.Order 不再背负持久化细节。
package order

import "time"

// OrderDO 订单数据对象。
type OrderDO struct {
	ID          int64     `db:"id"`
	OrderNo     string    `db:"order_no"`
	UserID      int64     `db:"user_id"`
	TotalAmount float64   `db:"total_amount"`
	Status      string    `db:"status"`
	CreatedAt   time.Time `db:"created_at"`
	UpdatedAt   time.Time `db:"updated_at"`
}
