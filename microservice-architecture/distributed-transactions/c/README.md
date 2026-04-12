# distributed-transactions (C)

## 模块说明

分布式事务 Saga 模式的 C 语言实现。用结构体模拟服务，函数模拟正向步骤和补偿动作。

## 设计模式应用

- **命令模式**：reserve_stock / release_stock 是独立命令函数。
- **责任链模式**：saga_execute 用 if-else 链式编排步骤。

## 代码结构

```
src/
  func.h   — 头文件，定义 SagaOrder / SagaCoordinator 等结构体
  saga.c   — Saga 实现（init / execute / 内部 reserve / release）
test/
  test.c   — 验证成功/补偿流程，检查库存回滚
```

## 与实际工程对比

| 维度 | 本示例 | 实际 C/C++ |
|---|---|---|
| 编排 | if-else 链 | 状态机 / 工作流引擎 |
| 补偿 | 直接调用 release | 回调函数注册 + 逆序执行 |
| 持久化 | 内存 | 数据库 / 日志 |

## 测试验证

```bash
cd microservice-architecture/distributed-transactions/c
cc test/test.c src/*.c -o test.out
./test.out
```
