# idempotency-pattern (TypeScript)

## 模块说明

幂等模式的 TypeScript 实现。通过 OrderResponse 和 StoredResult 类型提供编译期安全。

## 设计模式应用

- **备忘录模式**：Record 存储首次执行结果，类型安全。
- **代理模式**：幂等检查包裹在业务逻辑之外。

## 代码结构

```
src/
  idempotency.ts        — OrderResponse 类型 + StoredResult 类型 + IdempotencyOrderService
test/
  test_idempotency.ts   — 验证三条路径
dist/                   — tsc 编译输出
```

## 与实际工程对比

| 维度 | 本示例 | NestJS + Redis |
|---|---|---|
| 类型 | TypeScript type | 运行时校验 |
| 存储 | 内存 Record | Redis + ioredis |
| 中间件 | 无 | NestJS 拦截器 |

## 测试验证

```bash
cd microservice-architecture/idempotency-pattern/ts
tsc -p .
node dist/test/test_idempotency.js
```
