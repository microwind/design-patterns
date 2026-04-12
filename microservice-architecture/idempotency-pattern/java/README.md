# idempotency-pattern (Java)

## 模块说明

幂等模式的 Java 实现。通过 idempotencyKey + fingerprint 实现重复请求折叠和冲突检测。

## 设计模式应用

- **备忘录模式（Memento Pattern）**：StoredResult 存储首次执行结果，后续重复请求直接返回。
- **代理模式（Proxy Pattern）**：幂等检查包裹在业务逻辑之外，调用方无需感知幂等机制。

## 代码结构

```
src/
  IdempotencyPattern.java  — IdempotencyOrderService + OrderResponse + StoredResult（内部类）
test/
  Test.java                — 验证首次/重复/冲突三条路径
```

## 与实际工程对比

| 维度 | 本示例 | Stripe / Spring Retry |
|---|---|---|
| 存储 | 内存 HashMap | Redis SETNX + TTL |
| 键来源 | 参数传入 | HTTP Header `Idempotency-Key` |
| 并发 | 非线程安全 | 分布式锁 / CAS |
| 过期 | 永不过期 | TTL 自动过期 |

## 测试验证

```bash
cd microservice-architecture/idempotency-pattern/java
javac src/IdempotencyPattern.java test/Test.java
java test.Test
```
