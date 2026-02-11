# 设计模式应用实践项目集

> 通过实际工程项目展示设计模式、架构模式、以及企业级应用开发最佳实践

这是一个精心设计的学习项目集合，包含多个从零开始构建的应用脚手架，涵盖 Java 和 Go 两个主流生态，展示了现代企业级应用的架构设计、模式应用和最佳实践。

---

## 🚀 快速导航

| 项目 | 语言 | 框架 | 特点 | 文档 |
|-----|------|------|------|------|
| **Springwind** | Java | 原生实现 | 学习 Spring MVC 原理 | [查看详情](#-springwind) |
| **Knife** | Java | Spring Boot 3.x | DDD + 企业级工具库 | [查看详情](#-knife) |
| **Springboot4DDD** | Java | Spring Boot 4.0.1 | DDD + RocketMQ | [查看详情](#-springboot4ddd) |
| **Gin DDD** | Go | Gin 1.9+ | DDD + RocketMQ 事件驱动 | [查看详情](#-gin-ddd) |

---

## 📚 项目详细介绍

### 🔷 Springwind

**[→ 进入项目目录](./springwind)**

Springwind 是一个类似 Spring MVC 的轻量级原生实现，完全不依赖 Spring 框架，从零开始构建 Web 应用基础设施。

#### 核心特点：
- ✅ **自主实现 IoC 容器** - 理解控制反转的核心机制
- ✅ **Servlet 请求分发** - 学习 Web 框架的路由处理原理
- ✅ **ORM 数据库映射** - 掌握对象-关系映射的实现
- ✅ **零依赖设计** - 完全自主实现，没有框架黑盒

#### 适用场景：
- 想深入理解 Spring 框架的底层原理
- 学习 Web 框架如何处理请求和依赖注入
- 理解 ORM 框架的设计思想

#### 技术栈：
- Java 8+
- 原生 Servlet
- JDBC

---

### 🔶 Knife

**[→ 进入项目目录](./knife)**

Knife 是一个基于 Spring Boot 3.x 的企业级 DDD 脚手架和工具库，严格遵循领域驱动设计理念进行架构设计。

#### 核心特点：
- ✅ **DDD 分层架构** - 领域层、应用层、基础设施层、接口层清晰分离
- ✅ **Spring Security** - 企业级安全认证和授权
- ✅ **MyBatis 持久化** - 灵活的数据库映射框架
- ✅ **Redis 缓存** - 高性能缓存集成
- ✅ **工具库支持** - 丰富的辅助工具和组件

#### 适用场景：
- 快速搭建符合 DDD 原则的 Spring Boot 应用
- 学习企业级应用的分层架构设计
- 了解 DDD 的实战应用和最佳实践

#### 技术栈：
- Java 17+
- Spring Boot 3.x
- Spring Security
- MyBatis
- Redis

---

### 🟡 Springboot4DDD

**[→ 进入项目目录](./springboot4ddd)**

Springboot4DDD 是一个基于 Spring Boot 4.0.1 和 Java 21 的 DDD 工程脚手架，采用严格的四层架构设计。

#### 核心特点：
- ✅ **严格四层架构** - 领域层、应用层、基础设施层、接口层
- ✅ **事件驱动设计** - RocketMQ 消息队列支持
- ✅ **多数据库支持** - PostgreSQL、Redis 集成
- ✅ **开箱即用** - 完整的脚手架，快速搭建
- ✅ **最新技术栈** - Spring Boot 4.0.1 + Java 21

#### 适用场景：
- 构建新一代高性能企业级应用
- 学习完整的 DDD 实施方案
- 理解事件驱动架构在真实项目中的应用

#### 技术栈：
- Java 21+
- Spring Boot 4.0.1
- PostgreSQL
- Redis
- RocketMQ

---

### 🔵 Gin DDD

**[→ 进入项目目录](./gin-ddd)**

Gin DDD 是一个基于 Gin 框架和 RocketMQ 的 Go 语言 DDD 工程脚手架，实现完整的事件驱动架构。

#### 核心特点：
- ✅ **DDD 四层架构** - Go 语言中的清晰分层设计
- ✅ **Gin Web 框架** - 高性能的 HTTP 框架
- ✅ **RocketMQ 集成** - 完整的事件驱动实现
- ✅ **领域事件机制** - 订单、用户等业务事件自动发布
- ✅ **双数据库支持** - MySQL 和 PostgreSQL
- ✅ **简洁清晰** - Go 代码结构优雅易扩展

#### 适用场景：
- 用 Go 语言构建 DDD 应用
- 学习事件驱动架构在微服务中的应用
- 理解如何在 Go 中实现高效的异步处理

#### 技术栈：
- Go 1.21+
- Gin 1.9+
- RocketMQ 5.3+
- MySQL 8.0+ 或 PostgreSQL 14+

---

## 🎯 如何选择合适的项目？

### 我是 Java 开发者

| 需求 | 推荐项目 | 原因 |
|------|---------|------|
| 学习 Spring 框架原理 | **Springwind** | 原生实现，深入理解底层 |
| 快速开发 DDD 应用（Spring Boot 3.x） | **Knife** | 成熟稳定，工具完善 |
| 最新技术栈 DDD 应用（Spring Boot 4.0.1） | **Springboot4DDD** | 新版本，事件驱动，性能优异 |

### 我是 Go 开发者

| 需求 | 推荐项目 | 原因 |
|------|---------|------|
| 学习 Go 中的 DDD 实践 | **Gin DDD** | 完整的 DDD 实现，事件驱动 |
| 构建高性能微服务 | **Gin DDD** | Gin 框架性能优异，RocketMQ 支持 |

### 我想学习架构设计

**学习路径推荐**：
1. 先看 **Springwind** - 理解框架底层原理
2. 再看 **Knife 或 Springboot4DDD** - 理解分层架构设计
3. 最后看 **Gin DDD** - 理解事件驱动架构在不同语言中的实现

---

## 🔧 各项目特性对比

| 特性 | Springwind | Knife | Springboot4DDD | Gin DDD |
|------|-----------|-------|-----------------|---------|
| 语言 | Java | Java | Java | Go |
| 框架 | 原生 | Spring Boot 3.x | Spring Boot 4.0.1 | Gin 1.9+ |
| DDD 实现 | - | ✅ | ✅ | ✅ |
| 安全认证 | - | ✅ | ✅ | - |
| 消息队列 | - | - | ✅ | ✅ |
| 缓存支持 | - | ✅ | ✅ | - |
| 事件驱动 | - | - | ✅ | ✅ |
| 学习难度 | ⭐⭐⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐ |

---

## 🚀 快速开始

### 项目克隆与进入

```bash
# 查看所有项目
ls -la

# 进入某个项目
cd springwind        # 或 knife、springboot4ddd、gin-ddd
```

### 各项目启动方式

#### Springwind
```bash
cd springwind
mvn clean package
mvn spring-boot:run
```

#### Knife
```bash
cd knife
mvn clean package
mvn spring-boot:run
```

#### Springboot4DDD
```bash
cd springboot4ddd
mvn clean package
mvn spring-boot:run
```

#### Gin DDD
```bash
cd gin-ddd
go mod tidy
go run cmd/server/main.go
```

**详细的启动步骤请参考各项目的 README 文件。**

---

## 📖 学习指南

### 对于初学者
1. 从 **Springwind** 开始，理解 Web 框架如何工作
2. 学习 **Knife** 或 **Springboot4DDD**，掌握分层架构
3. 深入 **Gin DDD**，理解事件驱动设计

### 对于有经验的开发者
1. 直接浏览感兴趣的项目
2. 对比不同框架的设计思想
3. 选择适合业务场景的架构模式

### 关键学习点

- **设计模式** - 工厂、单例、代理、观察者等模式在项目中的应用
- **架构设计** - 分层架构、事件驱动、微服务架构
- **DDD 实践** - 聚合根、领域事件、仓储模式
- **最佳实践** - 依赖注入、接口编程、测试驱动开发

---

## 📚 参考资源

- [领域驱动设计（DDD）](https://en.wikipedia.org/wiki/Domain-driven_design)
- [Spring Framework 文档](https://spring.io/projects/spring-framework)
- [Spring Boot 文档](https://spring.io/projects/spring-boot)
- [Gin Web Framework](https://gin-gonic.com/)
- [Apache RocketMQ](https://rocketmq.apache.org/)
- [Event-Driven Architecture](https://martinfowler.com/articles/201701-event-driven.html)
- [Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)

---

## 📄 许可证

MIT License - 所有项目均开源，欢迎学习、修改和分享

---

## 💡 贡献指南

如果你有改进建议或发现问题，欢迎提交 Issue 或 Pull Request！

---

**作者**: Jarry
**最后更新**: 2026-02-11

如果这些项目对你有帮助，欢迎 ⭐ Star！
