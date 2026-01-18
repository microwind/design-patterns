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
│   ├── persistence/                    # 持久化实现
│   └── util/                           # 工具类
│       └── SignatureUtil.java          # 签名工具类
└── interfaces/                         # 接口层
    └── rest/                           # REST API
        └── HealthController.java       # 健康检查控制器
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

## License

MIT License

## 作者

jarry
