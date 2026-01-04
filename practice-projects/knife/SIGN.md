# API 签名验证机制详细说明

## 目录
- [概述](#概述)
- [核心概念](#核心概念)
- [架构设计](#架构设计)
- [签名流程](#签名流程)
- [配置说明](#配置说明)
- [API 接口](#api-接口)
- [使用示例](#使用示例)
- [安全特性](#安全特性)
- [常见问题](#常见问题)

---

## 概述

本项目实现了一套完整的 API 签名验证机制，用于保护接口安全，防止数据篡改和重放攻击。签名机制基于 DDD（领域驱动设计）架构实现，具有高度的可扩展性和灵活性。

### 设计目标
- **安全性**：防止数据篡改、重放攻击、中间人攻击
- **灵活性**：支持多种签名算法、多种配置模式
- **可扩展性**：基于策略模式，易于扩展新的签名策略
- **易用性**：提供注解驱动的签名验证，简化开发

---

## 核心概念

### 1. 签名（Sign）
签名是根据应用标识、密钥、接口路径、时间戳等信息通过特定算法生成的字符串，用于验证请求的合法性和完整性。

### 2. 动态盐值（Dynamic Salt）
动态盐值是在签名生成前，根据接口固定盐值和时间戳动态生成的随机字符串，增强签名的安全性，防止签名被破解。

### 3. 应用标识（AppCode）
每个接入应用都有唯一的应用标识码，用于识别调用方身份。

### 4. 密钥（SecretKey）
每个应用都有对应的密钥，用于签名的生成和验证。密钥不能在网络中传输，只能在签名计算时使用。

### 5. 签名模式

- **不带参数签名（withParams = false）**：签名计算不包含请求参数，使用 SHA-256 算法
  - 适用场景：GET 请求、参数不敏感的接口
  - 签名源：`appCode + secretKey + path + timestamp`

- **带参数签名（withParams = true）**：签名计算包含请求参数，使用 SM3 算法
  - 适用场景：POST/PUT 请求、参数敏感的接口
  - 签名源：`param1=value1&param2=value2&... + appCode + secretKey + timestamp`
  - 参数按 ASCII 字典序排序

**是否采用参数签名，由API的注解指定。**

---

## 架构设计

### 分层架构

```
┌─────────────────────────────────────────────────────────┐
│                   接口层（Interfaces）                    │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │ @RequireSign │  │SignController│  │ SignAdvice   │  │
│  │  注解定义     │  │  REST API    │  │  切面增强    │  │
│  └──────────────┘  └──────────────┘  └──────────────┘  │
└─────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────┐
│                  中间件层（Middleware）                   │
│  ┌──────────────────────────────────────────────────┐  │
│  │        SignatureInterceptor（签名拦截器）          │  │
│  │  - 拦截 @RequireSign 注解的接口                    │  │
│  │  - 提取 Header 签名信息                            │  │
│  │  - 调用应用层服务进行验证                          │  │
│  └──────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────┐
│                  应用层（Application）                    │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │  SignService │  │DynamicSalt   │  │SignValidation│  │
│  │  签名核心服务 │  │  Service     │  │   Service    │  │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘  │
│         │                  │                  │          │
│  ┌──────▼──────────────────▼──────────────────▼───────┐ │
│  │              策略模式（Strategy Pattern）           │ │
│  │  ┌────────────┐ ┌─────────────┐ ┌───────────────┐ │ │
│  │  │SecretKey   │ │DynamicSalt  │ │InterfaceSalt  │ │ │
│  │  │Strategy    │ │Strategy     │ │Strategy       │ │ │
│  │  │ - Local    │ │ - Local     │ │ - Local       │ │ │
│  │  │ - JPA      │ │ - JPA       │ │ - JPA         │ │ │
│  │  │ - JDBC     │ │ - JDBC      │ │ - JDBC        │ │ │
│  │  └────────────┘ └─────────────┘ └───────────────┘ │ │
│  └─────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────┐
│                    领域层（Domain）                       │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │     Sign     │  │  DynamicSalt │  │SignUserAuth  │  │
│  │  签名领域对象 │  │  动态盐值对象 │  │  用户授权    │  │
│  └──────────────┘  └──────────────┘  └──────────────┘  │
│  ┌─────────────────────────────────────────────────┐   │
│  │    SignDomainService（签名领域服务）             │   │
│  │    - 签名生成算法                                 │   │
│  │    - 签名验证算法                                 │   │
│  └─────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────┐
│                 基础设施层（Infrastructure）              │
│  ┌──────────────────────────────────────────────────┐  │
│  │         SignRepositoryImpl（仓储实现）            │  │
│  │  - 数据库访问                                      │  │
│  │  - 缓存管理                                        │  │
│  └──────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────┘
                            ↓
                      ┌──────────┐
                      │ Database │
                      └──────────┘
```

### 核心组件说明

#### 1. 接口层（Interfaces）
- **@RequireSign 注解**：标记需要签名验证的 Controller 或方法
- **@IgnoreSignHeader 注解**：跳过签名验证（优先级最高）
- **@WithParams 枚举**：控制是否包含请求参数进行签名
- **SignController**：提供签名相关的 REST API
- **SignHeaderAdvice**：自动绑定签名 Header 参数到方法参数

#### 2. 中间件层（Middleware）
- **SignatureInterceptor**：签名验证拦截器
  - 拦截所有带有 @RequireSign 注解的接口
  - 提取 Header 中的签名信息
  - 调用应用层服务进行签名验证
  - 验证失败返回 401 错误

- **CachedBodyFilter**：请求体缓存过滤器
  - 缓存请求体，解决流只能读取一次的问题
  - 支持多次读取请求体（拦截器和 Controller 都需要读取）

#### 3. 应用层（Application）
- **SignService**：签名核心服务
  - 签名生成
  - 签名验证
  - 应用权限验证

- **DynamicSaltService**：动态盐值服务
  - 动态盐值生成
  - 动态盐值验证

- **SignValidationService**：签名验证服务
  - 参数完整性校验
  - 时效性验证
  - 路径一致性校验

- **策略模式实现**：
  - **SecretKeyStrategy**：密钥获取策略（本地配置 / JPA / JDBC）
  - **DynamicSaltStrategy**：动态盐值策略（本地配置 / JPA / JDBC）
  - **InterfaceSaltStrategy**：接口盐值策略（本地配置 / JPA / JDBC）

#### 4. 领域层（Domain）
- **Sign**：签名领域对象，封装签名相关的业务逻辑
- **DynamicSalt**：动态盐值领域对象
- **SignUserAuth**：签名用户授权信息（应用标识、密钥、权限路径）
- **SignDomainService**：签名领域服务，实现签名生成和验证的核心算法

#### 5. 基础设施层（Infrastructure）
- **SignRepositoryImpl**：签名仓储实现，负责数据库访问和缓存管理

---

## 签名流程

### 完整流程图

```
┌─────────┐                                              ┌─────────┐
│ 客户端  │                                              │ 服务端  │
└────┬────┘                                              └────┬────┘
     │                                                         │
     │  1. 请求动态盐值（可选）                                │
     │  POST /api/sign/dynamic-salt-generate                  │
     │  Header: Sign-appCode=APP001                           │
     │          Sign-path=/api/orders                         │
     ├────────────────────────────────────────────────────────>│
     │                                                         │
     │                                      2. 验证应用权限    │
     │                                      3. 获取接口盐值    │
     │                                      4. 生成动态盐值    │
     │                                      5. 保存到数据库（可选）│
     │                                                         │
     │  Response: { dynamicSalt, dynamicSaltTime }            │
     │<────────────────────────────────────────────────────────┤
     │                                                         │
     │  6. 请求签名                                            │
     │  POST /api/sign/generate                               │
     │  Header: Sign-appCode=APP001                           │
     │          Sign-path=/api/orders                         │
     │          Sign-dynamicSalt=abc123...                    │
     │          Sign-dynamicSaltTime=1650713278548            │
     │          Sign-withParams=false                         │
     ├────────────────────────────────────────────────────────>│
     │                                                         │
     │                                      7. 验证动态盐值    │
     │                                      8. 获取应用密钥    │
     │                                      9. 生成签名        │
     │                                                         │
     │  Response: { sign, time, expireTime }                  │
     │<────────────────────────────────────────────────────────┤
     │                                                         │
     │  10. 携带签名访问受保护接口                            │
     │  POST /api/orders                                      │
     │  Header: Sign-appCode=APP001                           │
     │          Sign-sign=def456...                           │
     │          Sign-time=1650713280000                       │
     │          Sign-path=/api/orders                         │
     │  Body: { "data": "business data" }                     │
     ├────────────────────────────────────────────────────────>│
     │                                      ┌─────────────────┐│
     │                                      │SignatureInterceptor│
     │                                      │11. 拦截请求     ││
     │                                      │12. 提取Header   ││
     │                                      │13. 验证签名     ││
     │                                      │14. 时效性检查   ││
     │                                      │15. 路径校验     ││
     │                                      └─────────────────┘│
     │                                                         │
     │                                      16. 执行业务逻辑   │
     │                                                         │
     │  Response: { "code": 200, "data": {...} }              │
     │<────────────────────────────────────────────────────────┤
     │                                                         │
```

### 详细步骤说明

#### 步骤 1-5：动态盐值生成（可选）
动态盐值是一个可选的安全增强机制，推荐在高安全性要求的场景下使用。

1. **客户端请求动态盐值**
   - 发送 appCode 和 path 到服务端
   - path 必须是接口的模板路径（如 `/api/orders` 或 `/api/orders/{orderId}`）

2. **服务端验证应用权限**
   - 检查 appCode 是否合法
   - 检查该应用是否有权限访问指定路径

3. **获取接口固定盐值**
   - 根据 path 从配置或数据库中获取接口固定盐值（interfaceSalt）
   - 如果不存在，使用默认盐值

4. **生成动态盐值**
   - 算法：`SM3(appCode + path + interfaceSalt + timestamp)`
   - 返回动态盐值和时间戳

5. **保存到数据库（可选）**
   - 如果配置了 `validate-dynamic-salt-from-database: true`
   - 将动态盐值保存到数据库，用于后续验证
   - 防止重放攻击（每个盐值只能使用一次）

#### 步骤 6-9：签名生成

1. **客户端请求签名**
   - 携带 appCode、path、dynamicSalt（可选）、dynamicSaltTime（可选）
   - 通过 `Sign-withParams` Header 指定是否携带参数

2. **服务端验证动态盐值**
   - 如果提供了动态盐值，先验证其有效性
   - 检查动态盐值是否过期（TTL 默认 24 小时）
   - 数据库模式下，检查盐值是否已使用

3. **获取应用密钥**
   - 根据配置模式（本地配置 / 数据库）获取应用的 secretKey
   - 使用策略模式，支持多种获取方式

4. **生成签名**
   - 不带参数：`SHA256(appCode + secretKey + path + timestamp)`
   - 带参数：`SM3(param1=value1&param2=value2&... + appCode + secretKey + timestamp)`
   - 返回签名、时间戳、过期时间

#### 步骤 10-16：携带签名访问受保护接口

1. **客户端携带签名访问接口**
   - Header 中包含 Sign-appCode、Sign-sign、Sign-time、Sign-path
   - 如果是带参数签名，Body 中的参数必须与签名时一致

2. **SignatureInterceptor 拦截请求**
   - 检查方法或类上是否有 @RequireSign 注解
   - 如果有 @IgnoreSignHeader 注解，跳过验证

3. **提取 Header 签名信息**
   - 提取 appCode、sign、time、path

4. **验证签名**
   - 调用 SignService 进行签名验证
   - 根据 @RequireSign 的 withParams 参数决定验证算法

5. **时效性检查**
   - 检查签名是否在有效期内（TTL 默认 30 分钟）
   - 防止重放攻击

6. **路径校验**
   - 验证客户端提供的 path 与服务端实际路径是否一致
   - 防止签名被滥用到其他接口

7. **执行业务逻辑**
   - 验证通过后，继续执行 Controller 的业务逻辑

---

## 配置说明

### application.yml 配置

```yaml
sign:
  # 配置模式：database（数据库） / local（本地配置文件）
  config-mode: database

  # 数据库操作方式：jpa / jdbc
  repository-type: jdbc

  # 动态盐值生成路径
  dynamic-salt-generate-path: "/api/sign/dynamic-salt-generate"

  # 签名生成路径
  sign-generate-path: "/api/sign/generate"

  # 需要缓存请求体的路径模式列表（使用 Ant 风格路径模式）
  cached-body-path-patterns:
    - "/api/**"  # 缓存所有 /api/ 下的请求

  # 动态盐值配置
  dynamic-salt:
    # 动态盐值有效期（毫秒），默认24小时
    ttl: 86400000
    # 是否使用数据库校验动态盐值
    # true: 每次生成都存入数据库，验证时从数据库查询并标记为已使用
    # false: 仅通过算法校验，不存储到数据库
    validate-from-database: false

  # 签名配置
  signature:
    # 签名有效期（毫秒），默认30分钟
    ttl: 1800000
    # 默认是否使用参数签名
    # false: 签名计算不包含请求参数（使用 SHA-256 算法）
    # true: 签名计算包含请求参数（使用 SM3 算法）
    default-with-params: false

  # 本地配置模式下的应用信息（config-mode: local 时生效）
  local-apps:
    - app-code: APP001
      secret-key: your_secret_key_1
      permit-paths: /api/orders/**,/api/users/**
      forbidden-paths: /api/admin/**
    - app-code: APP002
      secret-key: your_secret_key_2
      permit-paths: /api/**
```

### 配置说明

#### 1. config-mode（配置模式）
- **local**：从配置文件读取应用信息（适用于开发环境）
- **database**：从数据库读取应用信息（适用于生产环境）

#### 2. repository-type（仓储类型）
- **jpa**：使用 JPA 访问数据库（推荐）
- **jdbc**：使用 JDBC 访问数据库（性能更好）

#### 3. cached-body-path-patterns（请求体缓存路径）
配置哪些路径需要缓存请求体。签名验证需要读取请求体，但 HTTP 流只能读取一次，因此需要缓存。

#### 4. dynamic-salt.ttl（动态盐值有效期）
动态盐值的有效时间，过期后需要重新生成。默认 24 小时。

#### 5. dynamic-salt.validate-from-database（数据库校验）
- **true**：动态盐值保存到数据库，验证时从数据库查询，防止重放攻击
- **false**：动态盐值仅通过算法校验，不存储到数据库

#### 6. signature.ttl（签名有效期）
签名的有效时间，过期后需要重新生成。默认 30 分钟。

#### 7. signature.default-with-params（默认参数签名）
- **false**：默认不携带参数签名（SHA-256）
- **true**：默认携带参数签名（SM3）
- 可以在 @RequireSign 注解中覆盖此默认值

---

## API 接口

### 1. 动态盐值生成

**接口**：`POST /api/sign/dynamic-salt-generate`

**功能**：为指定应用和接口生成动态盐值

**请求 Header**：
```
Sign-appCode: APP001
Sign-path: /api/orders
```

**请求 Body（可选）**：
```json
{
  "appCode": "APP001",
  "path": "/api/orders"
}
```

**响应**：
```json
{
  "code": 200,
  "message": "动态盐值生成成功",
  "data": {
    "appCode": "APP001",
    "path": "/api/orders",
    "dynamicSalt": "abc123def456...",
    "dynamicSaltTime": 1650713278548
  }
}
```

---

### 2. 动态盐值校验

**接口**：`POST /api/sign/dynamic-salt-validate`

**功能**：验证客户端提供的动态盐值是否有效

**请求 Header**：
```
Sign-appCode: APP001
Sign-path: /api/orders
Sign-dynamicSalt: abc123def456...
Sign-dynamicSaltTime: 1650713278548
```

**请求 Body**：
```json
{
  "appCode": "APP001",
  "path": "/api/orders",
  "dynamicSalt": "abc123def456...",
  "dynamicSaltTime": 1650713278548
}
```

**响应**：
```json
{
  "code": 200,
  "message": "动态盐值校验成功",
  "data": {
    "isValid": true,
    "dynamicSalt": {
      "appCode": "APP001",
      "path": "/api/orders",
      "dynamicSalt": "abc123def456...",
      "dynamicSaltTime": 1650713278548
    }
  }
}
```

---

### 3. 签名生成

**接口**：`POST /api/sign/generate`

**功能**：为已通过动态盐值校验的请求生成签名

**请求 Header**：
```
Sign-appCode: APP001
Sign-path: /api/orders
Sign-dynamicSalt: abc123def456...
Sign-dynamicSaltTime: 1650713278548
Sign-withParams: false
```

**请求 Body（可选，带参数签名时需要）**：
```json
{
  "userId": "123",
  "orderName": "测试订单",
  "amount": 100.50
}
```

**响应**：
```json
{
  "code": 200,
  "message": "签名生成成功",
  "data": {
    "appCode": "APP001",
    "path": "/api/orders",
    "sign": "def456ghi789...",
    "time": 1650713280000,
    "expireTime": 1650714080000
  }
}
```

---

### 4. 签名校验

**接口**：`POST /api/sign/sign-validate`

**功能**：验证客户端提供的签名是否有效

**请求 Header**：
```
Sign-appCode: APP001
Sign-path: /api/orders
Sign-sign: def456ghi789...
Sign-time: 1650713280000
Sign-withParams: false
```

**请求 Body（可选，带参数签名时需要）**：
```json
{
  "userId": "123",
  "orderName": "测试订单",
  "amount": 100.50
}
```

**响应**：
```json
{
  "code": 200,
  "message": "签名校验成功",
  "data": {
    "isValid": true,
    "sign": {
      "appCode": "APP001",
      "path": "/api/orders",
      "sign": "def456ghi789...",
      "time": 1650713280000
    }
  }
}
```

---

### 5. 受保护接口访问

**接口**：任何标记了 @RequireSign 的接口

**功能**：访问需要签名验证的业务接口

**请求 Header**：
```
Sign-appCode: APP001
Sign-sign: def456ghi789...
Sign-time: 1650713280000
Sign-path: /api/orders
```

**请求 Body**：
```json
{
  "userId": "123",
  "orderName": "测试订单",
  "amount": 100.50
}
```

**响应**：
```json
{
  "code": 200,
  "message": "请求成功",
  "data": {
    // 业务数据
  }
}
```

---

## 使用示例

### 示例 1：在 Controller 类上使用 @RequireSign

```java
@RequireSign  // 整个 Controller 的所有方法都需要签名验证
@RestController
@RequestMapping("/api/users")
public class UserController {

    // 该方法需要签名验证（使用配置文件的默认 withParams）
    @PostMapping("/create")
    public ApiResponse<UserDTO> create(@RequestBody UserDTO dto) {
        // 业务逻辑
        return ApiResponse.success(dto);
    }

    // 该方法需要签名验证
    @GetMapping("/list")
    public ApiResponse<List<UserDTO>> list() {
        // 业务逻辑
        return ApiResponse.success(users);
    }

    // 跳过签名验证（优先级最高）
    @IgnoreSignHeader
    @GetMapping("/public-info")
    public ApiResponse<String> publicInfo() {
        return ApiResponse.success("公开信息");
    }
}
```

---

### 示例 2：在方法上使用 @RequireSign

```java
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    // 该方法需要签名验证，不带参数（使用 SHA-256 算法）
    @RequireSign(withParams = WithParams.FALSE)
    @GetMapping("/list")
    public ApiResponse<List<OrderDTO>> list() {
        // 业务逻辑
        return ApiResponse.success(orders);
    }

    // 该方法需要签名验证，带参数（使用 SM3 算法）
    @RequireSign(withParams = WithParams.TRUE)
    @PostMapping("/create")
    public ApiResponse<OrderDTO> create(@RequestBody OrderDTO dto) {
        // 业务逻辑
        // 签名会验证 dto 参数的完整性
        return ApiResponse.success(dto);
    }

    // 该方法不需要签名验证
    @GetMapping("/public/{orderId}")
    public ApiResponse<OrderDTO> getPublicOrder(@PathVariable String orderId) {
        // 业务逻辑
        return ApiResponse.success(order);
    }
}
```

---

### 示例 3：获取签名 Header 参数

```java
@RequireSign
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @PostMapping("/submit")
    public ApiResponse<Map<String, Object>> submit(
            @ModelAttribute("SignHeaders") SignHeaderRequest signHeaders,
            @RequestBody Map<String, Object> parameters) {

        // 通过 @ModelAttribute("SignHeaders") 自动绑定签名 Header
        log.info("应用标识: {}", signHeaders.getAppCode());
        log.info("签名值: {}", signHeaders.getSign());
        log.info("时间戳: {}", signHeaders.getTime());
        log.info("路径: {}", signHeaders.getPath());

        // 业务逻辑
        return ApiResponse.success(parameters);
    }
}
```

---

### 示例 4：客户端调用流程（Java）

```java
import java.net.http.*;
import java.net.URI;
import java.util.*;

public class SignatureClient {

    private static final String BASE_URL = "http://localhost:8080";
    private static final String APP_CODE = "APP001";
    private static final HttpClient httpClient = HttpClient.newHttpClient();

    public static void main(String[] args) throws Exception {
        // 1. 生成动态盐值（可选）
        Map<String, Object> saltResponse = generateDynamicSalt("/api/orders");
        String dynamicSalt = (String) saltResponse.get("dynamicSalt");
        Long dynamicSaltTime = (Long) saltResponse.get("dynamicSaltTime");

        // 2. 生成签名
        Map<String, Object> signResponse = generateSignature(
            "/api/orders", dynamicSalt, dynamicSaltTime, false, null
        );
        String sign = (String) signResponse.get("sign");
        Long time = (Long) signResponse.get("time");

        // 3. 携带签名访问受保护接口
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("userId", "123");
        requestBody.put("orderName", "测试订单");
        requestBody.put("amount", 100.50);

        String result = callProtectedApi("/api/orders", sign, time, requestBody);
        System.out.println("调用结果: " + result);
    }

    /**
     * 生成动态盐值
     */
    private static Map<String, Object> generateDynamicSalt(String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL + "/api/sign/dynamic-salt-generate"))
            .header("Sign-appCode", APP_CODE)
            .header("Sign-path", path)
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString("{}"))
            .build();

        HttpResponse<String> response = httpClient.send(request,
            HttpResponse.BodyHandlers.ofString());

        // 解析响应，提取 dynamicSalt 和 dynamicSaltTime
        // 这里简化处理，实际应使用 JSON 库解析
        return parseResponse(response.body());
    }

    /**
     * 生成签名
     */
    private static Map<String, Object> generateSignature(
            String path, String dynamicSalt, Long dynamicSaltTime,
            boolean withParams, Map<String, Object> parameters) throws Exception {

        HttpRequest.Builder builder = HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL + "/api/sign/generate"))
            .header("Sign-appCode", APP_CODE)
            .header("Sign-path", path)
            .header("Sign-dynamicSalt", dynamicSalt)
            .header("Sign-dynamicSaltTime", String.valueOf(dynamicSaltTime))
            .header("Sign-withParams", String.valueOf(withParams))
            .header("Content-Type", "application/json");

        String body = withParams ? toJson(parameters) : "{}";
        HttpRequest request = builder.POST(HttpRequest.BodyPublishers.ofString(body)).build();

        HttpResponse<String> response = httpClient.send(request,
            HttpResponse.BodyHandlers.ofString());

        return parseResponse(response.body());
    }

    /**
     * 调用受保护接口
     */
    private static String callProtectedApi(
            String path, String sign, Long time, Map<String, Object> body) throws Exception {

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL + path))
            .header("Sign-appCode", APP_CODE)
            .header("Sign-sign", sign)
            .header("Sign-time", String.valueOf(time))
            .header("Sign-path", path)
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(toJson(body)))
            .build();

        HttpResponse<String> response = httpClient.send(request,
            HttpResponse.BodyHandlers.ofString());

        return response.body();
    }

    // 辅助方法（实际应使用 JSON 库）
    private static Map<String, Object> parseResponse(String json) {
        // 解析 JSON 响应
        return new HashMap<>();
    }

    private static String toJson(Map<String, Object> map) {
        // 转换为 JSON 字符串
        return "{}";
    }
}
```

---

### 示例 5：客户端调用流程（curl）

```bash
#!/bin/bash

BASE_URL="http://localhost:8080"
APP_CODE="APP001"
PATH="/api/orders"

# 1. 生成动态盐值
echo "=== 步骤1: 生成动态盐值 ==="
SALT_RESPONSE=$(curl -s -X POST "$BASE_URL/api/sign/dynamic-salt-generate" \
  -H "Sign-appCode: $APP_CODE" \
  -H "Sign-path: $PATH" \
  -H "Content-Type: application/json" \
  -d '{}')

echo "动态盐值响应: $SALT_RESPONSE"

# 提取动态盐值和时间戳（需要 jq 工具）
DYNAMIC_SALT=$(echo $SALT_RESPONSE | jq -r '.data.dynamicSalt')
DYNAMIC_SALT_TIME=$(echo $SALT_RESPONSE | jq -r '.data.dynamicSaltTime')

echo "dynamicSalt: $DYNAMIC_SALT"
echo "dynamicSaltTime: $DYNAMIC_SALT_TIME"

# 2. 生成签名
echo -e "\n=== 步骤2: 生成签名 ==="
SIGN_RESPONSE=$(curl -s -X POST "$BASE_URL/api/sign/generate" \
  -H "Sign-appCode: $APP_CODE" \
  -H "Sign-path: $PATH" \
  -H "Sign-dynamicSalt: $DYNAMIC_SALT" \
  -H "Sign-dynamicSaltTime: $DYNAMIC_SALT_TIME" \
  -H "Sign-withParams: false" \
  -H "Content-Type: application/json" \
  -d '{}')

echo "签名响应: $SIGN_RESPONSE"

# 提取签名和时间戳
SIGN=$(echo $SIGN_RESPONSE | jq -r '.data.sign')
TIME=$(echo $SIGN_RESPONSE | jq -r '.data.time')

echo "sign: $SIGN"
echo "time: $TIME"

# 3. 携带签名访问受保护接口
echo -e "\n=== 步骤3: 访问受保护接口 ==="
RESULT=$(curl -s -X POST "$BASE_URL$PATH" \
  -H "Sign-appCode: $APP_CODE" \
  -H "Sign-sign: $SIGN" \
  -H "Sign-time: $TIME" \
  -H "Sign-path: $PATH" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "123",
    "orderName": "测试订单",
    "amount": 100.50
  }')

echo "调用结果: $RESULT"
```

---

## 安全特性

### 1. 防止数据篡改
- 签名计算包含请求的关键信息（appCode、path、timestamp、params）
- 任何信息被篡改后，签名验证都会失败

### 2. 防止重放攻击
- **时间戳验证**：签名包含时间戳，过期后自动失效（TTL 默认 30 分钟）
- **动态盐值**：每次请求都需要生成新的动态盐值，防止签名被重复使用
- **数据库校验**：开启数据库校验后，动态盐值只能使用一次

### 3. 防止中间人攻击
- 密钥不在网络中传输，只在签名计算时使用
- 建议在生产环境使用 HTTPS，防止请求被窃听

### 4. 应用隔离
- 每个应用有独立的 appCode 和 secretKey
- 不同应用之间的签名不能互相使用

### 5. 路径权限控制
- **permitPaths**：白名单，允许访问的路径
- **forbiddenPaths**：黑名单，禁止访问的路径
- 支持 Ant 风格路径模式（如 `/api/orders/**`）

### 6. 灵活的签名算法
- **不带参数签名（SHA-256）**：适用于 GET 请求、参数不敏感的接口
- **带参数签名（SM3）**：适用于 POST/PUT 请求、参数敏感的接口
- 参数按 ASCII 字典序排序，确保签名的一致性

### 7. 多层验证
- **基础参数校验**：appCode、sign、time 不能为空
- **时效性验证**：签名必须在有效期内
- **路径一致性校验**：客户端提供的 path 与服务端实际路径必须一致
- **应用权限验证**：应用必须有权限访问指定路径
- **签名算法验证**：重新计算签名并比对

---

## 常见问题

### Q1: 为什么需要动态盐值？

**答**：动态盐值是一个可选的安全增强机制，主要作用是：
- **防止签名被破解**：即使攻击者获得了签名，也无法通过反向推导获得密钥
- **防止重放攻击**：每次请求都需要生成新的动态盐值，防止签名被重复使用
- **增强时效性**：动态盐值有独立的 TTL（默认 24 小时），比签名的 TTL（默认 30 分钟）更长

如果安全性要求不高，可以跳过动态盐值，直接生成签名。

---

### Q2: 什么时候使用带参数签名？

**答**：推荐规则：
- **GET 请求**：使用不带参数签名（`withParams = false`）
- **POST/PUT/PATCH 请求（参数不敏感）**：使用不带参数签名
- **POST/PUT/PATCH 请求（参数敏感）**：使用带参数签名（`withParams = true`）

带参数签名的优势：
- 验证参数的完整性，防止参数被篡改
- 更高的安全性（使用 SM3 算法）

带参数签名的劣势：
- 性能略低（需要读取请求体并计算签名）
- 参数必须严格一致（包括参数顺序）

---

### Q3: 如何处理路径参数？

**答**：签名计算使用的 path 必须是接口的模板路径，而不是实际路径。

例如：
- 模板路径：`/api/orders/{orderId}`
- 实际路径：`/api/orders/123456`
- 签名时使用：`/api/orders/{orderId}`

服务端会自动将实际路径转换为模板路径进行验证。

---

### Q4: 签名验证失败的常见原因？

**答**：
1. **时间戳过期**：签名的 TTL 默认 30 分钟，超过后需要重新生成
2. **路径不一致**：客户端提供的 path 与服务端实际路径不一致
3. **参数不一致**：带参数签名时，参数被修改或顺序错误
4. **密钥错误**：appCode 或 secretKey 配置错误
5. **算法不匹配**：签名时使用的算法与验证时的算法不一致
6. **动态盐值过期**：动态盐值的 TTL 默认 24 小时，超过后需要重新生成
7. **动态盐值已使用**：开启数据库校验后，动态盐值只能使用一次

---

### Q5: 如何在本地开发环境快速测试？

**答**：配置 `config-mode: local`，在 `application.yml` 中配置应用信息：

```yaml
sign:
  config-mode: local
  local-apps:
    - app-code: DEV001
      secret-key: dev_secret_key_123
      permit-paths: /**
```

然后使用 `DEV001` 和 `dev_secret_key_123` 进行签名生成和验证。

---

### Q6: 生产环境如何配置？

**答**：推荐配置：

```yaml
sign:
  config-mode: database  # 从数据库读取应用信息
  repository-type: jdbc  # 使用 JDBC 提升性能
  dynamic-salt:
    ttl: 86400000  # 24 小时
    validate-from-database: true  # 开启数据库校验，防止重放攻击
  signature:
    ttl: 1800000  # 30 分钟
    default-with-params: false  # 根据实际情况配置
```

同时确保：
- 使用 HTTPS 协议
- 定期更换密钥
- 监控异常签名请求
- 限制请求频率

---

### Q7: 如何扩展签名策略？

**答**：本项目使用策略模式，支持自定义签名策略。

例如，添加 Redis 密钥获取策略：

1. 实现 `SecretKeyRetrievalStrategy` 接口：

```java
@Component
public class RedisSecretKeyStrategy implements SecretKeyRetrievalStrategy {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public String getSecretKey(String appCode) {
        return redisTemplate.opsForValue().get("sign:secretKey:" + appCode);
    }

    @Override
    public String getStrategyName() {
        return "redis";
    }
}
```

2. 在配置文件中指定策略：

```yaml
sign:
  secret-key-strategy: redis
```

---

### Q8: 性能优化建议？

**答**：
1. **使用 JDBC 而不是 JPA**：`repository-type: jdbc`
2. **关闭数据库校验**：`validate-dynamic-salt-from-database: false`（如果不需要防止重放攻击）
3. **使用不带参数签名**：`default-with-params: false`（如果参数不敏感）
4. **增加缓存**：对密钥、权限信息等进行缓存
5. **限制缓存路径**：只缓存需要签名验证的路径

---

## 总结

本项目的签名验证机制具有以下特点：

1. **完整的签名流程**：动态盐值生成 → 签名生成 → 签名验证
2. **灵活的配置模式**：支持本地配置和数据库配置
3. **多种签名算法**：SHA-256、SM3、MD5、SHA1
4. **策略模式设计**：易于扩展新的签名策略
5. **注解驱动开发**：简化签名验证的使用
6. **多层安全防护**：防止数据篡改、重放攻击、中间人攻击
7. **DDD 架构**：清晰的分层设计，易于维护和扩展

更多信息请参考：
- [README.md](README.md) - 项目整体说明
- [SignatureInterceptor.java](src/main/java/com/microwind/knife/middleware/SignatureInterceptor.java) - 签名拦截器
- [SignService.java](src/main/java/com/microwind/knife/application/services/sign/SignService.java) - 签名服务
- [SignatureUtil.java](src/main/java/com/microwind/knife/utils/SignatureUtil.java) - 签名工具类
