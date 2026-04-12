// Package src 实现了重试模式（Retry Pattern）的核心逻辑。
//
// 【设计模式】
//   - 策略模式：operation 作为函数参数（策略）传入 Retry。
//   - 模板方法模式：Retry 定义了循环调用的骨架。
//
// 【架构思想】重试处理暂时性故障，但必须控制最大次数。
//
// 【开源对比】
//   - avast/retry-go：Go 重试库，支持指数退避和可重试错误过滤
//   - cenkalti/backoff：Go 退避库
//   本示例实现固定次数重试。
package src

// ScriptedOperation 脚本化操作（测试辅助）。
// 模拟"前 N 次失败，之后成功"的场景。
type ScriptedOperation struct {
	failuresBeforeSuccess int // 成功前需要失败的次数
	attempts              int // 当前已尝试次数
}

// NewScriptedOperation 创建脚本化操作。
func NewScriptedOperation(failuresBeforeSuccess int) *ScriptedOperation {
	return &ScriptedOperation{failuresBeforeSuccess: failuresBeforeSuccess}
}

// Call 调用操作。前 failuresBeforeSuccess 次返回 false，之后返回 true。
func (o *ScriptedOperation) Call() bool {
	o.attempts++
	return o.attempts > o.failuresBeforeSuccess
}

// Retry 执行重试。循环调用操作，成功时立即返回。
// 返回 (是否成功, 实际尝试次数)。
func Retry(maxAttempts int, operation func() bool) (bool, int) {
	for attempt := 1; attempt <= maxAttempts; attempt++ {
		// 调用操作，成功则立即返回
		if operation() {
			return true, attempt
		}
	}
	// 达到最大次数仍失败
	return false, maxAttempts
}
