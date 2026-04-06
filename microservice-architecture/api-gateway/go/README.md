# api-gateway (go)

当前目录提供一个最小 API Gateway 参考实现，演示 4 个基本能力：

- 路由分发
- 请求头鉴权
- 相关性 ID 透传
- 未知路由统一返回

## 目录结构

```text
go/
├── go.mod
├── src/
│   └── gateway.go
└── test/
    └── gateway_test.go
```

## 运行方式

```bash
cd microservice-architecture/api-gateway/go
go test ./...
```
