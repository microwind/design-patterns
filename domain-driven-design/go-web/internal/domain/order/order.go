// 领域层(Domain)：领域实体
package order

import "fmt"

// OrderStatus 表示订单的状态
type OrderStatus int

const (
  CREATED  OrderStatus = iota // 订单已创建
  CANCELED                    // 订单已取消
)

// Order 订单实体，作为聚合根
type Order struct {
  ID           int64       // 订单ID
  CustomerName string      // 客户名称
  Amount       float64     // 订单金额
  Status       OrderStatus // 订单状态
}

// NewOrder 创建新订单，返回一个 Order 实体
func NewOrder(id int64, customerName string, amount float64) *Order {
  if amount <= 0 {
    fmt.Println("订单金额无效")
    return nil
  }
  return &Order{
    ID:           id,
    CustomerName: customerName,
    Amount:       amount,
    Status:       CREATED,
  }
}

// Cancel 取消订单，修改订单状态
func (o *Order) Cancel() {
  if o.Status == CREATED {
    o.Status = CANCELED
    fmt.Printf("订单 ID %d 已取消\n", o.ID)
  } else {
    fmt.Printf("订单 ID %d 已取消，无法重复取消\n", o.ID)
  }
}

// UpdateCustomerInfo 更新客户名称
func (o *Order) UpdateCustomerInfo(newCustomerName string) {
  o.CustomerName = newCustomerName
  fmt.Printf("订单 ID %d 的客户名称已更新为: %s\n", o.ID, o.CustomerName)
}

// UpdateAmount 更新订单金额
func (o *Order) UpdateAmount(newAmount float64) {
  if newAmount <= 0 {
    fmt.Println("更新金额无效")
    return
  }
  o.Amount = newAmount
  fmt.Printf("订单 ID %d 的金额已更新为: %.2f\n", o.ID, o.Amount)
}

// Display 显示订单信息
func (o *Order) Display() {
  fmt.Printf("订单 ID: %d\n客户名称: %s\n订单金额: %.2f\n订单状态: %s\n",
    o.ID, o.CustomerName, o.Amount, o.StatusToString())
}

// StatusToString 获取订单状态的字符串表示
func (o *Order) StatusToString() string {
  switch o.Status {
  case CREATED:
    return "已创建"
  case CANCELED:
    return "已取消"
  default:
    return "未知状态"
  }
}
