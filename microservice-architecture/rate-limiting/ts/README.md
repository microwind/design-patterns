# rate-limiting (TypeScript)

## 模块说明

固定窗口限流器的 TypeScript 实现。private/readonly 保证 limit 不可被外部修改。

## 设计模式应用

- **策略模式**：固定窗口是一种限流策略。

## 代码结构

```
src/
  limiter.ts        — FixedWindowLimiter（allow / advanceWindow）
test/
  test_limiter.ts   — 验证
dist/               — tsc 编译输出
```

## 测试验证

```bash
cd microservice-architecture/rate-limiting/ts
tsc -p .
node dist/test/test_limiter.js
```
