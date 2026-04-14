/**
 * @file resilience.js - 弹性模式组合（Resilience Patterns）的 JavaScript 实现
 *
 * 本模块将超时、重试、断路器三种弹性模式组合在一起，展示它们如何协同工作。
 *
 * 【设计模式】
 *   - 策略模式（Strategy Pattern）：超时、重试、断路器是可独立使用或组合的弹性策略。
 *   - 代理模式（Proxy Pattern）：callWithTimeout / retry / CircuitBreaker.execute
 *     包裹在真实操作之外，透明地添加弹性行为。
 *   - 状态模式（State Pattern）：CircuitBreaker 在 closed/open 状态下行为不同。
 *
 * 【架构思想】
 *   超时防止无限等待，重试处理暂时性故障，断路器阻止级联雪崩。
 *
 * 【开源对比】
 *   - cockatiel（Node.js）：受 .NET Polly 启发的弹性库，支持策略组合
 *   - opossum（Node.js）：断路器库
 *   本示例省略了指数退避、Promise 链等工程细节，聚焦于三种模式的核心逻辑。
 */

export class OperationTimeoutError extends Error {}
export class CircuitOpenError extends Error {}

export class ScriptedDependency {
  constructor(results) {
    this.results = results
    this.index = 0
  }

  async call() {
    if (this.results.length === 0) {
      throw new Error('no scripted result available')
    }

    const current = this.results[Math.min(this.index, this.results.length - 1)]
    this.index++

    if (current.delayMs && current.delayMs > 0) {
      await new Promise((resolve) => setTimeout(resolve, current.delayMs))
    }
    if (current.error) {
      throw current.error
    }
    return current.value || ''
  }
}

export function callWithTimeout(timeoutMs, operation) {
  const timeout = new Promise((_, reject) => {
    setTimeout(() => reject(new OperationTimeoutError('operation timed out')), timeoutMs)
  })
  return Promise.race([operation(), timeout])
}

export async function retry(maxAttempts, operation) {
  const attemptsLimit = Math.max(1, maxAttempts)
  let lastError = null

  for (let attempt = 1; attempt <= attemptsLimit; attempt++) {
    try {
      return { value: await operation(), attempts: attempt }
    } catch (error) {
      lastError = error
    }
  }

  throw lastError
}

export class CircuitBreaker {
  constructor(failureThreshold) {
    this.failureThreshold = Math.max(1, failureThreshold)
    this.consecutiveFailures = 0
    this.open = false
  }

  async execute(operation, fallback) {
    if (this.open) {
      throw new CircuitOpenError(fallback)
    }

    try {
      const value = await operation()
      this.consecutiveFailures = 0
      return value
    } catch {
      this.consecutiveFailures++
      if (this.consecutiveFailures >= this.failureThreshold) {
        this.open = true
      }
      return fallback
    }
  }

  reset() {
    this.consecutiveFailures = 0
    this.open = false
  }
}
