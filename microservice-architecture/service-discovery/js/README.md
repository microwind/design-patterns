# service-discovery (JavaScript)

## 模块说明

服务发现模式的 JavaScript (ESM) 实现。使用 Map 数据结构维护注册表。

## 设计模式应用

- **注册表模式（Registry Pattern）**：ServiceRegistry 用嵌套 Map 维护映射。
- **策略模式（Strategy Pattern）**：RoundRobinDiscoverer 封装轮询策略。

## 代码结构

```
src/
  registry.js        — ServiceRegistry + RoundRobinDiscoverer（ESM 导出）
test/
  test_registry.js   — 验证注册/摘除/轮询完整流程
```

## 与实际工程对比

| 维度 | 本示例 | consul (npm) |
|---|---|---|
| 存储 | 内存 Map | 远程 Consul 集群 |
| 健康检查 | 无 | TTL / HTTP 探测 |
| 异步 | 同步 | Promise / callback |

## 测试验证

```bash
cd microservice-architecture/service-discovery/js
node test/test_registry.js
```
