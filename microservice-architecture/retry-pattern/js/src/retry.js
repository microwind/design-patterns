/**
 * @file retry.js - 重试模式（Retry Pattern）的 JavaScript 实现
 *
 * 【设计模式】
 *   - 策略模式：operation 作为函数参数传入 retry。
 *   - 模板方法模式：retry 定义了循环调用的骨架。
 *
 * 【架构思想】重试处理暂时性故障，但必须控制最大次数。
 *
 * 【开源对比】
 *   - p-retry（npm）：Promise 重试库，支持指数退避
 *   - async-retry（npm）：异步重试库
 *   本示例实现同步固定次数重试。
 */

/**
 * ScriptedOperation - 脚本化操作（测试辅助）
 * 模拟"前 N 次失败，之后成功"。
 */
export class ScriptedOperation {
  constructor(failuresBeforeSuccess) {
    this.failuresBeforeSuccess = failuresBeforeSuccess
    this.attempts = 0
  }

  /** 调用操作。前 failuresBeforeSuccess 次返回 false。 */
  call() {
    this.attempts++
    return this.attempts > this.failuresBeforeSuccess
  }
}

/**
 * 执行重试。循环调用操作，成功时立即返回。
 * @param {number} maxAttempts 最大尝试次数
 * @param {Function} operation 待重试操作（返回 true=成功）
 * @returns {{ ok: boolean, attempts: number }} 重试结果
 */
export function retry(maxAttempts, operation) {
  for (let attempt = 1; attempt <= maxAttempts; attempt++) {
    // 调用操作，成功则立即返回
    if (operation()) {
      return { ok: true, attempts: attempt }
    }
  }
  // 达到最大次数仍失败
  return { ok: false, attempts: maxAttempts }
}
