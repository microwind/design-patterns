# idempotency-pattern (Go)

## 模块说明

幂等模式的 Go 实现。通过 idempotencyKey + fingerprint 实现重复请求折叠和冲突检测。

## 设计模式应用

- **备忘录模式**：storedResult 存储首次执行结果。
- **代理模式**：幂等检查包裹在业务逻辑之外。

## 代码结构

```
src/
  idempotency.go        — IdempotencyOrderService + OrderResponse
test/
  idempotency_test.go   — Go 标准测试，验证三条路径
```

## 与实际工程对比

| 维度 | 本示例 | Redis + Go |
|---|---|---|
| 存储 | 内存 map | Redis SETNX + TTL |
| 并发 | 非线程安全 | sync.Mutex / Redis 原子操作 |
| 指纹 | 字符串拼接 | SHA-256 哈希 |

## 测试验证

```bash
cd microservice-architecture/idempotency-pattern/go
go test ./...
```
