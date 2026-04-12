# rate-limiting (JavaScript)

## 模块说明

固定窗口限流器的 JavaScript (ESM) 实现。

## 设计模式应用

- **策略模式**：固定窗口是一种限流策略。express-rate-limit 提供 Express 中间件集成。

## 代码结构

```
src/
  limiter.js        — FixedWindowLimiter（allow / advanceWindow）
test/
  test_limiter.js   — 验证放行/拒绝/窗口重置
```

## 测试验证

```bash
cd microservice-architecture/rate-limiting/js
node test/test_limiter.js
```
