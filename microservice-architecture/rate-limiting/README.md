# 【限流模式详解】C/Java/JS/Go/Python/TS不同语言实现

# 简介

限流（Rate Limiting）是微服务架构中保护系统不被过载流量拖垮的核心模式。当高峰流量或恶意请求超过系统处理能力时，限流器通过**准入控制**拒绝超额请求，优先保护系统稳定性。本模块演示最基础的**固定窗口限流**算法。

# 作用

1. **过载保护**：在流量高峰时拒绝超额请求，防止系统被压垮。
2. **公平分配**：限制单个客户端/API 的请求频率，保证资源公平使用。
3. **窗口重置**：窗口推进后计数清零，恢复接受新请求。

# 实现步骤

1. 定义限流器（FixedWindowLimiter），包含 limit（窗口内最大请求数）和 count（当前计数）。
2. allow() 方法：count < limit 时放行并递增，否则拒绝。
3. advanceWindow() 方法：窗口推进时清零计数。

# 流程图

```text
  请求到达
    │
    ▼
  count < limit ?
    │
    ├── 是 ──► count++ ──► 放行（返回 true）
    │
    └── 否 ──► 拒绝（返回 false）

  窗口推进（定时器触发）
    │
    ▼
  count = 0（重置）
```

# 涉及的设计模式

| 设计模式 | 在本模块中的体现 |
|---|---|
| **策略���式（Strategy Pattern）** | 固定窗口是一种限流策略。实际工程中还有令牌桶、滑动窗口、漏桶等策略，可通过策略模式切换。 |

# 与实际开源项目对比

| 对比维度 | 本示例 | 实际工程 |
|---|---|---|
| **算法** | 固定窗口 | Sentinel（滑动窗口）、Resilience4j（令牌桶/滑动窗口）、Nginx（漏桶） |
| **窗口推进** | 手动调用 advanceWindow | 定时器自动推进 |
| **粒度** | 全局单一计数 | 按 API / 按用户 / 按 IP 多维度限流 |
| **分布式** | 单进程内存 | Redis + Lua 脚本实现分布式限流 |
| **线程安全** | 否 | AtomicInteger / Redis 原子操作 |
| **拒绝策略** | 返回 false | 返回 HTTP 429 + Retry-After Header |

> **整体思路一致**：计数 + 阈值判断 + 窗口重置是所有固定窗口实现的核心骨架。

# 代码

## Java 核心实现

```java
// 策略模式 —— 固定窗口是一种限流策略
public class FixedWindowLimiter {
    private final int limit;  // 窗口内最大请求数
    private int count;        // 当前窗口已消耗次数

    public boolean allow() { ... }      // 判断是否放行
    public void advanceWindow() { ... } // 推进到下一窗口
}
```

## Go 核心实现

```go
type FixedWindowLimiter struct {
    Limit int
    Count int
}
func (l *FixedWindowLimiter) Allow() bool { ... }
func (l *FixedWindowLimiter) AdvanceWindow() { ... }
```

## Python 核心实现

```python
class FixedWindowLimiter:
    """固定窗口限流器 —— 策略模式"""
    def allow(self) -> bool: ...
    def advance_window(self) -> None: ...
```

## JavaScript 核心实现

```javascript
export class FixedWindowLimiter {
  allow() { ... }
  advanceWindow() { ... }
}
```

## TypeScript 核心实现

```typescript
export class FixedWindowLimiter {
  allow(): boolean { ... }
  advanceWindow(): void { ... }
}
```

## C 核心实现

```c
typedef struct {
    int limit;
    int count;
} FixedWindowLimiter;

int limiter_allow(FixedWindowLimiter *limiter);
void limiter_advance_window(FixedWindowLimiter *limiter);
```

# 测试验证

```bash
# Java
cd microservice-architecture/rate-limiting/java
javac src/FixedWindowLimiter.java test/Test.java && java test.Test

# Go
cd microservice-architecture/rate-limiting/go
go test ./...

# Python
cd microservice-architecture/rate-limiting/python
python3 -m unittest discover -s test -p "test_*.py"

# JavaScript
cd microservice-architecture/rate-limiting/js
node test/test_limiter.js

# TypeScript
cd microservice-architecture/rate-limiting/ts
tsc -p . && node dist/test/test_limiter.js

# C
cd microservice-architecture/rate-limiting/c
cc test/test.c src/*.c -o test.out && ./test.out
```
