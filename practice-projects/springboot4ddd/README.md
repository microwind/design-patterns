# 基于 Spring Boot 4 的 DDD 工程脚手架

基于 Spring Boot 4.0.1 和领域驱动设计（DDD）的脚手架，开箱即用，让你快速搭建Java Web工程。本项目旨在让Java后端项目变得和Go、NodeJS、Python一样简单，易于上手。

源码地址：https://github.com/microwind/design-patterns

## 技术栈

- **Spring Boot**: 4.0.1
- **Java**: 21
- **数据库**: PostgreSQL（生产环境）、H2（测试环境）
- **缓存**: Redis
- **消息队列**: RocketMQ
- **构建工具**: Maven

## 项目结构

```
src/main/java/com/github/microwind/springboot4ddd/
├── Application.java                    # Spring Boot 启动类
├── domain/                             # 领域层
│   ├── model/                          # 领域模型（实体、值对象、聚合根）
│   ├── repository/                     # 仓储接口
│   └── service/                        # 领域服务
├── application/                        # 应用层
│   ├── dto/                            # 数据传输对象
│   └── service/                        # 应用服务
├── infrastructure/                     # 基础设施层
│   ├── common/                         # 通用组件
│   │   ├── ApiResponse.java            # 统一API响应对象
│   │   └── GlobalExceptionHandler.java # 全局异常处理器
│   ├── config/                         # 配置类
│   ├── repository/                     # 持久化实现
│   └── util/                           # 工具类
│       └── SignatureUtil.java          # 签名工具类
└── interfaces/                         # 接口层
│   ├── controller/                     # 控制器 REST API
|   |   └── HealthController.java       # 健康检查控制器
│   ├── annotation/                     # 注解声明
│   ├── vo/                             # requset与response对象
        
```

## DDD 层次说明

### 领域层 (Domain Layer)
- 核心业务逻辑层，包含领域模型和业务规则
- 独立于技术实现细节
- 包含实体、值对象、聚合根、领域服务和仓储接口

### 应用层 (Application Layer)
- 协调领域对象完成用户需求
- 不包含业务逻辑，只负责编排
- 定义应用服务和DTO

### 基础设施层 (Infrastructure Layer)
- 提供技术支撑能力
- 包含持久化、配置、通用组件等
- 实现领域层定义的仓储接口

### 接口层 (Interfaces Layer)
- 对外暴露接口
- 包含REST API、消息监听器等
- 处理请求响应转换

## 快速开始

### 环境要求

- JDK 21+
- Maven 3.8+
- PostgreSQL 14+ (生产环境)
- Redis 6+ (可选)
- RocketMQ 4.9+ (可选)

### 启动mysql

```shell
$ sudo systemctl start mysql # Linux
$ sudo mysql.server start # MacOS
$ brew services start mysql # MacOS
```

### 启动PostgreSQL
```shell
$ brew services start postgresql # MacOS
$ sudo systemctl start postgresql # Linux
```

### 启动roketmq
#### 启动 NameServer
```shell
$ nohup sh bin/mqnamesrv &
# 查看 NameServer 日志
$ tail -f ~/logs/rocketmqlogs/namesrv.log
# 启动 Broker，并指定 NameServer 地址为本地
$ nohup sh bin/mqbroker -n localhost:9876 &
# 查看 Broker 日志
$ tail -f ~/logs/rocketmqlogs/broker.log
# 使用jps命令检查NameServer和Broker的进程是否存在
$ jps
```

### 构建项目

```bash
./mvnw clean compile
```

### 运行测试

```bash
./mvnw test
```

### 启动应用

```bash
./mvnw spring-boot:run
```

应用将在 `http://localhost:8080` 启动。

### 健康检查

访问健康检查接口：
```bash
curl http://localhost:8080/api/health
```

## 配置说明

### 生产环境配置 (src/main/resources/application.yaml)

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/springboot4ddd
    username: postgres
    password: postgres
  data:
    redis:
      host: localhost
      port: 6379
