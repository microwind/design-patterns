# outbox-pattern

该示例聚焦微服务中的可靠事件发布，统一演示 3 个动作：

1. 创建订单时同时写业务数据和 outbox 事件
2. relay 扫描 pending 事件并发布
3. 发布成功后标记为 published，避免重复发布

统一业务语境：

- 订单：`ORD-1001`
- 事件：`order_created`
- outbox 状态：`pending / published`

## 目标

- 说明为什么“写库成功 + 发消息失败”会产生不一致
- 说明 outbox 记录如何作为可靠发布缓冲
- 说明 relay 重跑时为什么不能重复发布已确认事件

## 语言实现

- c
- go
- java
- js
- python
- ts
