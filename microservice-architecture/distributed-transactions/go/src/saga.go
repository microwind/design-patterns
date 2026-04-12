// Package src 实现了分布式事务 Saga 模式的核心逻辑。
//
// 【设计模式】
//   - 命令模式：每个步骤（reserve/charge）和补偿（release）是独立命令。
//   - 责任链模式：正向步骤按链式顺序执行，失败则中断并补偿。
//   - 状态模式：订单状态 PENDING → COMPLETED / CANCELLED。
//
// 【架构思想】
//   Saga 将跨服务事务拆分为有序步骤 + 补偿动作，实现最终一致性。
//
// 【开源对比】
//   - go-saga：Go 语言 Saga 库
//   - Temporal Go SDK：强类型工作流引擎
//   本示例用同步方法调用模拟编排式 Saga。
package src

// SagaOrder Saga 订单实体。状态：PENDING → COMPLETED / CANCELLED。
type SagaOrder struct {
	OrderID string
	Status  string // PENDING / COMPLETED / CANCELLED
}

// InventoryService 库存服务。提供正向操作和补偿操作。
type InventoryService struct {
	bookStock int
}

// PaymentService 支付服务。fail 标志模拟支付失败。
type PaymentService struct {
	fail bool
}

// SagaCoordinator Saga 协调者（编排式）。
// 【设计模式】命令模式 + 责任链：按序执行步骤，失败时逆序补偿。
type SagaCoordinator struct {
	inventory *InventoryService
	payment   *PaymentService
}

// NewSagaCoordinator 创建 Saga 协调者。
func NewSagaCoordinator(stock int, paymentFails bool) *SagaCoordinator {
	return &SagaCoordinator{
		inventory: &InventoryService{bookStock: stock},
		payment:   &PaymentService{fail: paymentFails},
	}
}

// Execute 执行 Saga 事务：库存预占 → 支付 → 成功/补偿。
func (s *SagaCoordinator) Execute(orderID string, sku string, quantity int) SagaOrder {
	order := SagaOrder{OrderID: orderID, Status: "PENDING"}

	// 步骤1：库存预占
	if !s.inventory.reserve(sku, quantity) {
		order.Status = "CANCELLED"
		return order
	}

	// 步骤2：支付扣款
	if !s.payment.charge(orderID) {
		// 支付失败 → 补偿：释放库存
		s.inventory.release(sku, quantity)
		order.Status = "CANCELLED"
		return order
	}

	// 全部成功
	order.Status = "COMPLETED"
	return order
}

// AvailableStock 获取当前库存（用于测试验证补偿是否生效）。
func (s *SagaCoordinator) AvailableStock() int {
	return s.inventory.bookStock
}

// reserve 正向步骤：预占库存。
func (i *InventoryService) reserve(sku string, quantity int) bool {
	if sku != "SKU-BOOK" || quantity <= 0 || i.bookStock < quantity {
		return false
	}
	i.bookStock -= quantity
	return true
}

// release 补偿动作：释放已预占的库存。
func (i *InventoryService) release(sku string, quantity int) {
	if sku == "SKU-BOOK" && quantity > 0 {
		i.bookStock += quantity
	}
}

// charge 正向步骤：扣款。
func (p *PaymentService) charge(orderID string) bool {
	return !p.fail
}
