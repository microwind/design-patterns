# 设计模式与架构实践项目集

> `practice-projects`：用可运行工程演示设计模式、DDD、MVC、事件驱动与企业级开发实践。

本目录聚焦两类目标：
- 学习架构与模式在真实项目中的落地方式
- 作为新项目脚手架的参考起点（Java / Go）

---

## 项目总览

| 项目 | 语言 | 架构/定位 | 核心特点 | 入口文档 |
|---|---|---|---|---|
| [springwind](./springwind) | Java | 类 Spring 框架原理实现 | IoC、DI、AOP、MVC、JDBC 模板 | [README](./springwind/README.md) |
| [knife](./knife) | Java | Spring Boot 3.x + DDD | 分层架构、签名验证、企业工具化 | [README](./knife/README.md) |
| [springboot4ddd](./springboot4ddd) | Java | Spring Boot 4 + DDD | Java 21、RocketMQ、多数据源 | [README](./springboot4ddd/README.md) |
| [gin-ddd](./gin-ddd) | Go | Gin + DDD | 四层架构、领域事件、RocketMQ | [README](./gin-ddd/README.md) |
| [gin-mvc](./gin-mvc) | Go | Gin + MVC | Controller/Service/Repository/Model、RocketMQ | [README](./gin-mvc/README.md) |

---

## 目录结构

```text
practice-projects/
├── springwind/          # Java：框架原理学习项目
├── knife/               # Java：Spring Boot 3.x DDD 工程
├── springboot4ddd/      # Java：Spring Boot 4 DDD 工程
├── gin-ddd/             # Go：DDD 脚手架
├── gin-mvc/             # Go：MVC 脚手架
└── README.md            # 当前导航文档
```

---

## 快速开始

### 1) 进入目录

```bash
cd practice-projects
ls -la
```

### 2) 按项目启动

#### Springwind（框架原理）

```bash
cd springwind
mvn clean compile -DskipTests=true
mvn clean test
```

说明：`springwind` 更偏框架学习项目，运行方式与示例请按其 README 指引。

#### Knife（Spring Boot 3.x）

```bash
cd knife
mvn clean install -U
mvn spring-boot:run
```

#### Springboot4DDD（Spring Boot 4）

```bash
cd springboot4ddd
./mvnw clean compile
./mvnw spring-boot:run
```

#### Gin DDD（Go）

```bash
cd gin-ddd
go mod tidy
go run cmd/server/main.go
```

#### Gin MVC（Go）

```bash
cd gin-mvc
go mod tidy
go run cmd/main.go
```

> 各项目的数据库初始化、MQ 配置和完整接口示例，请直接查看对应子项目 README。

---

## 如何选择

### 我是 Java 开发者

| 目标 | 推荐项目 | 原因 |
|---|---|---|
| 想理解 Spring 底层机制 | `springwind` | 从零实现 IoC/DI/AOP/MVC，原理清晰 |
| 想快速搭建 DDD 应用（SB3） | `knife` | 成熟分层 + 业务化实践 |
| 想用新技术栈做 DDD（SB4/Java21） | `springboot4ddd` | 新版本 + 事件驱动 + 工程化更完整 |

### 我是 Go 开发者

| 目标 | 推荐项目 | 原因 |
|---|---|---|
| 更强调领域建模与边界清晰 | `gin-ddd` | 四层 DDD + 领域事件 |
| 更强调实现效率与可读性 | `gin-mvc` | MVC 分层直观，上手成本更低 |

### Go 版本对照（DDD vs MVC）

| 维度 | gin-ddd | gin-mvc |
|---|---|---|
| 分层方式 | 领域/应用/基础设施/接口 | Controller/Service/Repository/Model |
| 适用场景 | 中大型复杂业务、强领域边界 | 多数常规业务、追求交付速度 |
| 事件驱动 | ✅ RocketMQ | ✅ RocketMQ |
| 双数据库 | ✅ | ✅ |
| 学习成本 | 较高 | 较低 |

---

## 推荐学习路径

1. `springwind`：先理解框架基本原理（IoC/DI/AOP/MVC）。
2. `knife` 或 `springboot4ddd`：理解 Java 体系下 DDD 分层与工程实践。
3. `gin-mvc`：快速建立 Go Web 工程化思维。
4. `gin-ddd`：对比 MVC 与 DDD，在 Go 中实践复杂业务建模。

---

## 常见问题

- 依赖拉取慢
  - Maven 配置镜像；Go 设置 `GOPROXY`。
- 启动失败（数据库连接）
  - 先检查本地数据库是否可连通、用户名密码与库名是否一致。
- RocketMQ 相关报错
  - 确认 NameServer/Broker 已启动，且配置与代码中的 topic/group 一致。

---

## 贡献与许可

- 许可：MIT（以各子项目内声明为准）
- 欢迎通过 Issue / PR 提交改进建议

---

**维护者**: Jarry  
**最后更新**: 2026-02-27
