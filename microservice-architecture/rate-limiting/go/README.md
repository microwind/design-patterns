# rate-limiting (Go)

## 模块说明

固定窗口限流器的 Go 实现。

## 设计模式应用

- **策略模式**：固定窗口是一种限流策略。Go 标准扩展库 `golang.org/x/time/rate` 提供令牌桶实现。

## 代码结构

```
src/
  limiter.go        — FixedWindowLimiter（Allow / AdvanceWindow）
test/
  limiter_test.go   — Go 标准测试
```

## 与实际工程对比

| 维度 | 本示例 | uber-go/ratelimit |
|---|---|---|
| 算法 | 固定窗口 | 令牌桶（漏桶变种） |
| 并发 | 非线程安全 | goroutine 安全 |

## 测试验证

```bash
cd microservice-architecture/rate-limiting/go
go test ./...
```
