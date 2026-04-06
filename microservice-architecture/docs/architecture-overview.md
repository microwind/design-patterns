# 微服务架构模式总览

## 1. 为什么需要这一类内容

经典设计模式解决的是“进程内对象如何协作”的问题，微服务解决的是“系统级能力如何拆分、通信、治理和演进”的问题。

当系统从单体走向多服务之后，团队会立刻遇到几类新问题：

- 服务地址会变，调用方如何找到被调服务
- 服务部署到不同环境时，配置如何统一管理
- 客户端重试或消息重复投递时，如何避免重复执行业务副作用
- 业务数据写入成功后，如何可靠地把事件发出去
- 跨服务操作执行到一半失败时，如何恢复到业务可接受状态
- 一条请求跨多个服务传播时，如何快速看清完整调用链
- 高峰流量和持续故障下，如何保护系统不被拖垮
- 功能、数据和事件如何以可控方式逐步放量和传播
- 同一服务有多个实例时，流量如何分发
- 契约升级时，旧客户端如何不被破坏
- 被调服务抖动时，是否会把整个调用链拖垮
- 该用同步调用还是异步消息

微服务专题就是围绕这些真实问题展开。

---

## 2. 十七个核心模块的关系

```text
microservice-basics
  -> service-discovery
  -> configuration-center
  -> idempotency-pattern
  -> outbox-pattern
  -> distributed-transactions
  -> distributed-tracing
  -> rate-limiting
  -> retry-pattern
  -> circuit-breaker
  -> feature-flag
  -> cdc-pattern
  -> load-balancing
  -> api-gateway
  -> service-communication
  -> api-versioning
  -> resilience-patterns
```

它们分别对应不同层面的职责：

- `microservice-basics`：服务边界与契约
- `service-discovery`：服务地址的动态获取
- `configuration-center`：配置的集中存储、按环境下发与运行时刷新
- `idempotency-pattern`：重复请求折叠、幂等键和副作用去重
- `outbox-pattern`：业务写入与事件发布解耦后的可靠发布
- `distributed-transactions`：跨服务步骤执行失败后的补偿与一致性收敛
- `distributed-tracing`：traceId/spanId 传播与调用链关联
- `rate-limiting`：高峰流量下的准入控制
- `retry-pattern`：失败后可控重试与收敛
- `circuit-breaker`：连续失败下的快速失败与恢复探测
- `feature-flag`：功能发布与代码发布解耦
- `cdc-pattern`：数据变更日志向事件流传播
- `load-balancing`：多个实例间的请求分发
- `api-gateway`：统一入口与横切能力
- `service-communication`：同步与异步调用方式
- `api-versioning`：接口兼容与演进
- `resilience-patterns`：稳定性保护

---

## 3. 一条典型调用链

```text
Client
  -> API Gateway
     -> Discover order-service
     -> Load config for order-service
     -> Load balance to one order instance
        -> order-service
           -> Check idempotency key
           -> Write order + outbox event
           -> Start saga / compensation flow
           -> Create child span
           -> Apply limit / retry / breaker policies
           -> Read feature flags
           -> Emit CDC-compatible state changes
           -> Discover inventory-service
           -> Load config for inventory-service
           -> Call inventory-service
           -> Retry / Timeout / Circuit Breaker
```

这个调用链已经足以说明，微服务不是“把单体拆成多个项目”这么简单。只要跨进程调用出现，系统设计重点就会从“类如何协作”转向“服务如何治理”。

---

## 4. 微服务不是目的，而是权衡结果

适合考虑微服务的场景：

- 业务边界清晰且演进速度不同
- 团队规模已经超过单体协作的舒适区
- 不同模块需要独立扩缩容
- 某些链路对性能、稳定性、部署节奏有明显差异

不适合一开始就微服务的场景：

- 业务边界还不清楚
- 团队规模很小
- 系统复杂度主要不在组织协作，而在业务本身
- 基础设施与治理能力不足

---

## 5. 本专题的示例设计方式

为了让目录真正可运行，本专题采用“统一业务语境 + 模块化示例”的方式：

- 统一业务语境：订单、库存、支付、配置、幂等键、事件发布、补偿事务、trace 上下文、限流阈值、功能开关、数据变更事件
- 统一代码目标：用最小代码表达一个清晰模式
- 统一验证方式：每个模块都能独立运行测试
- 统一学习节奏：先理解边界，再理解治理

示例使用统一业务语境，让不同模块之间可以自然衔接：从订单服务发现库存服务，到通过配置中心读取超时阈值和功能开关，再到用幂等键抵御重复提交、通过 outbox 和 CDC 传播数据变更、用 Saga 做补偿事务，并通过 trace 上下文把整个链路串起来，最后对远程调用加上限流、重试和断路保护。

---

## 6. 建议配套阅读

- `learning-path.md`
- `topics/microservice-basics.md`
- `topics/service-discovery.md`
- `topics/configuration-center.md`
- `topics/idempotency-pattern.md`
- `topics/outbox-pattern.md`
- `topics/distributed-transactions.md`
- `topics/distributed-tracing.md`
- `topics/rate-limiting.md`
- `topics/retry-pattern.md`
- `topics/circuit-breaker.md`
- `topics/feature-flag.md`
- `topics/cdc-pattern.md`
- `topics/resilience-patterns.md`
