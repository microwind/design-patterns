/**
 * @file limiter.ts - 限流模式（Rate Limiting）的 TypeScript 实现
 *
 * 【设计模式】策略模式：固定窗口是一种限流策略。
 *
 * 【架构思想】TypeScript 的 private/readonly 保证 limit 不可被外部修改。
 *
 * 【开源对比】
 *   - rate-limiter-flexible：支持 TypeScript 类型定义
 *   本示例实现固定窗口，省略了时间窗口。
 */

/**
 * FixedWindowLimiter - 固定窗口限流器
 */
export class FixedWindowLimiter {
  /** 当前窗口已通过请求数 */
  private count = 0;

  /** @param limit 窗口内最大允许请求数 */
  constructor(private readonly limit: number) {}

  /** 判断是否允许通过。count < limit 时放行，否则拒绝。 */
  allow(): boolean {
    if (this.count >= this.limit) {
      return false;
    }
    this.count++;
    return true;
  }

  /** 推进窗口，重置计数。 */
  advanceWindow(): void {
    this.count = 0;
  }
}
