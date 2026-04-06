# api-gateway (js)

当前目录提供一个最小 API Gateway 示例，演示：

- 路由分发
- 请求头鉴权
- 相关性 ID 透传
- 未知路由统一返回

## 目录结构

```text
js/
├── package.json
├── src/
│   └── gateway.js
└── test/
    └── test_gateway.js
```

## 运行方式

```bash
cd microservice-architecture/api-gateway/js
node test/test_gateway.js
```
