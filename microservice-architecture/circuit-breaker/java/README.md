# circuit-breaker (Java)

## 模块说明

断路器模式（Circuit Breaker Pattern）的 Java 实现。演示断路器在 closed / open / half-open 三种状态之间的转换逻辑。

## 设计模式应用

- **状态模式（State Pattern）**：断路器的行为随 state 字段变化。`recordFailure()` 仅在 closed 状态下累加失败，`probe()` 仅在 open 状态下触发探测。实际工程（如 Resilience4j）会将每种状态抽象为独立的状态类。
- **代理模式（Proxy Pattern）**：断路器包裹在真实服务调用之外，调用方通过断路器间接访问下游服务。

## 代码结构

```
src/
  CircuitBreakerPattern.java  — 断路器状态机实现（内部类 CircuitBreaker）
test/
  Test.java                   — 验证 closed → open → half-open → closed 完整状态转换
```

## 与实际工程对比

| 维度 | 本示例 | Resilience4j |
|---|---|---|
| 失败判定 | 简单计数 | 滑动窗口失败率 |
| 线程安全 | 否 | AtomicReference + CAS |
| 定时恢复 | 外部手动 probe | 内置定时器自动 half-open |
| 降级回调 | 无 | 支持 fallback |
| 可观测性 | 无 | Micrometer 指标集成 |

> 整体思路一致：状态机（closed → open → half-open → closed）是所有实现的核心骨架。

## 测试验证

```bash
cd microservice-architecture/circuit-breaker/java
javac src/CircuitBreakerPattern.java test/Test.java
java test.Test
```