```

### 测试环境配置 (src/test/resources/application.yaml)

测试环境使用 H2 内存数据库，无需配置外部数据库。

## 核心功能

- ✅ DDD 分层架构
- ✅ 统一响应格式
- ✅ 全局异常处理
- ✅ 签名工具支持
- ✅ 健康检查接口
- ✅ PostgreSQL 数据库支持
- ✅ Redis 缓存支持
- ✅ RocketMQ 消息队列支持

## 开发指南

### 添加新功能

1. 在 `domain/model` 中定义领域模型
2. 在 `domain/repository` 中定义仓储接口
3. 在 `infrastructure/persistence` 中实现仓储
4. 在 `application/service` 中实现应用服务
5. 在 `interfaces/rest` 中添加 REST 控制器

### 代码规范

- 遵循 DDD 分层原则
- 使用 Lombok 简化代码
- 保持领域层纯净，不依赖框架
- 所有对外接口使用统一响应格式 `ApiResponse`

### 测试访问链接

**User**

| 方法 | HTTP | 路由 | 功能 |
| :--- | :--- | :--- | :--- |
| createUser | POST | `/api/users` | 创建用户 |
| getAllUsers | GET | `/api/users` | 获取所有用户 |
| getUsersByPage | GET | `/api/users/page` | 分页查询用户 |
| getUserById | GET | `/api/users/{id}` | 根据ID获取用户 |
| getUserByName | GET | `/api/users/name/{name}` | 根据用户名获取用户 |
| updateUser | PUT | `/api/users/{id}` | 更新用户 |
| deleteUser | DELETE | `/api/users/{id}` | 删除用户 |

**Order**

| 方法 | HTTP | 路由 | 功能 |
| :--- | :--- | :--- | :--- |
| createOrder | POST | `/api/orders/create` | 创建订单 |
| getOrder | GET | `/api/orders/{id}` | 获取订单详情 |
| getUserOrders | GET | `/api/orders/user/{userId}` | 获取用户订单列表 |
| getUserOrdersByPage | GET | `/api/orders/user/{userId}/page` | 分页查询用户订单 |
| listAllOrders | GET | `/api/orders/list` | 获取所有订单 |
| listAllOrdersByPage | GET | `/api/orders/page` | 分页查询所有订单 |
| cancelOrder | POST | `/api/orders/{id}/cancel` | 取消订单 |
| payOrder | POST | `/api/orders/{id}/pay` | 支付订单 |
| completeOrder | POST | `/api/orders/{id}/complete` | 完成订单 |
| deleteOrder | DELETE | `/api/orders/{id}` | 删除订单 |

#### 分页请求示例

**使用 Spring Data 的 Pageable 参数：**

```bash
# 获取第0页，每页10条数据，按照创建时间降序排列
curl "http://localhost:8080/api/users/page?page=0&size=10&sort=id,desc"

# 获取用户的订单分页数据
curl "http://localhost:8080/api/orders/user/1/page?page=0&size=5&sort=id,desc"

# 获取所有订单的分页数据
curl "http://localhost:8080/api/orders/page?page=0&size=15&sort=createdTime,desc"
```

**响应格式（分页数据）：**

```json
{
  "code": 200,
  "message": "分页查询用户成功",
  "data": {
    "content": [
      { "id": 1, "name": "user1", ... },
      { "id": 2, "name": "user2", ... }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 10,
      "offset": 0,
      "paged": true,
      "unpaged": false
    },
    "totalPages": 5,
    "totalElements": 50,
    "last": false,
    "size": 10,
    "number": 0,
    "sort": { ... },
    "numberOfElements": 10,
    "first": true,
    "empty": false
  },
  "timestamp": "2026-02-27T12:00:00"
}
```

**Pageable 参数说明：**
- `page`: 页码（从0开始，默认0）
- `size`: 每页记录数（默认20）
- `sort`: 排序规则，格式为 `sort=字段名,desc|asc`（可选，多个排序用逗号分隔）

**示例：**
- `?page=0&size=10` - 获取第一页，每页10条
- `?page=1&size=20` - 获取第二页，每页20条
- `?page=0&size=10&sort=id,desc&sort=name,asc` - 按ID降序，名称升序排列

### 脚手架使用指南

[Springboot4DDD-Scaffold.md](./Springboot4DDD-Scaffold.md)

## License

MIT License

## 作者
jarryli

mail:lichunping@buaa.edu.cn
