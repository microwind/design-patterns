# rate-limiting (Java)

## 模块说明

固定窗口限流器的 Java 实现。演示窗口内放行、超限拒绝、窗口推进重置的完整流程。

## 设计模式应用

- **策略模式（Strategy Pattern）**：固定窗口是一种限流策略。实际工程中 Sentinel 支持滑动窗口、Guava 提供令牌桶，可通过策略模式切换。

## 代码结构

```
src/
  FixedWindowLimiter.java  — 固定窗口限流器（allow / advanceWindow）
test/
  Test.java                — 验证放行/拒绝/窗口重置
```

## 与实际工程对比

| 维度 | 本示例 | Sentinel / Resilience4j |
|---|---|---|
| 算法 | 固定窗口 | 滑动窗口 / 令牌桶 |
| 线程安全 | 否 | AtomicInteger |
| 分布式 | 单进程 | Redis + Lua |

## 测试验证

```bash
cd microservice-architecture/rate-limiting/java
javac src/FixedWindowLimiter.java test/Test.java
java test.Test
```
