# api-versioning (go)

当前目录演示一个最小版本路由器：

- 默认版本回退
- 通过请求头选择版本
- V1 / V2 并存
- 不支持版本的错误响应

## 目录结构

```text
go/
├── go.mod
├── src/
│   └── router.go
└── test/
    └── router_test.go
```

## 运行方式

```bash
cd microservice-architecture/api-versioning/go
go test ./...
```
