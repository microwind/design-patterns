# Distributed Tracing

## 场景问题

微服务里最常见的一类排障问题是：

- 用户说“下单超时了”
- 网关有日志
- 订单服务也有日志
- 库存服务也有日志

但如果这些日志之间没有统一的 trace 上下文，就很难回答：

- 这几条日志是不是同一条请求
- 请求到底卡在了哪一跳
- 哪个服务是父调用，哪个服务是子调用

## 核心思想

分布式追踪的目标不是“多打一份日志”，而是给一条跨服务请求建立统一上下文：

- `traceId`：标识同一条调用链
- `spanId`：标识当前服务内的一个处理片段
- `parentSpanId`：标识当前片段的父调用

最基础的能力是“传播”：

1. 在入口生成 `traceId`
2. 服务间调用时透传 `traceId`
3. 每一跳创建自己的 `spanId`
4. 记录父子关系

## 结构与职责

```text
Gateway
  traceId=T-1001
  spanId=S-gateway
      |
      v
Order Service
  traceId=T-1001
  spanId=S-order
  parentSpanId=S-gateway
      |
      v
Inventory Service
  traceId=T-1001
  spanId=S-inventory
  parentSpanId=S-order
```

## 最小可运行示例

本专题的最小示例统一表达 3 个动作：

1. 网关收到请求后生成 `traceId`
2. 订单服务创建自己的 `spanId` 并继承 `traceId`
3. 库存服务继续继承 `traceId`，并把订单服务的 `spanId` 作为父节点

统一业务语境：

- 请求：`create order`
- trace：`TRACE-1001`
- spans：`SPAN-GATEWAY` / `SPAN-ORDER` / `SPAN-INVENTORY`

## 进阶演进

真实系统里，分布式追踪通常进一步演进为：

1. 自动埋点 HTTP / RPC / MQ
2. 采样策略
3. Span tag / attribute 标准化
4. 异步链路上的上下文续接
5. 对接 OpenTelemetry / Jaeger / Tempo / Zipkin

## 常见误区

1. 只打 traceId，不记录 span 层级
2. 入口生成了 traceId，但下游没有继续透传
3. 追踪上下文和业务日志字段不一致
4. 只接可视化平台，不先统一埋点模型
5. 异步消息不带 trace 上下文，导致链路断裂

## 推荐和哪些模块一起看

- `api-gateway`
- `service-communication`
- `resilience-patterns`

原因：

- 网关通常是 trace 上下文的入口
- 通信方式决定 trace 如何传播
- 韧性模式中的超时、重试、熔断都需要 trace 来定位问题
