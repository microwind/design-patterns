# nestjs-ddd

基于 NestJS 的领域驱动设计（DDD）脚手架，结构参考了 [`gin-ddd`](https://github.com/microwind/design-patterns/tree/main/practice-projects/gin-ddd) 与 [`springboot4ddd`](https://github.com/microwind/design-patterns/tree/main/practice-projects/springboot4ddd) ，开箱即用。

---

## 核心特性

- **DDD 分层清晰**：`domain / application / infrastructure / interfaces` 四层严格解耦
- **多数据库对齐 gin-ddd**：用户库使用 **MySQL**，订单库使用 **PostgreSQL**，通过 TypeORM 多数据源管理
- **YAML 配置**：`config/config.yaml` 与 `gin-ddd` 风格一致，支持 server / database / logger / event 配置
- **领域事件**：基于 `@nestjs/event-emitter` 的内存事件总线，可替换为 Kafka / RabbitMQ
- **API 响应规范**：统一的 `{ code, message, data }` 响应体、全局异常过滤器、Swagger 自动文档
- **接口字段 snake_case**：与 `gin-ddd` 保持同一份 HTTP 契约
- **严格校验**：`class-validator` + 全局 `ValidationPipe`

## 目录结构

```
nestjs-ddd/
├── config/
│   ├── config.yaml                        # 本地开发配置（与 gin-ddd 对齐）
│   └── config.docker.yaml                 # docker compose 专用配置
├── docs/
│   ├── init.mysql.sql                     # 用户库 DDL + 样例数据
│   └── init.postgres.sql                  # 订单库 DDL + 样例数据
├── Dockerfile                             # 应用镜像（多阶段构建）
├── compose.yaml                           # MySQL + PostgreSQL + App 一键编排
├── src/
│   ├── domain/                            # 领域层：实体、仓储接口、领域事件
│   │   ├── shared/events/
│   │   ├── user/{model,repository}
│   │   └── order/{model,repository,events}
│   ├── application/                       # 应用层：用例编排，DTO / Command
│   │   ├── user/
│   │   └── order/
│   ├── infrastructure/                    # 基础设施层
│   │   ├── config/                        # YAML 解析 + TypeORM 多数据源构造
│   │   ├── persistence/{user,order}       # TypeORM 实体 + 仓储实现
│   │   └── messaging/                     # 事件发布 / 监听实现
│   ├── interfaces/http/                   # 接口层：Controller / VO / 过滤器 / 拦截器
│   │   ├── common/
│   │   ├── filters/
│   │   ├── interceptors/
│   │   ├── user/{vo}
│   │   ├── order/{vo}
│   │   └── app.controller.ts
│   ├── modules/                           # NestJS 模块装配
│   ├── app.module.ts
│   └── main.ts
├── package.json
├── tsconfig.json
└── README.md
```

## 快速开始

### 1. 依赖准备

- Node.js ≥ 18
- MySQL ≥ 8（或与 `gin-ddd` 相同的实例）
- PostgreSQL ≥ 13

初始化数据库（**首次部署必做**）：

```shell
# MySQL (用户库 frog) —— 脚本会自动 CREATE DATABASE + DROP/CREATE TABLE
mysql -u root -p < docs/init.mysql.sql

# PostgreSQL (订单库 seed) —— 需要先手动建库，再执行脚本
psql -U postgres -c "CREATE DATABASE seed;"
psql -U postgres -d seed -f docs/init.postgres.sql
```

> **重要**：脚手架默认 `synchronize: false`（生产安全做法），**不会自动改表结构**，
> 必须先用 init SQL 建好表。如果库里有旧版（例如之前跑过 gin-ddd、字段不同），
> 直接再跑一次脚本即可，脚本会 `DROP TABLE` 后重建。

数据库的连接信息集中在 `config/config.yaml`，默认值与 `gin-ddd/config/config.yaml` 完全一致：

| 数据源 | 类型 | 库名 | 默认账户 |
| --- | --- | --- | --- |
| `USER_DATASOURCE` | MySQL | `frog` | `frog_admin / frog798` |
| `ORDER_DATASOURCE` | PostgreSQL | `seed` | `postgres / lego798` |

### 2. 安装依赖并启动

```shell
cd practice-projects/nestjs-ddd
npm install
npm run start:dev
```

启动后：

- 服务地址：`http://localhost:8080`
- API 前缀：`/api`
- Swagger 文档：`http://localhost:8080/docs`
- 健康检查：`GET /health`

#### 常见启动报错

| 错误示例 | 原因 | 解决 |
| --- | --- | --- |
| `column "order_no" of relation "orders" contains null values` | 库里有旧版 `orders` 表，与当前实体不兼容 | 重新执行 `docs/init.postgres.sql`（会 DROP 后重建） |
| `Duplicate entry '' for key 'users.IDX_xxx'` | `users` 表有冲突数据/索引 | 重新执行 `docs/init.mysql.sql`（会 DROP 后重建） |
| `ECONNREFUSED 127.0.0.1:3306/5432` | 数据库没起或端口不通 | 确认 MySQL/PostgreSQL 已启动，或改用 `docker compose up -d mysql postgres` |
| `Access denied for user 'frog_admin'` | 账号密码与 `config/config.yaml` 不一致 | 按 yaml 创建账号，或修改 yaml 账密 |

### 3. 一键启动（Docker Compose）

如果你不想单独装 MySQL / PostgreSQL，可以直接用 `compose.yaml` 一键拉起数据库和服务：

```shell
cd practice-projects/nestjs-ddd

# 启动 MySQL + PostgreSQL + NestJS 应用
docker compose up -d

# 查看日志
docker compose logs -f app

# 停止 & 清理
docker compose down            # 仅停止容器
docker compose down -v         # 连数据卷一起清理
```

- `compose.yaml` 会自动执行 `docs/init.mysql.sql` 与 `docs/init.postgres.sql` 完成建库建表。
- 应用使用 `config/config.docker.yaml`（service 名作为 host），与本地开发配置互不影响。
- 仅起数据库：`docker compose up -d mysql postgres`，然后在宿主机 `npm run start:dev` 连接 localhost。

## API 契约

路由、请求方法、入参/出参字段都与 `gin-ddd` 完全对齐。

### 用户接口

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| `POST`   | `/api/users` | 创建用户 |
| `GET`    | `/api/users` | 获取所有用户 |
| `GET`    | `/api/users/:id` | 获取指定用户 |
| `PUT`    | `/api/users/:id/email` | 更新邮箱 |
| `PUT`    | `/api/users/:id/phone` | 更新手机号 |
| `DELETE` | `/api/users/:id` | 删除用户 |
| `GET`    | `/api/users/:id/orders` | 获取该用户的所有订单 |

### 订单接口

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| `POST` | `/api/orders` | 创建订单 |
| `GET`  | `/api/orders` | 所有订单 |
| `GET`  | `/api/orders/:id` | 订单详情 |
| `PUT`  | `/api/orders/:id/pay` | 支付 |
| `PUT`  | `/api/orders/:id/ship` | 发货 |
| `PUT`  | `/api/orders/:id/deliver` | 确认送达 |
| `PUT`  | `/api/orders/:id/cancel` | 取消 |
| `PUT`  | `/api/orders/:id/refund` | 退款 |

### 示例请求

```shell
# 创建用户
curl -X POST http://localhost:8080/api/users \
  -H 'Content-Type: application/json' \
  -d '{"name":"jarry","email":"jarry@example.com","phone":"13800138000"}'

# 创建订单
curl -X POST http://localhost:8080/api/orders \
  -H 'Content-Type: application/json' \
  -d '{"user_id":1,"total_amount":99.9}'

# 支付订单
curl -X PUT http://localhost:8080/api/orders/1/pay

# 更新手机号
curl -X PUT http://localhost:8080/api/users/1/phone \
  -H 'Content-Type: application/json' \
  -d '{"new_phone":"13900139000"}'
```

### 统一响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": { "id": 1, "name": "jarry", "email": "jarry@example.com" }
}
```

## DDD 分层说明

| 层 | 职责 | 关键约束 |
| --- | --- | --- |
| **domain**         | 业务概念、业务规则（聚合、实体、值对象、领域事件、仓储接口） | **不依赖任何框架**，纯 TS |
| **application**    | 用例编排、事务边界、DTO / Command | 只依赖 `domain` |
| **infrastructure** | 技术细节：持久化、消息、配置、ORM 映射 | 实现 `domain` 中的接口 |
| **interfaces**     | 协议适配（HTTP）：Controller、VO、过滤器、拦截器 | 只依赖 `application` |

### 领域事件

- 接口：`EventPublisher`（`domain/shared/events`）
- 实现：`InMemoryEventPublisher`（`infrastructure/messaging`，基于 `@nestjs/event-emitter`）
- 监听：`OrderEventListener` 示范了 `OrderCreatedEvent` / `OrderPaidEvent` 的处理
- 扩展：替换 `EventPublisher` 的 Provider 即可切换到 Kafka / RabbitMQ / RocketMQ

## 常用脚本

```shell
npm run start:dev       # 开发模式（热重载）
npm run start           # 生产启动
npm run build           # 编译到 dist/
npm run test            # 单元测试
npm run test:e2e        # 端到端测试
npm run lint            # ESLint
npm run format          # Prettier
```

## 与 gin-ddd 的差异

> 在 **API 契约、数据库 schema、业务规则** 与 gin-ddd 完全一致，但使用了各自技术栈的常见做法。

| 维度 | gin-ddd (Go) | nestjs-ddd (TypeScript) |
| --- | --- | --- |
| Web 框架 | Gin | NestJS |
| ORM | GORM | TypeORM |
| IoC | 手工装配 | NestJS 依赖注入 |
| 配置 | Viper + YAML | `@nestjs/config` + `js-yaml` |
| 事件 | 内存 Bus（接口化） | `@nestjs/event-emitter`（接口化） |
| 校验 | binding tag | `class-validator` |
| 文档 | 无 | Swagger 自动生成 |

## 参考

- [NestJS 官方文档](https://docs.nestjs.com/)
- [TypeORM 多数据源](https://typeorm.io/multiple-data-sources)
- 仓库相邻项目：`practice-projects/gin-ddd`, `practice-projects/springboot4ddd`
