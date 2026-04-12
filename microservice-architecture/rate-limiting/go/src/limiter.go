// Package src 实现了限流模式（Rate Limiting）的核心逻辑。
//
// 【设计模式】策略模式：固定窗口是一种限流策略。
//
// 【架构思想】限流保护系统不被过载流量拖垮。
//
// 【开源对比】
//   - uber-go/ratelimit：令牌桶限流器
//   - golang.org/x/time/rate：Go 标准扩展库的令牌桶
//   本示例实现固定窗口，省略了时间和并发安全。
package src

// FixedWindowLimiter 固定窗口限流器。
// 【设计模式】策略模式：固定窗口是一种限流策略。
type FixedWindowLimiter struct {
	limit int // 窗口内最大允许请求数
	count int // 当前窗口已通过请求数
}

// NewFixedWindowLimiter 创建固定窗口限流器。
func NewFixedWindowLimiter(limit int) *FixedWindowLimiter {
	return &FixedWindowLimiter{limit: limit}
}

// Allow 判断是否允许通过。count < limit 时放行，否则拒绝。
func (l *FixedWindowLimiter) Allow() bool {
	// 达到上限，拒绝
	if l.count >= l.limit {
		return false
	}
	// 放行并递增
	l.count++
	return true
}

// AdvanceWindow 推进窗口，重置计数。
func (l *FixedWindowLimiter) AdvanceWindow() {
	l.count = 0
}
