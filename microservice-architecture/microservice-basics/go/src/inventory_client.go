// Package src 实现了微服务基础（Microservice Basics）的核心逻辑。
//
// 本包演示微服务拆分的基本模式：订单服务通过契约接口调用库存服务，
// 支持从本地调用（阶段1）无缝切换到 HTTP 远程调用（阶段2）。
//
// 【设计模式】
//   - 依赖倒置原则（DIP）：OrderService 依赖 InventoryClient 接口
//   - 适配器模式（Adapter Pattern）：HttpInventoryClient 适配 HTTP 为统一接口
//   - 策略模式（Strategy Pattern）：通过注入不同实现切换调用策略
//
// 【开源对比】
//   - Go 微服务框架：go-kit、go-micro、Kratos
//   - 服务间通信：gRPC-Go、HTTP + JSON
//   本示例用原生 interface + HTTP 展示最基础的服务拆分与通信。
package src

// InventoryClient 定义库存服务的契约接口。
//
// 【设计模式】依赖倒置原则 + 策略模式：
// OrderService 依赖此接口而非具体实现，通过替换实现类可切换本地/远程调用。
//
// 【开源对比】在 gRPC 中，此接口由 .proto 文件自动生成。
type InventoryClient interface {
	// Reserve 预留库存，返回是否成功
	Reserve(sku string, quantity int) bool
}
