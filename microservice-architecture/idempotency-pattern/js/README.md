# idempotency-pattern (JavaScript)

## 模块说明

幂等模式的 JavaScript (ESM) 实现。使用 Map 存储幂等结果，展开运算符构造重放响应。

## 设计模式应用

- **备忘录模式**：Map 存储首次执行结果。
- **代理模式**：幂等检查包裹在业务逻辑之外。

## 代码结构

```
src/
  idempotency.js        — IdempotencyOrderService
test/
  test_idempotency.js   — 验证三条路径
```

## 与实际工程对比

| 维度 | 本示例 | Express + Redis |
|---|---|---|
| 存储 | 内存 Map | Redis SETNX + TTL |
| 中间件 | 无 | Express/Koa 中间件层 |

## 测试验证

```bash
cd microservice-architecture/idempotency-pattern/js
node test/test_idempotency.js
```
