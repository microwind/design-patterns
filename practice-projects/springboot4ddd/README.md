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

### 启动依赖服务

#### 启动 MySQL（用于存储用户数据）

```shell
# Linux 系统
$ sudo systemctl start mysql

# MacOS 系统
$ sudo mysql.server start
# 或者使用 Homebrew
$ brew services start mysql
```

#### 启动 PostgreSQL（用于存储订单数据）

```shell
# MacOS 系统
$ brew services start postgresql

# Linux 系统
$ sudo systemctl start postgresql

# 验证连接（可选）
$ psql -h localhost -U postgres -d postgres
```

#### 启动 Redis（可选，用于缓存）

```shell
# MacOS 系统
$ brew services start redis

# Linux 系统
$ sudo systemctl start redis-server

# 验证连接（可选）
$ redis-cli ping
# 返回 PONG 表示连接成功
```

#### 启动 RocketMQ（用于事件驱动架构）

```shell
# 进入 RocketMQ 安装目录
$ cd /path/to/rocketmq-all-x.x.x-bin-release

# 1. 启动 NameServer（消息名称服务，管理 Topic 和 Broker 信息）
# 建议在后台运行：
$ nohup sh bin/mqnamesrv > /tmp/namesrv.log 2>&1 &

# 查看 NameServer 启动日志
$ tail -f /tmp/namesrv.log

# 2. 启动 Broker（消息代理服务，负责存储和转发消息）
# 需要指定 NameServer 地址，等待 NameServer 启动后再启动 Broker
$ sleep 5  # 等待 NameServer 完全启动
$ nohup sh bin/mqbroker -n localhost:9876 > /tmp/broker.log 2>&1 &

# 查看 Broker 启动日志
$ tail -f /tmp/broker.log

# 3. 验证启动成功
# 检查进程是否运行（NameServer 和 Broker 都应该存在）
$ ps aux | grep -E "NamesrvStartup|BrokerStartup" | grep -v grep

# 或者使用 jps 命令查看
$ jps
# 应该看到 NamesrvStartup 和 BrokerStartup 进程

# 4. 清理 RocketMQ 数据（开发调试时）
# 如果需要清理 RocketMQ 中的消息数据和存储，执行以下步骤：
$ pkill -f "org.apache.rocketmq.broker.BrokerStartup"      # 停止 Broker
$ pkill -f "org.apache.rocketmq.namesrv.NamesrvStartup"    # 停止 NameServer
$ rm -rf ~/store                                             # 删除存储数据
$ rm -rf /path/to/logs/rocketmqlogs                         # 删除日志文件
# 然后重新启动 NameServer 和 Broker（重复步骤1和2）
```

**RocketMQ 默认配置：**
- NameServer 监听地址：`127.0.0.1:9876`
- Broker 监听地址：`127.0.0.1:10911`（通常无需修改）

### 构建项目

```bash
./mvnw clean compile
```

### 运行测试

```bash
./mvnw test
```

### 启动应用

#### 方式一：使用 Maven 直接启动（推荐开发环境）

```bash
# 启动应用，使用 Spring Boot Maven 插件
$ ./mvnw spring-boot:run

# 或者如果已经编译过：
$ mvn spring-boot:run
```

#### 方式二：编译后启动（推荐生产环境）

```bash
# 1. 编译项目（跳过测试，加快编译）
$ ./mvnw clean package -DskipTests

# 2. 启动应用
# 找到生成的 jar 文件（通常在 target 目录下）
$ java -jar target/microwind.springboot4ddd-0.0.1-SNAPSHOT.jar

# 3. 以后台进程启动（推荐用于服务器）
$ nohup java -jar target/microwind.springboot4ddd-0.0.1-SNAPSHOT.jar > /tmp/app.log 2>&1 &

# 4. 查看启动日志
$ tail -f /tmp/app.log
```

#### 方式三：使用 IDE 启动（适合 IntelliJ IDEA）

