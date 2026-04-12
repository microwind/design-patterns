package src

// InventoryService 是本地库存服务实现（阶段1）。
//
// 【设计模式】
//   - 策略模式：作为 InventoryClient 接口的本地实现策略，直接操作内存库存。
//   - 适配器模式：将内存 map 操作适配为统一的 Reserve 接口。
//
// 【架构思想】
//   阶段1 的库存服务运行在同一进程内，代表单体架构。
//   当切换到 HttpInventoryClient 时，OrderService 无需修改。
//
// 【开源对比】
//   实际工程中库存数据存储在数据库（MySQL/Redis），预留需要分布式锁保证并发安全。
//   本示例用内存 map 简化，聚焦于服务拆分和契约调用的本质。
type InventoryService struct {
	stock map[string]int // 内存库存表：SKU -> 可用数量
}

// NewInventoryService 创建本地库存服务，初始化测试库存数据。
func NewInventoryService() *InventoryService {
	return &InventoryService{stock: map[string]int{
		"SKU-BOOK": 10,
		"SKU-PEN":  1,
	}}
}

// Reserve 预留库存。检查是否充足，充足则扣减并返回 true。
func (s *InventoryService) Reserve(sku string, quantity int) bool {
	available, ok := s.stock[sku]
	// 库存不存在或不足，拒绝预留
	if !ok || available < quantity {
		return false
	}
	// 扣减库存
	s.stock[sku] = available - quantity
	return true
}

// Available 查询指定 SKU 的可用库存数量。
func (s *InventoryService) Available(sku string) int {
	return s.stock[sku]
}
