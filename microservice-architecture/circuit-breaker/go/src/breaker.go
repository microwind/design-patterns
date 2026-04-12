// Package src 实现了断路器模式（Circuit Breaker Pattern）的核心逻辑。
//
// 【设计模式】
//   - 状态模式（State Pattern）：断路器在 closed / open / half-open 三种状态下
//     行为不同。本示例用字符串字段 + 条件分支简化实现；在实际工程中（如 sony/gobreaker）
//     通常会用独立的状态对象或回调来处理不同状态的行为。
//   - 代理模式（Proxy Pattern）：断路器包裹在真实服务调用之外，对调用方透明地拦截请求。
//
// 【架构思想】
//   断路器防止调用方在下游故障时持续重试导致级联雪崩，通过探测机制实现自动恢复。
//
// 【开源对比】
//   - sony/gobreaker：Go 语言最流行的断路器库，支持滑动窗口、自定义状态变更回调、
//     ReadyToTrip 自定义判定逻辑。
//   - afex/hystrix-go：Netflix Hystrix 的 Go 移植版。
//   本示例省略了时间窗口、goroutine 安全、超时控制等工程细节，聚焦于状态机核心。
package src

// CircuitBreaker 实现断路器状态机。
//
// 状态转换：
//
//	closed → open（连续失败达到阈值）
//	open → half-open → closed（探测成功）
//	open → half-open → open（探测失败）
type CircuitBreaker struct {
	state            string // 当前状态：closed / open / half-open
	failures         int    // 当前连续失败次数
	failureThreshold int    // 触发熔断的失败阈值
}

// NewCircuitBreaker 创建一个断路器实例。
//
// failureThreshold: 连续失败多少次后触发熔断。
// 对比 sony/gobreaker 的 Settings.ReadyToTrip 回调，本示例简化为固定阈值。
func NewCircuitBreaker(failureThreshold int) *CircuitBreaker {
	return &CircuitBreaker{state: "closed", failureThreshold: failureThreshold}
}

// RecordFailure 记录一次失败调用。
// 仅在 closed 状态下生效：累加失败计数，达到阈值时切换到 open。
func (b *CircuitBreaker) RecordFailure() {
	// 只有闭合状态下才统计失败
	if b.state == "closed" {
		b.failures++
		// 失败次数达到阈值，打开断路器
		if b.failures >= b.failureThreshold {
			b.state = "open"
		}
	}
}

// Probe 在 open 状态下进行一次探测调用。
// 先转为 half-open，再根据 success 参数决定恢复 closed 或回退 open。
//
// 对比 sony/gobreaker：生产环境中 open → half-open 的转换由内置定时器自动触发，
// 而非外部手动调用。本示例由外部显式调用 Probe 以简化实现。
func (b *CircuitBreaker) Probe(success bool) {
	// 非 open 状态下不需要探测
	if b.state != "open" {
		return
	}
	// 进入半开状态，允许一次试探调用
	b.state = "half-open"
	if success {
		// 探测成功，恢复正常
		b.state = "closed"
		b.failures = 0
	} else {
		// 探测失败，重新熔断
		b.state = "open"
	}
}

// State 返回断路器当前状态字符串。
func (b *CircuitBreaker) State() string {
	return b.state
}