在 IntelliJ IDEA 中：
1. 右键点击 `Application.java`
2. 选择 `Run 'Application.main()'`
3. 或者按 `⌃⇧R` (MacOS) / `Ctrl+Shift+F10` (Windows/Linux)

#### 启动验证

应用启动成功后，应该看到以下日志信息：

```
RocketMQ 配置验证通过
MySQL(User) 数据库连接正常
PostgreSQL(Order) 数据库连接正常
RocketMQ NameServer 可连接: 127.0.0.1:9876
外部依赖服务检查通过
Tomcat started on port 8080 (http) with context path '/'
Started Application in X.XXX seconds
```

如果看到类似日志说明启动成功。

#### 健康检查

应用启动后，可以通过健康检查接口验证服务状态：

```bash
# 基础健康检查
$ curl http://localhost:8080/api/health

# 返回示例：
# {
#   "code": 200,
#   "message": "健康检查通过",
#   "data": {
#     "status": "UP",
#     "dependencies": {
#       "MySQL": "正常",
#       "PostgreSQL": "正常",
#       "Redis": "正常",
#       "RocketMQ": "正常"
#     }
#   },
#   "timestamp": "2026-02-27T12:00:00"
# }
```

#### 常见问题排查

**问题：启动失败，提示数据库连接错误**
- 检查 PostgreSQL 和 MySQL 是否已启动
- 检查 `application.yaml` 中数据库配置是否正确
- 默认配置启用了优雅降级，数据库不可用时系统仍能启动

**问题：RocketMQ 连接失败**
- 检查 NameServer 是否已启动：`ps aux | grep NamesrvStartup`
- 检查 Broker 是否已启动：`ps aux | grep BrokerStartup`
- 检查配置文件中的 NameServer 地址是否正确：`127.0.0.1:9876`
- 如果磁盘满（消息发送失败时），需要清理存储数据：`rm -rf ~/store`

**问题：Redis 连接失败（可选）**
- 如果不使用缓存功能，可以忽略 Redis 连接错误
- Redis 启动：`brew services start redis` (MacOS) 或 `sudo systemctl start redis-server` (Linux)

#### 停止应用

```bash
# 如果是使用 Maven 或 IDE 启动：
# 直接在控制台按 Ctrl+C

# 如果是后台进程启动：
$ pkill -f "microwind.springboot4ddd"
# 或者
$ kill -9 <PID>
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
# 获取第1页，每页10条数据，按照ID降序排列
curl "http://localhost:8080/api/users/page?page=1&size=10&sort=id,desc"

# 获取用户的订单分页数据（第1页）
curl "http://localhost:8080/api/orders/user/1/page?page=1&size=5&sort=id,desc"

# 获取所有订单的分页数据（第1页）
curl "http://localhost:8080/api/orders/page?page=1&size=15&sort=createdTime,desc"
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
      "pageNumber": 1,
      "pageSize": 10,
      "offset": 0,
      "paged": true,
      "unpaged": false
    },
    "totalPages": 5,
    "totalElements": 50,
    "last": false,
    "size": 10,
    "number": 1,
    "sort": { ... },
    "numberOfElements": 10,
    "first": true,
    "empty": false
  },
  "timestamp": "2026-02-27T12:00:00"
}
```

**Pageable 参数说明：**
- `page`: 页码（从1开始，默认1）
- `size`: 每页记录数（默认20）
- `sort`: 排序规则，格式为 `sort=字段名,desc|asc`（可选，多个排序用逗号分隔）

**示例：**
- `?page=1&size=10` - 获取第一页，每页10条
- `?page=2&size=20` - 获取第二页，每页20条
- `?page=1&size=10&sort=id,desc&sort=name,asc` - 按ID降序，名称升序排列

### 脚手架使用指南

[Springboot4DDD-Scaffold.md](./Springboot4DDD-Scaffold.md)

## License

MIT License

## 作者
jarryli

mail:lichunping@buaa.edu.cn
