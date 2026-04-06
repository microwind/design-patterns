# api-gateway (ts)

当前目录提供一个最小 API Gateway 示例，演示：

- 路由分发
- 请求头鉴权
- 相关性 ID 透传
- 未知路由统一返回

## 目录结构

```text
ts/
├── package.json
├── tsconfig.json
├── src/
│   └── gateway.ts
└── test/
    └── test_gateway.ts
```

## 运行方式

```bash
cd microservice-architecture/api-gateway/ts
tsc -p .
node dist/test/test_gateway.js
```
