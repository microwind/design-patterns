# circuit-breaker (Go)

## 模块说明

断路器模式（Circuit Breaker Pattern）的 Go 实现。演示断路器在 closed / open / half-open 三种状态之间的转换逻辑。

## 设计模式应用

- **状态模式（State Pattern）**：断路器的行为随 state 字段变化。`RecordFailure()` 仅在 closed 状态下累加失败，`Probe()` 仅在 open 状态下触发探测。
- **代理模式（Proxy Pattern）**：断路器包裹在真实服务调用之外，调用方通过断路器间接访问下游服务。

## 代码结构

```
src/
  breaker.go        — 断路器状态机（CircuitBreaker 结构体）
test/
  breaker_test.go   — Go 标准测试，验证完整状态转换流程
```

## 与实际工程对比

| 维度 | 本示例 | sony/gobreaker |
|---|---|---|
| 失败判定 | 简单计数 | Counts 结构体 + ReadyToTrip 回调 |
| 线程安全 | 否 | sync.Mutex 保护 |
| 定时恢复 | 外部手动 Probe | 内置 Timeout 定时器自动 half-open |
| 状态回调 | 无 | OnStateChange 回调通知 |
| 自定义判定 | 固定阈值 | 支持自定义 ReadyToTrip 函数 |

> 整体思路一致：状态机骨架相同，sony/gobreaker 在此基础上增加了线程安全和可配置性。

## 测试验证

```bash
cd microservice-architecture/circuit-breaker/go
go test ./...
```
