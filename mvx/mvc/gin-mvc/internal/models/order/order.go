package models

import (
  "database/sql/driver"
  "errors"
  "fmt"
  "time"

  "github.com/google/uuid"
  "github.com/shopspring/decimal"
  "gorm.io/gorm"
)

// Order 订单实体
type Order struct {
  OrderID   int64           `gorm:"primaryKey;autoIncrement"`     // 符合GORM惯例
  OrderNo   string          `gorm:"uniqueIndex;not null;size:36"` // 添加唯一索引，限制长度
  UserID    int64           `gorm:"not null"`
  Amount    decimal.Decimal `gorm:"type:decimal(10,2)"`               // 使用更合适的decimal类型
  OrderName string          `gorm:"not null;size:200"`                // 添加合理长度限制
  Status    OrderStatus     `gorm:"type:varchar(20);index"`           // 添加索引
  CreatedAt time.Time       `gorm:"autoCreateTime;column:created_at"` // 自动创建时间[6,7](@ref)
  UpdatedAt time.Time       `gorm:"autoUpdateTime;column:updated_at"` // 自动更新时间[6,7](@ref)
}

// OrderStatus 订单状态枚举
type OrderStatus string

const (
  Created   OrderStatus = "CREATED"
  Paid      OrderStatus = "PAID"
  Delivered OrderStatus = "DELIVERED"
  Completed OrderStatus = "COMPLETED"
  Cancelled OrderStatus = "CANCELLED"
)

// Value 实现 driver.Valuer 接口
func (o OrderStatus) Value() (driver.Value, error) {
  return string(o), nil
}

// Scan 实现 sql.Scanner 接口（增强类型处理）
func (o *OrderStatus) Scan(value interface{}) error {
  if value == nil {
    *o = ""
    return nil
  }

  switch v := value.(type) {
  case string:
    *o = OrderStatus(v)
  case []byte:
    *o = OrderStatus(string(v))
  default:
    return errors.New("failed to scan OrderStatus: invalid type")
  }
  return nil
}

// BeforeCreate 在创建订单前自动生成订单编号（添加错误处理）
func (o *Order) BeforeCreate(tx *gorm.DB) error {
  if o.OrderNo == "" {
    uuidObj, err := uuid.NewRandom()
    if err != nil {
      return fmt.Errorf("生成订单号失败: %w", err)
    }
    o.OrderNo = uuidObj.String()
  }

  // 金额默认值处理
  if o.Amount.IsZero() {
    o.Amount = decimal.Zero
  }

  return nil
}
