# circuit-breaker (Python)

## 模块说明

断路器模式（Circuit Breaker Pattern）的 Python 实现。演示断路器在 closed / open / half-open 三种状态之间的转换逻辑。

## 设计模式应用

- **状态模式（State Pattern）**：断路器的行为随 state 属性变化。`record_failure()` 仅在 closed 状态下累加失败，`probe()` 仅在 open 状态下触发探测。
- **代理模式（Proxy Pattern）**：断路器包裹在真实服务调用之外，调用方通过断路器间接访问下游服务。

## 代码结构

```
src/
  __init__.py     — 包初始化文件
  breaker.py      — 断路器状态机（CircuitBreaker 类）
test/
  test_breaker.py — 验证 closed → open → half-open → closed 完整状态转换
```

## 与实际工程对比

| 维度 | 本示例 | pybreaker |
|---|---|---|
| 失败判定 | 简单计数 | fail_max + 异常类型过滤 |
| 线程安全 | 否 | threading.Lock 保护 |
| 定时恢复 | 外部手动 probe | reset_timeout 自动进入 half-open |
| 状态监听 | 无 | CircuitBreakerListener 回调 |
| 异常排除 | 无 | exclude 参数排除特定异常 |

> 整体思路一致：状态机骨架相同，pybreaker 在此基础上增加了线程安全和异常过滤。

## 测试验证

```bash
cd microservice-architecture/circuit-breaker/python
python3 -m unittest discover -s test -p "test_*.py"
```
