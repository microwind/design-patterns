# 微服务示例索引

本目录的示例按“一个模块一个主题”的方式组织。

## 当前策略

- `C`：当前完整参考实现
- `Go`：当前完整参考实现
- `Python`：当前完整参考实现
- `TypeScript`：当前完整参考实现
- `Java`：当前完整参考实现
- `JavaScript`：当前完整参考实现

## 示例清单

| 模块 | 目标 | C | Go | Python | TypeScript | Java | JavaScript |
|---|---|---|---|---|---|---|---|
| `microservice-basics` | 服务拆分与契约调用 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| `api-gateway` | 统一入口、路由、鉴权、透传 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| `service-discovery` | 服务注册、发现、摘除、轮询选择 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| `configuration-center` | 配置集中管理、按环境加载、版本刷新 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| `idempotency-pattern` | 幂等键、重复请求折叠、冲突检测 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| `outbox-pattern` | 业务写入、outbox 记录、relay 发布 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| `distributed-transactions` | Saga 补偿、状态推进、失败回滚 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| `distributed-tracing` | traceId/spanId 传播、父子 span 关联 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| `rate-limiting` | 固定窗口限流、窗口推进、拒绝策略 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| `retry-pattern` | 可重试失败、最大次数、最终成功/失败 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| `circuit-breaker` | closed/open/half-open 状态机与恢复探测 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| `feature-flag` | 开关发布、按用户命中、默认策略 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| `cdc-pattern` | 变更日志、CDC connector、事件发布 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| `load-balancing` | 轮询、加权轮询、最少连接 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| `service-communication` | 同步调用与异步事件对比 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| `api-versioning` | V1/V2 并存与版本选择 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| `resilience-patterns` | 超时、重试、熔断、降级 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |

## 推荐运行顺序

1. `microservice-basics/go`
2. `service-discovery/go`
3. `configuration-center/go`
4. `idempotency-pattern/go`
5. `outbox-pattern/go`
6. `distributed-transactions/go`
7. `distributed-tracing/go`
8. `rate-limiting/go`
9. `retry-pattern/go`
10. `circuit-breaker/go`
11. `feature-flag/go`
12. `cdc-pattern/go`
13. `load-balancing/go`
14. `api-gateway/go`
15. `service-communication/go`
16. `api-versioning/go`
17. `resilience-patterns/go`

对照学习时，也可以直接切到对应的 `c/`、`python/`、`ts/`、`java/` 或 `js/` 目录运行同模块测试。
