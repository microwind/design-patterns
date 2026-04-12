package src

// OrderService 是订单服务（核心业务服务）。
//
// 【设计模式】
//   - 依赖倒置原则（DIP）：依赖 InventoryClient 接口而非具体实现。
//   - 外观模式（Facade）：CreateOrder 对外提供统一入口，隐藏内部流程。
//   - 策略模式：通过注入不同的 InventoryClient 实现，切换本地/远程调用。
//
// 【架构思想】
//   OrderService 只关心业务逻辑，不关心库存服务的具体位置和实现。
//   这种解耦使得服务可以独立部署和扩展。
//
// 【开源对比】
//   - go-kit：通过 endpoint + transport 层实现类似的关注点分离
//   - Kratos：通过 DI 容器（wire）实现依赖注入
//   本示例用构造函数注入模拟 IoC。
type OrderService struct {
	inventory InventoryClient // 库存服务客户端（接口注入，支持本地/远程切换）
}

// NewOrderService 创建订单服务，注入库存客户端。
func NewOrderService(inventory InventoryClient) *OrderService {
	return &OrderService{inventory: inventory}
}

// CreateOrder 创建订单。先调用库存服务预留库存，根据结果决定订单状态。
func (s *OrderService) CreateOrder(orderID string, sku string, quantity int) Order {
	// 通过契约接口调用库存服务（不关心是本地还是远程）
	if s.inventory.Reserve(sku, quantity) {
		return Order{OrderID: orderID, Sku: sku, Quantity: quantity, Status: "CREATED"}
	}
	return Order{OrderID: orderID, Sku: sku, Quantity: quantity, Status: "REJECTED"}
}
