# 【弹性模式详解】C/Java/JS/Go/Python/TS不同语言实现

# 简介

弹性模式（Resilience Patterns）是微服务架构中保障系统在部分故障下仍能正常运行的核心模式集合。本示例统一演示三种核心弹性机制：**超时控制（Timeout）**、**重试（Retry）** 和 **熔断器（Circuit Breaker）**，展示如何在依赖不可靠时保护调用方。

# 作用

1. **超时控制**：防止调用方无限等待慢依赖，超时后立即失败并释放资源。
2. **重试**：在瞬时故障（网络抖动、临时不可用）时自动重试，提升成功率。
3. **熔断器**：连续失败达到阈值时"断开"电路，后续请求直接返回 fallback，防止故障级联。

# 实现步骤

1. **ScriptedDependency**：模拟不可靠依赖，按脚本返回成功/失败/延迟。
2. **callWithTimeout**：将操作提交到独立线程，通过 Future.get(timeout) 限制最长等待时间，超时抛出 OperationTimeoutException。
3. **retry**：循环执行操作最多 maxAttempts 次，成功即返回（附带尝试次数），全部失败抛出最后一个异常。
4. **CircuitBreaker**：维护 consecutiveFailures 计数器，达到 failureThreshold 后标记为 open；open 状态下直接抛出 CircuitOpenException，调用 reset() 可恢复。

# 流程图

```text
【超时控制】              【重试】                  【熔断器】
callWithTimeout()        retry(maxAttempts)        execute(operation)
    │                       │                         │
    ▼                       ▼                     open? ──是──► 抛出 CircuitOpenException
提交到线程池             attempt = 1                  │
    │                       │                        否
    ▼                       ▼                         │
Future.get(timeout)     执行 operation                ▼
    │                       │                     执行 operation
    ├── 超时 → 抛出异常     ├── 成功 → 返回            │
    │                       │                     ├── 成功 → failures=0, 返回
    └── 正常 → 返回结果     └── 失败 → attempt++       │
                               │                  └── 失败 → failures++
                               ▼                       │
                           attempt > max?              ▼
                               │                  failures ≥ threshold?
                           ├── 是 → 抛出异常          │
                           └── 否 → 重试          ├── 是 → open=true
                                                  └── 否 → 返回 fallback
```

# 涉及的设计模式

| 设计模式 | 在本模块中的体现 |
|---|---|
| **代理模式（Proxy Pattern）** | Timeout、Retry、CircuitBreaker 都包裹在真实操作之外，对调用方透明地增加保护逻辑。 |
| **状态模式（State Pattern）** | CircuitBreaker 在 closed / open 两种状态间切换，不同状态下 execute 行为完全不同。 |
| **模板方法模式（Template Method）** | retry 的循环结构是固定的"尝试→捕获→重试"模板，具体操作由调用方传入。 |

# 与实际开源项目对比

| 对比维度 | 本示例 | 实际工程 |
|---|---|---|
| **实现库** | 手写逻辑 | Resilience4j（Java）、Polly（.NET）、Hystrix（已停更） |
| **熔断状态** | closed / open 两态 | closed / open / half-open 三态（半开状态放行探测请求） |
| **退避策略** | 无（立即重试） | 指数退避 + 随机抖动（Exponential Backoff + Jitter） |
| **超时实现** | 独立线程 + Future | Netty 异步超时 / Context.WithTimeout (Go) |
| **监控** | 无 | Metrics 暴露（成功率、延迟分位、熔断次数） |
| **组合使用** | 单独演示 | Timeout → Retry → CircuitBreaker 链式组合 |

> **整体思路一致**：超时保护 + 自动重试 + 熔断兜底是所有弹性方案的核心骨架。

# 测试验证

```bash
# Java
cd microservice-architecture/resilience-patterns/java
javac src/ResiliencePatterns.java test/Test.java && java test.Test

# Go
cd microservice-architecture/resilience-patterns/go
go test ./...

# Python
cd microservice-architecture/resilience-patterns/python
python3 -m unittest discover -s test -p "test_*.py"

# JavaScript
cd microservice-architecture/resilience-patterns/js
node test/test_resilience.js

# TypeScript
cd microservice-architecture/resilience-patterns/ts
tsc -p . && node dist/test/test_resilience.js

# C
cd microservice-architecture/resilience-patterns/c
cc test/test.c src/*.c -o test.out && ./test.out
```
