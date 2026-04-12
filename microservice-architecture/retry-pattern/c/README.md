# retry-pattern (C)

## 模块说明

重试模式的 C 语言实现。通过结构体 + 函数调用模拟重试策略。

## 设计模式应用

- **策略模式**：ScriptedOperation 结构体作为策略传入 retry_run。
- **模板方法模式**：retry_run 定义循环骨架。

## 代码结构

```
src/
  func.h    — ScriptedOperation 结构体和函数声明
  retry.c   — operation_init / operation_call / retry_run
test/
  test.c    — 验证成功重试和最终失败
```

## 测试验证

```bash
cd microservice-architecture/retry-pattern/c
cc test/test.c src/*.c -o test.out
./test.out
```
