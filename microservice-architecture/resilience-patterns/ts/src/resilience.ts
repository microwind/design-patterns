/**
 * @file resilience.ts - 弹性模式组合（Resilience Patterns）的 TypeScript 实现
 *
 * 本模块将超时、重试、断路器三种弹性模式组合在一起，展示它们如何协同工作。
 *
 * 【设计模式】
 *   - 策略模式（Strategy Pattern）：超时、重试、断路器是可独立使用或组合的弹性策略。
 *     TypeScript 版本利用类型系统提供了更好的类型安全。
 *   - 代理模式（Proxy Pattern）：callWithTimeout / retry / CircuitBreaker.execute
 *     包裹在真实操作之外，透明地添加弹性行为。
 *   - 状态模式（State Pattern）：CircuitBreaker 在 closed/open 状态下行为不同。
 *
 * 【架构思想】
 *   超时防止无限等待，重试处理暂时性故障，断路器阻止级联雪崩。
 *
 * 【开源对比】
 *   - cockatiel：原生 TypeScript 的弹性库，支持策略组合（断路器+重试+超时）
 *   本示例省略了指数退避、泛型包装等工程细节，聚焦于三种模式的核心逻辑。
 */

export class OperationTimeoutError extends Error {}
export class CircuitOpenError extends Error {}

export type Result = {
  value?: string;
  error?: Error;
  delayMs?: number;
};

export class ScriptedDependency {
  private index = 0;

  constructor(private readonly results: Result[]) {}

  async call(): Promise<string> {
    if (this.results.length === 0) {
      throw new Error("no scripted result available");
    }
    const index = this.index < this.results.length ? this.index : this.results.length - 1;
    const result = this.results[index];
    this.index++;

    if (result.delayMs && result.delayMs > 0) {
      await new Promise((resolve) => setTimeout(resolve, result.delayMs));
    }
    if (result.error) {
      throw result.error;
    }
    return result.value ?? "";
  }
}

export async function callWithTimeout(timeoutMs: number, operation: () => Promise<string>): Promise<string> {
  const timeoutPromise = new Promise<string>((_, reject) => {
    setTimeout(() => reject(new OperationTimeoutError("operation timed out")), timeoutMs);
  });
  return Promise.race([operation(), timeoutPromise]);
}

export async function retry(
  maxAttempts: number,
  operation: () => Promise<string>
): Promise<{ value: string; attempts: number }> {
  const attemptsLimit = Math.max(1, maxAttempts);
  let lastError: Error | null = null;

  for (let attempt = 1; attempt <= attemptsLimit; attempt++) {
    try {
      return { value: await operation(), attempts: attempt };
    } catch (error) {
      lastError = error as Error;
    }
  }

  throw lastError;
}

export class CircuitBreaker {
  private consecutiveFailures = 0;
  private open = false;

  constructor(private readonly failureThreshold: number) {}

  async execute(operation: () => Promise<string>, fallback: string): Promise<string> {
    if (this.open) {
      throw new CircuitOpenError(fallback);
    }

    try {
      const value = await operation();
      this.consecutiveFailures = 0;
      return value;
    } catch {
      this.consecutiveFailures++;
      if (this.consecutiveFailures >= Math.max(1, this.failureThreshold)) {
        this.open = true;
      }
      return fallback;
    }
  }

  reset(): void {
    this.consecutiveFailures = 0;
    this.open = false;
  }
}
