/**
 * @file breaker.ts - 断路器模式（Circuit Breaker Pattern）的 TypeScript 实现
 *
 * 【设计模式】
 *   - 状态模式（State Pattern）：断路器在 closed / open / half-open 三种状态下行为不同。
 *     TypeScript 版本利用类型系统提供了更好的类型安全（private 修饰符、参数类型标注）。
 *   - 代理模式（Proxy Pattern）：断路器包裹在真实服务调用之外，对调用方透明地拦截请求。
 *
 * 【架构思想】
 *   断路器防止调用方在下游故障时持续重试导致级联雪崩，通过探测机制实现自动恢复。
 *
 * 【开源对比】
 *   - opossum（Node.js/TS）：支持 TypeScript 类型定义，提供 Promise 包装、
 *     滑动窗口、fallback 降级、事件监听。
 *   - cockatiel（Node.js/TS）：原生 TypeScript 编写的弹性库，支持断路器 + 重试 + 超时。
 *   本示例省略了泛型包装、Promise 集成、事件回调等工程细节，聚焦于状态机核心。
 */

/**
 * CircuitBreaker - 断路器状态机
 *
 * 状态转换：
 *   closed → open（连续失败达到阈值）
 *   open → half-open → closed（探测成功）
 *   open → half-open → open（探测失败）
 */
export class CircuitBreaker {
  /** 当前连续失败次数（私有，外部不可直接访问） */
  private failures = 0;

  /** 当前状态：closed / open / half-open */
  state = "closed";

  /**
   * 创建断路器
   * @param failureThreshold - 连续失败多少次后触发熔断
   */
  constructor(private readonly failureThreshold: number) {}

  /**
   * 记录一次失败调用。
   * 仅在 closed 状态下生效：累加失败计数，达到阈值时切换到 open。
   */
  recordFailure(): void {
    // 只有闭合状态下才统计失败
    if (this.state === "closed") {
      this.failures++;
      // 失败次数达到阈值，打开断路器
      if (this.failures >= this.failureThreshold) {
        this.state = "open";
      }
    }
  }

  /**
   * 在 open 状态下进行一次探测调用。
   * 先转为 half-open，再根据探测结果决定最终状态。
   *
   * @param success - 探测调用是否成功
   *   true  → 恢复 closed，清零失败计数
   *   false → 回到 open，继续熔断
   */
  probe(success: boolean): void {
    // 非 open 状态下不需要探测
    if (this.state !== "open") return;
    // 进入半开状态，允许一次试探调用
    this.state = "half-open";
    if (success) {
      // 探测成功，恢复正常
      this.state = "closed";
      this.failures = 0;
    } else {
      // 探测失败，重新熔断
      this.state = "open";
    }
  }
}
