# 【重试模式详解】C/Java/JS/Go/Python/TS不同语言实现

# 简介

重试模式（Retry Pattern）是微服务架构中处理"临时性失败"的核心模式。网络抖动、服务瞬时过载等问题往往是暂时的，通过有限次数的重试即可恢复。但重试不能"无脑多试几次"——必须区分可重试和不可重试错误，控制最大重试次数，并与超时、幂等配合使用。

# 作用

1. **处理暂时性故障**：网络抖动、服务短暂不可用时，通过重试自动恢复。
2. **收敛控制**：限制最大重试次数，避免无限重试加重下游负担。
3. **结果明确**：最终返回成功或确认失败，调用方可据此决策。

# 实现步骤

1. 定义可调用操作接口，返回 true/false。
2. retry 函数循环调用操作，成功时立即返回。
3. 达到最大次数仍失败时返回失败结果。
4. ScriptedOperation 模拟"前 N 次失败，之后成功"的场景。

# 流程图

```text
  attempt = 1
    │
    ▼
  调用 operation()
    │
    ├── 成功 ──► 返回 {ok: true, attempts}
    │
    └── 失败 ──► attempt < max ? ──► attempt++ ──► 重试
                        │
                        └── 否 ──► 返回 {ok: false, attempts: max}
```

# 涉及的设计模式

| 设计模式 | 在本模块中的体现 |
|---|---|
| **策略模式（Strategy Pattern）** | operation 作为策略传入，可以是任何可调用对象。实际工程中还有指数退避、抖动等重试策略。 |
| **模板方法模式（Template Method）** | retry 定义了"循环 → 判断 → 继续/停止"的骨架，具体操作由调用方提供。 |

# 与实际开源项目对比

| 对比维度 | 本示例 | 实际工程 |
|---|---|---|
| **退避策略** | 无（立即重试） | 指数退避 + 随机抖动（Resilience4j / Polly / tenacity） |
| **可重试判断** | 所有失败都重试 | 区分可重试/不可重试异常 |
| **超时** | 无 | 每次重试独立超时 |
| **幂等** | 无 | 重试必须配合幂等键 |
| **异步** | 同步 | Promise / CompletableFuture |

> **整体思路一致**：循环调用 + 最大次数限制 + 结果判断是所有实现的核心骨架。

# 代码

## Java 核心实现

```java
// 模板方法 —— retry 定义循环骨架，操作由调用方提供
public static RetryResult retry(int maxAttempts, BooleanSupplier operation) {
    for (int attempt = 1; attempt <= maxAttempts; attempt++) {
        if (operation.getAsBoolean()) return new RetryResult(true, attempt);
    }
    return new RetryResult(false, maxAttempts);
}

// 策略模式 —— ScriptedOperation 模拟可编排的操作策略
public static class ScriptedOperation {
    public boolean call() { ... }
}
```

## Go 核心实现

```go
func Retry(maxAttempts int, operation func() bool) RetryResult { ... }
type ScriptedOperation struct { ... }
func (o *ScriptedOperation) Call() bool { ... }
```

## Python 核心实现

```python
def retry(max_attempts: int, operation: Callable[[], bool]) -> RetryResult: ...
class ScriptedOperation:
    def call(self) -> bool: ...
```

## JavaScript 核心实现

```javascript
export function retry(maxAttempts, operation) { ... }
export class ScriptedOperation {
  call() { ... }
}
```

## TypeScript 核心实现

```typescript
export function retry(maxAttempts: number, operation: () => boolean): RetryResult { ... }
export class ScriptedOperation {
  call(): boolean { ... }
}
```

## C 核心实现

```c
RetryResult retry(int max_attempts, int (*operation)(void *), void *ctx);
```

# 测试验证

```bash
# Java
cd microservice-architecture/retry-pattern/java
javac src/RetryPattern.java test/Test.java && java test.Test

# Go
cd microservice-architecture/retry-pattern/go
go test ./...

# Python
cd microservice-architecture/retry-pattern/python
python3 -m unittest discover -s test -p "test_*.py"

# JavaScript
cd microservice-architecture/retry-pattern/js
node test/test_retry.js

# TypeScript
cd microservice-architecture/retry-pattern/ts
tsc -p . && node dist/test/test_retry.js

# C
cd microservice-architecture/retry-pattern/c
cc test/test.c src/*.c -o test.out && ./test.out
```
