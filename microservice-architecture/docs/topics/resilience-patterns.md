# Resilience Patterns

## 场景问题

在分布式系统里，远程调用失败不是偶发现象，而是设计前提。

典型失败包括：

- 网络抖动
- 对端超时
- 瞬时过载
- 依赖服务雪崩

如果系统没有韧性设计，一次失败就可能沿调用链级联放大。

## 核心思想

韧性设计的目标不是“让调用永远成功”，而是：

- 快速失败
- 控制影响范围
- 尽量恢复
- 必要时优雅降级

常见组合包括：

- Timeout
- Retry
- Circuit Breaker
- Fallback

## 结构与职责

```text
Order Service
  -> Resilient Client
     - timeout
     - retry
     - circuit breaker
     - fallback
     -> Inventory Service
```

## 最小可运行示例

Go 示例中实现了：

- 带超时的调用包装
- 有次数限制的重试
- 失败阈值触发熔断
- 熔断打开后返回降级结果

参考目录：

- `examples/resilience-patterns/go`

## 进阶演进

1. 指数退避与抖动
2. 半开状态探测恢复
3. 区分可重试错误和不可重试错误
4. 基于指标自动调节熔断阈值

## 常见误区

1. 所有错误都重试
2. 不设超时，导致线程或协程被拖死
3. 熔断后没有降级策略
4. 只上熔断器，不看指标和告警
