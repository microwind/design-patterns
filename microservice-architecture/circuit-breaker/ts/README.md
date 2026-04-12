# circuit-breaker (TypeScript)

## 模块说明

断路器模式（Circuit Breaker Pattern）的 TypeScript 实现。利用 TypeScript 的类型系统提供了更好的类型安全（private 修饰符、参数类型标注），演示断路器状态机的核心转换逻辑。

## 设计模式应用

- **状态模式（State Pattern）**：断路器的行为随 state 属性变化。`recordFailure()` 仅在 closed 状态下累加失败，`probe()` 仅在 open 状态下触发探测。TypeScript 的 `private` 修饰符确保 failures 计数不被外部直接修改。
- **代理模式（Proxy Pattern）**：断路器包裹在真实服务调用之外，调用方通过断路器间接访问下游服务。

## 代码结构

```
src/
  breaker.ts        — 断路器状态机（CircuitBreaker 类）
test/
  test_breaker.ts   — 验证 closed → open → half-open → closed 完整状态转换
dist/               — tsc 编译输出目录
```

## 与实际工程对比

| 维度 | 本示例 | cockatiel (TS) |
|---|---|---|
| 类型安全 | 基础类型标注 | 完整泛型支持（`CircuitBreakerPolicy<T>`） |
| 异步支持 | 同步方法 | 原生 async/await + Policy.execute() |
| 定时恢复 | 外部手动 probe | halfOpenAfter 定时自动恢复 |
| 组合能力 | 单独使用 | 可与 Retry、Timeout、Bulkhead 组合 |
| 降级回调 | 无 | 支持 onFailure 和 fallback |

> 整体思路一致：状态机骨架相同，cockatiel 在此基础上增加了泛型包装和策略组合能力。

## 测试验证

```bash
cd microservice-architecture/circuit-breaker/ts
tsc -p .
node dist/test/test_breaker.js
```
