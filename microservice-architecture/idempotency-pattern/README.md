# 【幂等模式详解】C/Java/JS/Go/Python/TS不同语言实现

# 简介

幂等模式（Idempotency Pattern）是微服务架构中保证"重复请求不产生重复副作用"的核心模式。在分布式环境中，由于网络重试、消息重复投递、用户重复提交等原因，同一个业务请求可能被执行多次。幂等模式通过**幂等键（Idempotency Key）**将重复请求折叠为同一结果，避免重复创建订单、重复扣款等问题。

# 作用

1. **防止重复执行**：同一幂等键的重复请求返回首次执行的结果，不再执行业务逻辑。
2. **冲突检测**：同一幂等键但参数不同的请求返回 CONFLICT，防止误用。
3. **安全重试**：调用方可以安全地重试失败请求，不用担心产生副作用。

# 实现步骤

1. 定义响应数据结构（OrderResponse），包含 replayed 标记区分首次/重复。
2. 实现幂等服务（IdempotencyOrderService），维护 idempotencyKey → (fingerprint, response) 的存储。
3. 每次请求先查找存储：
   - 未找到 → 执行业务逻辑，存储结果，返回 CREATED。
   - 找到且指纹匹配 → 返回存储的结果，标记 replayed=true。
   - 找到但指纹不匹配 → 返回 CONFLICT。

# 流程图

```text
请求到达（携带 idempotencyKey）
    │
    ▼
查找存储 ──未找到──► 执行业务逻辑 ──► 存储结果 ──► 返回 CREATED
    │
  已找到
    │
    ├── 指纹匹配 ──► 返回存储结果（replayed=true）
    │
    └── 指纹不匹配 ──► 返回 CONFLICT
```

# 涉及的设计模式

| 设计模式 | 在本模块中的体现 |
|---|---|
| **备忘录模式（Memento Pattern）** | 首次执行的结果被存储（备忘），后续重复请求直接返回备忘结果，不再重新执行。 |
| **代理模式（Proxy Pattern）** | 幂等层包裹在真实业务逻辑之外，对调用方透明地拦截重复请求。 |

# 与实际开源项目对比

| 对比维度 | 本示例 | 实际工程 |
|---|---|---|
| **存储** | 内存 Map/Dict | Redis（TTL 过期）、数据库唯一索引 |
| **键来源** | 调用方显式传入 | HTTP Header `Idempotency-Key`（Stripe 规范） |
| **过期策略** | 永不过期 | TTL 自动过期（如 24h），防止存储无限增长 |
| **并发控制** | 非线程安全 | 分布式锁 / CAS / 数据库唯一约束 |
| **指纹校验** | 字符串拼接对比 | 请求体哈希（SHA-256） |
| **持久化** | 内存（进程重启丢失） | Redis / 数据库持久化 |

> **整体思路一致**：幂等键 + 结果存储 + 指纹校验是所有实现的核心骨架。

# 代码

## Java 核心实现

```java
// 备忘录模式 —— 首次结果被存储，重复请求返回备忘结果
// 代理模式 —— 幂等层包裹业务逻辑，透明拦截重复请求
public static class IdempotencyOrderService {
    private final Map<String, StoredResult> store = new HashMap<>();

    public OrderResponse placeOrder(String idempotencyKey, String orderId,
            String sku, int quantity) { ... }
}
```

## Go 核心实现

```go
type IdempotencyOrderService struct {
    store map[string]storedResult
}
func (s *IdempotencyOrderService) PlaceOrder(idempotencyKey, orderID, sku string, qty int) OrderResponse { ... }
```

## Python 核心实现

```python
class IdempotencyOrderService:
    """幂等下单服务 —— 备忘录 + 代理模式"""
    def place_order(self, idempotency_key: str, order_id: str,
                    sku: str, quantity: int) -> OrderResponse: ...
```

## JavaScript 核心实现

```javascript
export class IdempotencyOrderService {
  placeOrder(idempotencyKey, orderId, sku, quantity) { ... }
}
```

## TypeScript 核心实现

```typescript
export class IdempotencyOrderService {
  placeOrder(idempotencyKey: string, orderId: string, sku: string, quantity: number): OrderResponse { ... }
}
```

## C 核心实现

```c
OrderResponse idempotency_place_order(IdempotencyService *svc,
    const char *idempotency_key, const char *order_id,
    const char *sku, int quantity);
```

# 测试验证

```bash
# Java
cd microservice-architecture/idempotency-pattern/java
javac src/IdempotencyPattern.java test/Test.java && java test.Test

# Go
cd microservice-architecture/idempotency-pattern/go
go test ./...

# Python
cd microservice-architecture/idempotency-pattern/python
python3 -m unittest discover -s test -p "test_*.py"

# JavaScript
cd microservice-architecture/idempotency-pattern/js
node test/test_idempotency.js

# TypeScript
cd microservice-architecture/idempotency-pattern/ts
tsc -p . && node dist/test/test_idempotency.js

# C
cd microservice-architecture/idempotency-pattern/c
cc test/test.c src/*.c -o test.out && ./test.out
```
