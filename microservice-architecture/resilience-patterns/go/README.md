# resilience-patterns (go)

当前目录提供一个最小韧性示例，演示：

- Timeout
- Retry
- Circuit Breaker
- Fallback

## 目录结构

```text
go/
├── go.mod
├── src/
│   └── resilience.go
└── test/
    └── resilience_test.go
```

## 运行方式

```bash
cd microservice-architecture/resilience-patterns/go
go test ./...
```
