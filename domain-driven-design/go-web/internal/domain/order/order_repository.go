// 领域层(Domain)：订单数据仓库接口
package order

// OrderRepository 订单仓储接口，定义对订单数据的操作
type OrderRepository interface {
  Save(order *Order) error                                  // 保存订单
  FindByID(id int) (*Order, error)                          // 根据ID查找订单
  FindAll() ([]*Order, error)                               // 查找所有订单
  Delete(id int) error                                      // 删除订单
  FindByCustomerName(customerName string) ([]*Order, error) // 根据客户名称查找订单
}
