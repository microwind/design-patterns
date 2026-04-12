// Package src 实现了幂等模式（Idempotency Pattern）的核心逻辑。
//
// 【设计模式】
//   - 备忘录模式（Memento Pattern）：首次执行结果被存储，后续重复请求返回备忘结果。
//   - 代理模式（Proxy Pattern）：幂等层包裹在业务逻辑之外，透明拦截重复请求。
//
// 【架构思想】
//   幂等模式通过 idempotencyKey 将重复请求折叠为同一结果，防止重复执行业务副作用。
//
// 【开源对比】
//   - Go 生态中通常通过 Redis SETNX + TTL 实现幂等
//   - gRPC 通过 request ID 实现请求去重
//   本示例用内存 map 简化，省略了 TTL 和并发控制。
package src

import "fmt"

// OrderResponse 表示订单响应。Replayed 字段区分首次/重复请求。
type OrderResponse struct {
	OrderID  string // 订单ID
	SKU      string // 商品SKU
	Quantity int    // 数量
	Status   string // 状态：CREATED / CONFLICT
	Replayed bool   // 是否为重放结果
}

// storedResult 存储的幂等结果（内部类型）。
type storedResult struct {
	fingerprint string        // 请求指纹，用于冲突检测
	response    OrderResponse // 首次执行的响应
}

// IdempotencyOrderService 带幂等保护的订单服务。
type IdempotencyOrderService struct {
	store map[string]storedResult // 幂等存储：key → result
}

// NewIdempotencyOrderService 创建幂等订单服务。
func NewIdempotencyOrderService() *IdempotencyOrderService {
	return &IdempotencyOrderService{store: map[string]storedResult{}}
}

// CreateOrder 创建订单（带幂等保护）。
// 三条路径：首次 → CREATED，重复+匹配 → replayed，重复+不匹配 → CONFLICT。
func (s *IdempotencyOrderService) CreateOrder(idempotencyKey string, orderID string, sku string, quantity int) OrderResponse {
	// 计算请求指纹
	fingerprint := requestFingerprint(orderID, sku, quantity)
	if existing, ok := s.store[idempotencyKey]; ok {
		// 同一幂等键但参数不同 → 冲突
		if existing.fingerprint != fingerprint {
			return OrderResponse{
				OrderID: orderID, SKU: sku, Quantity: quantity,
				Status: "CONFLICT", Replayed: false,
			}
		}
		// 同一幂等键且参数相同 → 返回存储的结果
		response := existing.response
		response.Replayed = true
		return response
	}

	// 首次请求 → 执行业务逻辑并存储结果
	response := OrderResponse{
		OrderID: orderID, SKU: sku, Quantity: quantity,
		Status: "CREATED", Replayed: false,
	}
	s.store[idempotencyKey] = storedResult{fingerprint: fingerprint, response: response}
	return response
}

// requestFingerprint 计算请求指纹（参数拼接）。
func requestFingerprint(orderID string, sku string, quantity int) string {
	return fmt.Sprintf("%s|%s|%d", orderID, sku, quantity)
}
