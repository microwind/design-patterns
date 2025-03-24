// repository/order_mysql_repository.go
// order_mysql_repository与order_mysql_repository二选一即可
package repository

import (
  "database/sql"
  "fmt"
  "strings"
  "time"

  models "gin-order/internal/models/order"
)

// orderMySQLRepository 实现结构体，基于 *sql.DB， 实现 OrderRepository接口
type orderMySQLRepository struct {
  db *sql.DB
}

// NewOrderMySQLRepository 构造函数
func NewOrderMySQLRepository(db *sql.DB) OrderRepository {
  return &orderMySQLRepository{db: db}
}

// CreateOrder 创建订单（假设 models.Order 定义了对应字段）
func (r *orderMySQLRepository) CreateOrder(newOrder models.Order) (*models.Order, error) {
  newOrder.CreatedAt = time.Now()
  newOrder.UpdatedAt = time.Now()
  newOrder.Status = models.Created
  query := `
		INSERT INTO orders 
			(order_no, user_id, order_name, amount, status, created_at, updated_at)
		VALUES (?, ?, ?, ?, ?, ?, ?)
	`
  res, err := r.db.Exec(query,
    newOrder.OrderNo,
    newOrder.UserID,
    newOrder.OrderName,
    newOrder.Amount,
    newOrder.Status,
    newOrder.CreatedAt,
    newOrder.UpdatedAt,
  )
  if err != nil {
    return nil, fmt.Errorf("CreateOrder Exec error: %w", err)
  }
  id, err := res.LastInsertId()
  if err != nil {
    return nil, fmt.Errorf("CreateOrder LastInsertId error: %w", err)
  }
  newOrder.OrderID = id // 假设 OrderID 为 int64
  return &newOrder, nil
}

// GetByOrderNo 通过订单号查询订单
func (r *orderMySQLRepository) GetByOrderNo(orderNo string) (*models.Order, error) {
  query := `
		SELECT order_id, order_no, user_id, order_name, amount, status, created_at, updated_at
		FROM orders
		WHERE order_no = ?
	`
  var order models.Order
  err := r.db.QueryRow(query, orderNo).Scan(
    &order.OrderID,
    &order.OrderNo,
    &order.UserID,
    &order.OrderName,
    &order.Amount,
    &order.Status,
    &order.CreatedAt,
    &order.UpdatedAt,
  )
  if err != nil {
    return nil, fmt.Errorf("GetByOrderNo Scan error: %w", err)
  }
  return &order, nil
}

// GetAllOrders 获取所有订单（支持分页）
func (r *orderMySQLRepository) GetAllOrders(page, pageSize int) ([]models.Order, int, error) {
  // 计算 OFFSET 和 LIMIT
  offset := (page - 1) * pageSize

  // 查询分页数据
  query := `
		SELECT order_id, order_no, user_id, order_name, amount, status, created_at, updated_at
		FROM orders
		LIMIT ? OFFSET ?
	`
  rows, err := r.db.Query(query, pageSize, offset)
  if err != nil {
    return nil, 0, fmt.Errorf("GetAllOrders Query error: %w", err)
  }
  defer rows.Close()

  // 解析查询结果
  var orders []models.Order
  for rows.Next() {
    var order models.Order
    err := rows.Scan(
      &order.OrderID,
      &order.OrderNo,
      &order.UserID,
      &order.OrderName,
      &order.Amount,
      &order.Status,
      &order.CreatedAt,
      &order.UpdatedAt,
    )
    if err != nil {
      return nil, 0, fmt.Errorf("GetAllOrders Scan error: %w", err)
    }
    orders = append(orders, order)
  }

  // 获取总记录数
  var total int
  countQuery := "SELECT COUNT(*) FROM orders"
  err = r.db.QueryRow(countQuery).Scan(&total)
  if err != nil {
    return nil, 0, fmt.Errorf("GetAllOrders Count query error: %w", err)
  }

  return orders, total, nil
}

