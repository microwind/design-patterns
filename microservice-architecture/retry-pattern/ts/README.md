# retry-pattern (TypeScript)

## 模块说明

重试模式的 TypeScript 实现。通过 `() => boolean` 类型和返回值类型提供编译期安全。

## 设计模式应用

- **策略模式**：operation 类型参数。
- **模板方法模式**：retry 定义循环骨架。

## 代码结构

```
src/
  retry.ts        — retry 函数 + ScriptedOperation
test/
  test_retry.ts   — 验证
dist/             — tsc 编译输出
```

## 测试验证

```bash
cd microservice-architecture/retry-pattern/ts
tsc -p .
node dist/test/test_retry.js
```
