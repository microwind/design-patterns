# service-discovery (TypeScript)

## 模块说明

服务发现模式的 TypeScript 实现。利用 `type` 和 `Record` 泛型提供编译期类型安全。

## 设计模式应用

- **注册表模式（Registry Pattern）**：ServiceRegistry 用 `Record<string, Record<string, ServiceInstance>>` 类型安全地维护映射。
- **策略模式（Strategy Pattern）**：RoundRobinDiscoverer 封装轮询策略，返回 `ServiceInstance | null` 联合类型。

## 代码结构

```
src/
  registry.ts        — ServiceInstance 类型 + ServiceRegistry + RoundRobinDiscoverer
test/
  test_registry.ts   — 验证注册/摘除/轮询完整流程
dist/                — tsc 编译输出
```

## 与实际工程对比

| 维度 | 本示例 | tRPC / consul |
|---|---|---|
| 类型安全 | Record + type | tRPC 端到端类型安全 |
| 存储 | 内存 Record | 远程注册中心 |
| 健康检查 | 无 | TTL / HTTP |

## 测试验证

```bash
cd microservice-architecture/service-discovery/ts
tsc -p .
node dist/test/test_registry.js
```
