# retry-pattern (JavaScript)

## 模块说明

重试模式的 JavaScript (ESM) 实现。

## 设计模式应用

- **策略模式**：operation 函数参数可替换。
- **模板方法模式**：retry 定义循环骨架。

## 代码结构

```
src/
  retry.js        — retry 函数 + ScriptedOperation
test/
  test_retry.js   — 验证成功重试和最终失败
```

## 测试验证

```bash
cd microservice-architecture/retry-pattern/js
node test/test_retry.js
```
