# distributed-tracing

该示例聚焦微服务中的链路追踪传播，统一演示：

1. 入口生成 `traceId`
2. 服务内创建 `spanId`
3. 下游服务继承 `traceId` 和 `parentSpanId`

统一业务语境：

- trace：`TRACE-1001`
- spans：`SPAN-GATEWAY` / `SPAN-ORDER` / `SPAN-INVENTORY`

## 目标

- 说明 traceId 如何把一条请求串起来
- 说明 spanId / parentSpanId 如何表示父子关系
- 说明为什么传播上下文是追踪系统的第一步

## 语言实现

- c
- go
- java
- js
- python
- ts
