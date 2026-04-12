/**
 * @file retry.ts - 重试模式（Retry Pattern）的 TypeScript 实现
 *
 * 【设计模式】
 *   - 策略模式：operation 作为 `() => boolean` 类型参数传入。
 *   - 模板方法模式：retry 定义了循环调用的骨架。
 *
 * 【架构思想】TypeScript 通过函数类型和返回值类型提供编译期安全。
 *
 * 【开源对比】
 *   - p-retry / cockatiel：TypeScript 重试库
 *   本示例实现同步固定次数重试。
 */

/** 脚本化操作（测试辅助），模拟"前 N 次失败，之后成功" */
export class ScriptedOperation {
  private attempts = 0;

  constructor(private readonly failuresBeforeSuccess: number) {}

  /** 调用操作。前 failuresBeforeSuccess 次返回 false。 */
  call(): boolean {
    this.attempts++;
    return this.attempts > this.failuresBeforeSuccess;
  }
}

/**
 * 执行重试。循环调用操作，成功时立即返回。
 * @param maxAttempts 最大尝试次数
 * @param operation   待重试操作
 * @returns 重试结果：{ ok, attempts }
 */
export function retry(maxAttempts: number, operation: () => boolean): { ok: boolean; attempts: number } {
  for (let attempt = 1; attempt <= maxAttempts; attempt++) {
    if (operation()) {
      return { ok: true, attempts: attempt };
    }
  }
  return { ok: false, attempts: maxAttempts };
}
