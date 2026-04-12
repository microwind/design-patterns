# circuit-breaker (JavaScript)

## 模块说明

断路器模式（Circuit Breaker Pattern）的 JavaScript (ESM) 实现。演示断路器在 closed / open / half-open 三种状态之间的转换逻辑。

## 设计模式应用

- **状态模式（State Pattern）**：断路器的行为随 state 属性变化。`recordFailure()` 仅在 closed 状态下累加失败，`probe()` 仅在 open 状态下触发探测。
- **代理模式（Proxy Pattern）**：断路器包裹在真实服务调用之外，调用方通过断路器间接访问下游服务。

## 代码结构

```
src/
  breaker.js        — 断路器状态机（CircuitBreaker 类，ESM 导出）
test/
  test_breaker.js   — 验证 closed → open → half-open → closed 完整状态转换
```

## 与实际工程对比

| 维度 | 本示例 | opossum (Node.js) |
|---|---|---|
| 失败判定 | 简单计数 | 滑动窗口失败率（errorThresholdPercentage） |
| 异步支持 | 同步方法 | Promise 包装，自动统计 resolve/reject |
| 定时恢复 | 外部手动 probe | resetTimeout 自动进入 half-open |
| 降级回调 | 无 | 支持 fallback 函数 |
| 事件系统 | 无 | EventEmitter 事件（open, close, halfOpen, fallback） |

> 整体思路一致：状态机骨架相同，opossum 在此基础上增加了 Promise 包装和事件系统。

## 测试验证

```bash
cd microservice-architecture/circuit-breaker/js
node test/test_breaker.js
```
