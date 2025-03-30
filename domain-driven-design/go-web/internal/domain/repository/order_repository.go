// 领域层(Domain)：订单数据仓库接口
package repository

import (
	"go-web-order/internal/domain/order"
)

// OrderRepository 订单仓储接口，定义对订单数据的操作。
// type OrderRepository interface {
//   Save(order *order.Order) error                                  // 保存订单
//   FindByID(id int64) (*order.Order, error)                        // 根据ID查找订单
//   FindAll(userId int) ([]*order.Order, error)                     // 查找所有订单
//   Delete(id int64) error                                          // 删除订单
//   FindByCustomerName(customerName string) ([]*order.Order, error) // 根据客户名称查找订单
// }

// [可选]OrderRepository 订单仓储接口，继承通用仓储接口 Repository[order.Order]
type OrderRepository interface {
  Repository[order.Order]  // 继承通用仓储接口
  FindByCustomerName(customerName string) ([]*order.Order, error) // 根据客户名称查找订单
}
