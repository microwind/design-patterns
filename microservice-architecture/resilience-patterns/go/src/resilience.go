// Package src 实现了弹性模式组合（Resilience Patterns）的核心逻辑。
//
// 本模块将超时、重试、断路器三种弹性模式组合在一起，展示它们如何协同工作。
//
// 【设计模式】
//   - 策略模式（Strategy Pattern）：超时、重试、断路器是三种可独立使用或组合的弹性策略。
//   - 代理模式（Proxy Pattern）：CallWithTimeout / Retry / CircuitBreaker.Execute
//     包裹在真实操作之外，透明地添加弹性行为。
//   - 状态模式（State Pattern）：CircuitBreaker 在 closed/open 状态下行为不同。
//   - 模板方法模式（Template Method）：Retry 定义了循环调用的固定骨架。
//
// 【架构思想】
//   超时防止无限等待，重试处理暂时性故障，断路器阻止级联雪崩。
//
// 【开源对比】
//   - sony/gobreaker：Go 断路器库
//   - avast/retry-go：Go 重试库，支持指数退避
//   - failsafe-go：Go 弹性库，支持策略组合
//   本示例省略了指数退避、goroutine 安全等工程细节，聚焦于三种模式的核心逻辑。
package src

import (
	"errors"
	"time"
)

var ErrTimeout = errors.New("operation timed out")
var ErrCircuitOpen = errors.New("circuit breaker is open")

type Result struct {
	Value string
	Err   error
	Delay time.Duration
}

type ScriptedDependency struct {
	results []Result
	index   int
}

func NewScriptedDependency(results []Result) *ScriptedDependency {
	return &ScriptedDependency{results: results}
}

func (d *ScriptedDependency) Call() (string, error) {
	if len(d.results) == 0 {
		return "", errors.New("no scripted result available")
	}

	index := d.index
	if index >= len(d.results) {
		index = len(d.results) - 1
	}
	result := d.results[index]
	d.index++

	if result.Delay > 0 {
		time.Sleep(result.Delay)
	}

	return result.Value, result.Err
}

func CallWithTimeout(timeout time.Duration, operation func() (string, error)) (string, error) {
	type response struct {
		value string
		err   error
	}

	done := make(chan response, 1)
	go func() {
		value, err := operation()
		done <- response{value: value, err: err}
	}()

	select {
	case result := <-done:
		return result.value, result.err
	case <-time.After(timeout):
		return "", ErrTimeout
	}
}

func Retry(maxAttempts int, operation func() (string, error)) (string, int, error) {
	if maxAttempts <= 0 {
		maxAttempts = 1
	}

	var lastErr error
	for attempt := 1; attempt <= maxAttempts; attempt++ {
		value, err := operation()
		if err == nil {
			return value, attempt, nil
		}
		lastErr = err
	}

	return "", maxAttempts, lastErr
}

type CircuitBreaker struct {
	failureThreshold    int
	consecutiveFailures int
	open                bool
}

func NewCircuitBreaker(failureThreshold int) *CircuitBreaker {
	if failureThreshold <= 0 {
		failureThreshold = 1
	}
	return &CircuitBreaker{failureThreshold: failureThreshold}
}

func (c *CircuitBreaker) Execute(operation func() (string, error), fallback string) (string, error) {
	if c.open {
		return fallback, ErrCircuitOpen
	}

	value, err := operation()
	if err != nil {
		c.consecutiveFailures++
		if c.consecutiveFailures >= c.failureThreshold {
			c.open = true
		}
		return fallback, err
	}

	c.consecutiveFailures = 0
	return value, nil
}

func (c *CircuitBreaker) Reset() {
	c.open = false
	c.consecutiveFailures = 0
}
