# circuit-breaker (C)

## 模块说明

断路器模式（Circuit Breaker Pattern）的 C 语言实现。用结构体和函数模拟面向对象的断路器状态机，演示 closed / open / half-open 三种状态之间的转换逻辑。

## 设计模式应用

- **状态模式（State Pattern）**：通过 state 字符串字段和 strcmp 条件分支实现状态机。C 语言没有类和多态机制，因此用结构体 + 条件分支来模拟不同状态下的行为切换。
- **代理模式（Proxy Pattern）**：断路器函数包裹在真实服务调用之外，调用方通过 breaker 函数间接控制调用流程。

## 代码结构

```
src/
  func.h     — 头文件，定义 CircuitBreaker 结构体和函数声明
  breaker.c  — 断路器状态机实现（init / record_failure / probe）
test/
  test.c     — 验证 closed → open → half-open → closed 完整状态转换
```

## 与实际工程对比

| 维度 | 本示例 | Envoy (C++) / Nginx |
|---|---|---|
| 状态管理 | 字符串 + strcmp | 枚举 + 原子操作 |
| 失败判定 | 简单计数 | 多维度（连接数、请求数、重试次数、待处理请求） |
| 线程安全 | 否 | mutex / 原子操作保护 |
| 定时恢复 | 外部手动 probe | 事件循环 + 定时器自动恢复 |
| 部署方式 | 应用内嵌入 | Service Mesh sidecar 或反向代理 |

> 整体思路一致：状态机骨架相同，生产级 C/C++ 实现在此基础上增加了线程安全和多维度判定。

## 测试验证

```bash
cd microservice-architecture/circuit-breaker/c
cc test/test.c src/*.c -o test.out
./test.out
```
