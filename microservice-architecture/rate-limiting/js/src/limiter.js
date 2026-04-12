/**
 * @file limiter.js - 限流模式（Rate Limiting）的 JavaScript 实现
 *
 * 【设计模式】策略模式：固定窗口是一种限流策略。
 *
 * 【架构思想】限流保护系统不被过载流量拖垮。
 *
 * 【开源对比】
 *   - express-rate-limit：Express 限流中间件
 *   - rate-limiter-flexible：支持 Redis/内存的多策略限流
 *   本示例实现固定窗口，省略了时间窗口。
 */

/**
 * FixedWindowLimiter - 固定窗口限流器
 * 【设计模式】策略模式：固定窗口是一种限流策略。
 */
export class FixedWindowLimiter {
  /**
   * @param {number} limit 窗口内最大允许请求数
   */
  constructor(limit) {
    /** @type {number} 窗口上限 */
    this.limit = limit
    /** @type {number} 当前计数 */
    this.count = 0
  }

  /**
   * 判断是否允许通过。
   * @returns {boolean} true=放行，false=拒绝
   */
  allow() {
    if (this.count >= this.limit) {
      return false
    }
    this.count++
    return true
  }

  /** 推进窗口，重置计数。 */
  advanceWindow() {
    this.count = 0
  }
}
