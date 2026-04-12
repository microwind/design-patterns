# 【断路器模式详解】C/Java/JS/Go/Python/TS不同语言实现

# 简介

断路器模式（Circuit Breaker Pattern）是微服务架构中最核心的弹性设计模式之一。当下游服务持续失败时，如果调用方仍然不断发送请求，只会把故障继续放大，最终导致级联雪崩。断路器的目标是在连续失败达到阈值后**快速失败**，避免无意义的等待和资源浪费，同时通过探测机制在下游恢复后自动重新放行。

该模式借鉴了电气工程中断路器（保险丝）的思想：正常时闭合通电，过载时自动断开保护电路。

# 作用

1. **故障隔离**：当下游服务不可用时，快速返回失败，避免调用方长时间阻塞等待超时。
2. **防止级联故障**：阻止故障沿调用链传播，保护上游服务不被拖垮。
3. **自动恢复**：通过 half-open 探测机制，在下游恢复后自动切回正常调用，无需人工干预。

# 实现步骤

1. 定义断路器的三种状态：`closed`（闭合/正常）、`open`（断开/熔断）、`half-open`（半开/探测）。
2. 在 `closed` 状态下记录连续失败次数，当失败次数达到阈值时，状态转为 `open`。
3. 在 `open` 状态下，所有请求直接快速失败，不再调用下游。
4. 经过一段冷却时间（或由外部触发），尝试一次探测调用，状态转为 `half-open`。
5. 如果探测成功，状态恢复为 `closed`，失败计数清零；如果探测失败，状态回到 `open`。

# 状态机

```text
    ┌───────────────────────────────────┐
    │                                   │
    ▼                                   │
 CLOSED ──── 连续失败≥阈值 ────► OPEN ────┘
    ▲                                   │
    │                                   │
    └── 探测成功 ── HALF-OPEN ◄──────────┘
                       │        冷却后试探
                       │
                    探测失败 ───► OPEN
```

# 涉及的设计模式

| 设计模式 | 在本模块中的体现 |
|---|---|
| **状态模式（State Pattern）** | 断路器有 closed / open / half-open 三种状态，每种状态下行为不同（recordFailure 和 probe 的处理逻辑随状态变化）。实际工程中通常将每种状态抽象为独立的状态类。 |
| **代理模式（Proxy Pattern）** | 断路器包裹在真实的服务调用之外，对调用方透明地拦截请求。在 open 状态下直接返回失败，不将请求转发到下游。 |

# 与实际开源项目对比

| 对比维度 | 本示例 | 实际工程 |
|---|---|---|
| **状态管理** | 用字符串字段（`"closed"/"open"/"half-open"`）表示状态 | Resilience4j 使用枚举 + AtomicReference 保证线程安全；Hystrix 使用滑动窗口统计 |
| **失败判定** | 简单计数（连续失败次数） | Resilience4j 支持基于滑动窗口的失败率（如最近 10 次调用失败率 > 50%） |
| **冷却时间** | 由外部手动触发 probe | Resilience4j / Hystrix 内置定时器，open 状态持续 N 秒后自动进入 half-open |
| **half-open 探测** | 单次探测成功即恢复 | Resilience4j 允许配置 half-open 状态下允许的探测调用次数 |
| **线程安全** | 非线程安全（教学示例） | 生产级实现全部线程安全（CAS、锁或 Actor 模型） |
| **可观测性** | 无 | Resilience4j 集成 Micrometer 指标，Hystrix 集成 Dashboard 实时监控 |
| **降级策略** | 无降级（直接失败） | 支持 fallback 函数，在熔断时返回兜底结果 |

> **整体思路一致**：状态机（closed → open → half-open → closed）是所有实现的核心骨架。本示例省略了时间窗口、线程安全、降级回调等工程细节，聚焦于状态机转换逻辑本身。

# 代码

## Java 核心实现

```java
// 断路器状态机 —— 状态模式的简化实现
public class CircuitBreaker {
    private final int failureThreshold; // 失败阈值
    private int failures;               // 当前连续失败次数
    private String state = "closed";    // 当前状态

    // 记录一次失败，失败次数达到阈值时切换到 open
    public void recordFailure() { ... }

    // 在 open 状态下进行探测，成功则恢复 closed，失败则保持 open
    public void probe(boolean success) { ... }
}
```

## Go 核心实现

```go
// CircuitBreaker 实现断路器状态机
type CircuitBreaker struct {
    state            string // 当前状态：closed / open / half-open
    failures         int    // 连续失败计数
    failureThreshold int    // 触发熔断的失败阈值
}

func (b *CircuitBreaker) RecordFailure() { ... }
func (b *CircuitBreaker) Probe(success bool) { ... }
```

## Python 核心实现

```python
class CircuitBreaker:
    """断路器状态机：closed → open → half-open → closed"""
    def record_failure(self) -> None: ...
    def probe(self, success: bool) -> None: ...
```

## JavaScript 核心实现

```javascript
export class CircuitBreaker {
  // 记录失败，超过阈值后断路器打开
  recordFailure() { ... }
  // 探测调用，决定恢复或保持断开
  probe(success) { ... }
}
```

## TypeScript 核心实现

```typescript
export class CircuitBreaker {
  private failures = 0;
  state = "closed";
  recordFailure(): void { ... }
  probe(success: boolean): void { ... }
}
```

## C 核心实现

```c
typedef struct {
    int failure_threshold; // 失败阈值
    int failures;          // 连续失败计数
    char state[16];        // 当前状态字符串
} CircuitBreaker;

void breaker_record_failure(CircuitBreaker *breaker);
void breaker_probe(CircuitBreaker *breaker, int success);
```

# 测试验证

所有语言实现统一验证以下流程：
1. 初始状态为 `closed`
2. 连续失败 2 次后进入 `open`
3. 探测成功后恢复 `closed`
4. 再次失败 2 次后进入 `open`，探测失败后保持 `open`

```bash
# Java
cd microservice-architecture/circuit-breaker/java
javac src/CircuitBreakerPattern.java test/Test.java && java test.Test

# Go
cd microservice-architecture/circuit-breaker/go
go test ./...

# Python
cd microservice-architecture/circuit-breaker/python
python3 test/test_breaker.py

# JavaScript
cd microservice-architecture/circuit-breaker/js
node test/test_breaker.js

# TypeScript
cd microservice-architecture/circuit-breaker/ts
tsc -p . && node dist/test/test_breaker.js

# C
cd microservice-architecture/circuit-breaker/c
cc test/test.c src/*.c -o test.out && ./test.out
```
