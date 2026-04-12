# retry-pattern (Go)

## 模块说明

重试模式的 Go 实现。使用 `func() bool` 函数参数作为重试策略。

## 设计模式应用

- **策略模式**：operation 函数参数可替换为任何操作。
- **模板方法模式**：Retry 定义循环骨架。

## 代码结构

```
src/
  retry.go        — Retry 函数 + ScriptedOperation
test/
  retry_test.go   — Go 标准测试
```

## 与实际工程对比

| 维度 | 本示例 | avast/retry-go |
|---|---|---|
| 退避 | 无 | 指数退避 + 抖动 |
| 错误分类 | bool | error 类型判断 |

## 测试验证

```bash
cd microservice-architecture/retry-pattern/go
go test ./...
```
