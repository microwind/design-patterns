# rate-limiting (C)

## 模块说明

固定窗口限流器的 C 语言实现。结构体存储 limit 和 count。

## 设计模式应用

- **策略模式**：固定窗口是一种限流策略。Nginx limit_req 模块使用类似的漏桶算法。

## 代码结构

```
src/
  func.h     — FixedWindowLimiter 结构体和函数声明
  limiter.c  — 限流器实现（init / allow / advance_window）
test/
  test.c     — 验证放行/拒绝/窗口重置
```

## 测试验证

```bash
cd microservice-architecture/rate-limiting/c
cc test/test.c src/*.c -o test.out
./test.out
```
