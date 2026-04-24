# API 签名验证使用指南

## 概述

本项目实现了基于 SHA-256 的 API 签名验证机制，用于保护 API 接口安全。

## 签名机制

### 签名算法

**不带参数签名（WithParams.FALSE）**:
```
signSource = appCode + secretKey + path + timestamp
sign = SHA256(signSource)
```

**带参数签名（WithParams.TRUE）**:
```
signSource = param1=value1&param2=value2&... + appCode + secretKey + path + timestamp
sign = SHA256(signSource)
```

### 签名流程

1. 客户端准备请求参数
2. 按照签名算法生成签名
3. 在请求头中携带签名信息
4. 服务端验证签名有效性

## 请求头

| Header名称 | 必填 | 说明 | 示例 |
|-----------|------|------|------|
| Sign-appCode | 是 | 应用编码 | test-app |
| Sign-sign | 是 | 签名值 | abc123... |
| Sign-time | 是 | 时间戳(毫秒) | 1704358096000 |
| Sign-path | 否 | API路径 | /api/order/create |

## 使用示例

### 1. 不带参数签名（GET请求）

```bash
# 准备参数
appCode="test-app"
secretKey="test-secret-key-123"
path="/api/order/1"
timestamp=$(date +%s%3N)

# 生成签名
signSource="${appCode}${secretKey}${path}${timestamp}"
sign=$(echo -n "${signSource}" | openssl dgst -sha256 | awk '{print $2}')

# 发起请求
curl -X GET "http://localhost:8080/api/order/1" \
  -H "Sign-appCode: ${appCode}" \
  -H "Sign-sign: ${sign}" \
  -H "Sign-time: ${timestamp}"
```

### 2. 带参数签名（POST请求）

```bash
# 准备参数
appCode="test-app"
secretKey="test-secret-key-123"
path="/api/order/create"
timestamp=$(date +%s%3N)

# 请求参数（按字典序排序）
params="totalAmount=299.99&userId=1"

# 生成签名
signSource="${params}${appCode}${secretKey}${path}${timestamp}"
sign=$(echo -n "${signSource}" | openssl dgst -sha256 | awk '{print $2}')

# 发起请求
curl -X POST "http://localhost:8080/api/order/create" \
  -H "Content-Type: application/json" \
  -H "Sign-appCode: ${appCode}" \
  -H "Sign-sign: ${sign}" \
  -H "Sign-time: ${timestamp}" \
  -d '{"userId":1,"totalAmount":299.99}'
```

## 注解使用

### @RequireSign

标记需要签名验证的接口：

```java
// 类级别 - 整个Controller都需要签名
@RestController
@RequireSign
public class OrderController {
    // ...
}

// 方法级别 - 单个方法需要签名
@PostMapping("/create")
@RequireSign(withParams = WithParams.TRUE)
public ApiResponse<Order> createOrder() {
    // ...
}
```

### @IgnoreSignHeader

跳过签名验证：

```java
@PostMapping("/public")
@IgnoreSignHeader
public ApiResponse<?> publicMethod() {
    // 此方法不需要签名验证
}
```

## 配置说明

在 `application.yml` 中配置：

```yaml
sign:
  signature:
    ttl: 600000  # 签名有效期（毫秒），默认10分钟
    default-with-params: false  # 默认是否使用参数签名
  cached-body-path-patterns:  # 需要缓存请求体的路径
    - /api/order/**
    - /api/payment/**
```

## 应用密钥配置

在 `SignatureInterceptor.java` 中配置应用密钥：

```java
private static final Map<String, String> APP_SECRETS = new HashMap<>();
static {
    APP_SECRETS.put("test-app", "test-secret-key-123");
    APP_SECRETS.put("prod-app", "prod-secret-key-456");
}
```

生产环境建议从数据库或配置中心读取密钥。

## 错误码

| 错误信息 | 说明 |
|---------|------|
| 缺少必需的签名 header | 请求头缺少 Sign-appCode、Sign-sign 或 Sign-time |
| 时间戳格式错误 | Sign-time 不是有效的时间戳 |
| 签名已过期 | 请求时间超过有效期（默认10分钟） |
| 未知的应用编码 | appCode 不存在或无效 |
| 签名验证失败 | 签名计算结果不匹配 |

## 最佳实践

1. **密钥管理**: 不要在代码中硬编码密钥，使用配置中心或密钥管理系统
2. **HTTPS**: 生产环境必须使用 HTTPS 传输
3. **时间同步**: 确保客户端和服务端时间同步
4. **签名缓存**: 可以缓存最近的签名，防止重放攻击
5. **参数排序**: 带参数签名时，参数必须按字典序排序