// GetOrdersByUserID 根据用户ID查询订单（支持分页）
func (r *orderMySQLRepository) GetOrdersByUserID(userId string, page, pageSize int) ([]models.Order, int, error) {
  // 计算 OFFSET 和 LIMIT
  offset := (page - 1) * pageSize

  // 查询分页数据
  query := `
		SELECT order_id, order_no, user_id, order_name, amount, status, created_at, updated_at
		FROM orders
		WHERE user_id = ?
		LIMIT ? OFFSET ?
	`
  rows, err := r.db.Query(query, userId, pageSize, offset)
  if err != nil {
    return nil, 0, fmt.Errorf("GetOrdersByUserID Query error: %w", err)
  }
  defer rows.Close()

  // 解析查询结果
  var orders []models.Order
  for rows.Next() {
    var order models.Order
    err := rows.Scan(
      &order.OrderID,
      &order.OrderNo,
      &order.UserID,
      &order.OrderName,
      &order.Amount,
      &order.Status,
      &order.CreatedAt,
      &order.UpdatedAt,
    )
    if err != nil {
      return nil, 0, fmt.Errorf("GetOrdersByUserID Scan error: %w", err)
    }
    orders = append(orders, order)
  }

  // 获取总记录数
  var total int
  countQuery := "SELECT COUNT(*) FROM orders WHERE user_id = ?"
  err = r.db.QueryRow(countQuery, userId).Scan(&total)
  if err != nil {
    return nil, 0, fmt.Errorf("GetOrdersByUserID Count query error: %w", err)
  }

  return orders, total, nil
}

// UpdateOrder 更新订单（字段不为空则更新）
func (r *orderMySQLRepository) UpdateOrder(updatedOrder models.Order) (*models.Order, error) {
  if updatedOrder.UpdatedAt.IsZero() {
    updatedOrder.UpdatedAt = time.Now()
  }

  // 初始化字段和参数
  var setClauses []string
  var args []interface{}

  // 根据字段是否为空来构建更新的 SQL 语句
  if updatedOrder.UserID != 0 {
    setClauses = append(setClauses, "user_id = ?")
    args = append(args, updatedOrder.UserID)
  }
  if updatedOrder.OrderName != "" {
    setClauses = append(setClauses, "order_name = ?")
    args = append(args, updatedOrder.OrderName)
  }
  // 使用 IsZero() 判断 decimal.Decimal 是否为零
  if !updatedOrder.Amount.IsZero() {
    setClauses = append(setClauses, "amount = ?")
    args = append(args, updatedOrder.Amount)
  }
  if updatedOrder.Status != "" {
    setClauses = append(setClauses, "status = ?")
    args = append(args, updatedOrder.Status)
  }
  setClauses = append(setClauses, "updated_at = ?")
  args = append(args, updatedOrder.UpdatedAt)
  // 如果没有任何字段需要更新，返回错误
  if len(setClauses) == 0 {
    return nil, fmt.Errorf("no fields to update")
  }

  // 构建最终的 SQL 查询
  query := fmt.Sprintf("UPDATE orders SET %s WHERE order_no = ?", strings.Join(setClauses, ", "))
  args = append(args, updatedOrder.OrderNo) // 添加查询条件

  // 执行 SQL 查询
  res, err := r.db.Exec(query, args...)
  if err != nil {
    return nil, fmt.Errorf("UpdateOrder Exec error: %w", err)
  }

  // 检查更新的行数
  rowsAffected, err := res.RowsAffected()
  if err != nil {
    return nil, fmt.Errorf("UpdateOrder RowsAffected error: %w", err)
  }
  if rowsAffected == 0 {
    return nil, sql.ErrNoRows
  }

  return &updatedOrder, nil
}

// UpdateOrderStatus 更新订单状态
func (r *orderMySQLRepository) UpdateOrderStatus(orderNo, status string) (*models.Order, error) {
  query := "UPDATE orders SET status = ? WHERE order_no = ?"
  res, err := r.db.Exec(query, status, orderNo)
  if err != nil {
    return nil, fmt.Errorf("UpdateOrderStatus Exec error: %w", err)
  }
  rowsAffected, err := res.RowsAffected()
  if err != nil {
    return nil, fmt.Errorf("UpdateOrderStatus RowsAffected error: %w", err)
  }
  if rowsAffected == 0 {
    return nil, sql.ErrNoRows
  }
  // 查询并返回更新后的订单
  return r.GetByOrderNo(orderNo)
}

// DeleteOrder 删除订单
func (r *orderMySQLRepository) DeleteOrder(orderNo string) error {
  query := "DELETE FROM orders WHERE order_no = ?"
  _, err := r.db.Exec(query, orderNo)
  if err != nil {
    return fmt.Errorf("DeleteOrder Exec error: %w", err)
  }
  return nil
}
