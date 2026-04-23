# django-ddd

基于 Django 5 + DRF 的领域驱动设计（DDD）脚手架，结构参考了 [`gin-ddd`](https://github.com/microwind/design-patterns/tree/main/practice-projects/gin-ddd) 与 [`nestjs-ddd`](https://github.com/microwind/design-patterns/tree/main/practice-projects/nestjs-ddd)，开箱即用。

---

## 核心特性

- **DDD 分层清晰**：`domain / application / infrastructure / interfaces` 四层职责清晰解耦
- **务实风格**：参照 DDD 精髓而非死守规范，领域模型为纯 Python 类，与 Django ORM 彻底解耦
- **多数据库对齐 gin-ddd**：用户库使用 **MySQL**，订单库使用 **PostgreSQL**，通过 Django `DATABASE_ROUTERS` 自动路由
- **YAML 配置**：`config/config.yaml` 与 gin-ddd 字段一致（`server / database / logger / event`）
- **领域事件**：内置 `InMemoryEventPublisher`，通过抽象接口可替换为 Kafka / RocketMQ
- **统一响应**：全局 `{ code, message, data }` + DRF 全局异常处理器
- **Swagger**：drf-spectacular 自动生成 OpenAPI，文档直达 `/api/docs`
- **接口字段 snake_case**：与 gin-ddd / nestjs-ddd 共用同一份 HTTP 契约
- **Docker 一键启动**：`docker compose up -d` 拉起 MySQL + PostgreSQL + 应用

## 目录结构

```
django-ddd/
├── config/
│   ├── config.yaml                        # 本地开发配置（与 gin-ddd 对齐）
│   └── config.docker.yaml                 # docker compose 专用
├── docs/
│   ├── init.mysql.sql                     # 用户库 DDL + 样例数据
│   └── init.postgres.sql                  # 订单库 DDL + 样例数据
├── Dockerfile                             # 应用镜像
├── compose.yaml                           # MySQL + PostgreSQL + App 编排
├── manage.py                              # Django 管理入口
├── pyproject.toml                         # 现代 Python 元文件
├── requirements.txt                       # 运行时依赖
├── src/
│   ├── project/                           # Django project 配置
│   │   ├── settings.py                    # 读 YAML → DATABASES + DDD 绑定
│   │   ├── urls.py                        # 根路由：健康检查 + /api + /api/docs
│   │   ├── routers.py                     # app_label → 数据库 的路由器
│   │   ├── wsgi.py / asgi.py
│   │   └── __init__.py
│   ├── shared/                            # 共享层
│   │   ├── apps.py                        # 启动时装配事件发布器 + 监听器
│   │   ├── domain/
│   │   │   ├── events.py                  # DomainEvent 基类
│   │   │   └── publisher.py               # EventPublisher 抽象
│   │   └── infrastructure/
│   │       ├── config.py                  # YAML → dataclass
│   │       ├── response.py                # 统一响应
│   │       ├── exceptions.py              # 领域异常 + 全局 handler
│   │       ├── events.py                  # InMemoryEventPublisher
│   │       └── listeners.py               # 内置监听器
│   ├── user/                              # 用户 bounded context
│   │   ├── domain/{user,repository,events}
│   │   ├── application/{dto,service}
│   │   ├── infrastructure/{models,repository}
│   │   ├── interfaces/{serializers,views,urls}
│   │   ├── migrations/                    # 保留空目录；表由 init.sql 管理
│   │   ├── models.py                      # re-export 供 Django 发现
│   │   └── apps.py
│   └── order/                             # 订单 bounded context
│       ├── domain/{order,repository,events}   # 含状态机
│       ├── application/{dto,service}
│       ├── infrastructure/{models,repository}
│       ├── interfaces/{serializers,views,urls}
│       └── apps.py
└── README.md
```

## 快速开始

### 1. 依赖准备

- Python ≥ 3.10
- MySQL ≥ 8
- PostgreSQL ≥ 13

> MySQL 驱动默认走 **PyMySQL**（纯 Python，零系统依赖），已在 `src/project/__init__.py` 注册为 MySQLdb。
> 如需切换到原生 `mysqlclient`（C 扩展，性能更高，但依赖 `libmysqlclient-dev` + `pkg-config`）：
>
> ```shell
> # macOS
> brew install mysql-client pkg-config
> export PKG_CONFIG_PATH="$(brew --prefix mysql-client)/lib/pkgconfig"
>
> # Debian / Ubuntu
> sudo apt-get install -y default-libmysqlclient-dev pkg-config build-essential
>
> pip install "mysqlclient>=2.2,<3.0"
> ```

初始化数据库（**首次部署必做**）：

```shell
# MySQL (用户库 frog) —— 脚本会 CREATE DATABASE + DROP/CREATE TABLE
mysql -u root -p < docs/init.mysql.sql

# PostgreSQL (订单库 seed)
psql -U postgres -c "CREATE DATABASE seed;"
psql -U postgres -d seed -f docs/init.postgres.sql
```

> Django 的 ORM Meta 配置为 `managed = False`，**不会自动改表结构**，
> 表结构只跟随 `docs/init.*.sql`。和 gin-ddd / nestjs-ddd 的行为保持一致。

### 2. 本地运行

```shell
cd practice-projects/django-ddd

python -m venv .venv
source .venv/bin/activate          # Windows: .venv\Scripts\activate

pip install -r requirements.txt

# 使用项目根目录的 config/config.yaml 启动
python manage.py runserver 0.0.0.0:8080
```

启动后：

- 服务地址：`http://localhost:8080`
- 健康检查：`GET /health`
- API 前缀：`/api`
- Swagger UI：`http://localhost:8080/api/docs`
- OpenAPI：`http://localhost:8080/api/schema`

### 3. Docker 一键启动

```shell
cd practice-projects/django-ddd

# 启动 MySQL + PostgreSQL + Django 应用
docker compose up -d --build

# 查看日志
docker compose logs -f app

# 停止 & 清理
docker compose down            # 仅停止
docker compose down -v         # 连数据卷一起清理
```

- `compose.yaml` 自动执行 `docs/init.mysql.sql` / `docs/init.postgres.sql` 建表
- 应用使用 `config/config.docker.yaml`（host 指向 compose service）
- 仅启动数据库：`docker compose up -d mysql postgres`

## API 契约

路由、请求方法、入参/出参字段都与 gin-ddd / nestjs-ddd 完全对齐。

### 用户接口

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| `POST`   | `/api/users` | 创建用户 |
| `GET`    | `/api/users` | 用户列表（支持 `offset / limit`） |
| `GET`    | `/api/users/<id>` | 获取指定用户 |
| `PUT`    | `/api/users/<id>/email` | 更新邮箱 |
| `PUT`    | `/api/users/<id>/phone` | 更新手机号 |
| `DELETE` | `/api/users/<id>` | 删除用户 |
| `GET`    | `/api/users/<id>/orders` | 该用户的所有订单 |

### 订单接口

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| `POST` | `/api/orders` | 创建订单 |
| `GET`  | `/api/orders` | 订单列表 |
| `GET`  | `/api/orders/<id>` | 订单详情 |
| `PUT`  | `/api/orders/<id>/pay` | 支付 |
| `PUT`  | `/api/orders/<id>/ship` | 发货 |
| `PUT`  | `/api/orders/<id>/deliver` | 送达 |
| `PUT`  | `/api/orders/<id>/cancel` | 取消 |
| `PUT`  | `/api/orders/<id>/refund` | 退款 |

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
```

### 统一响应

```json
{
  "code": 0,
  "message": "success",
  "data": { "id": 1, "name": "jarry" }
}
```

## DDD 分层

| 层 | 关键文件 | 依赖方向 |
| --- | --- | --- |
| **domain**         | `*/domain/*.py`（纯 Python） | 无外部依赖 |
| **application**    | `*/application/service.py` | 只依赖 domain + shared |
| **infrastructure** | `*/infrastructure/{models,repository}.py` | 依赖 Django ORM，实现 domain 仓储 |
| **interfaces**     | `*/interfaces/{serializers,views,urls}.py` | 只依赖 application |

领域事件：

- 接口：`shared/domain/publisher.py :: EventPublisher`
- 默认实现：`shared/infrastructure/events.py :: InMemoryEventPublisher`
- 订阅：`shared/infrastructure/listeners.py`（可在自己的 app 增加监听器）
- 切换消息中间件：只需替换 `configure_publisher` 工厂

## 与兄弟脚手架的差异

| 维度 | gin-ddd (Go) | nestjs-ddd (TypeScript) | django-ddd (Python) |
| --- | --- | --- | --- |
| Web 框架 | Gin | NestJS | Django + DRF |
| ORM | GORM | TypeORM | Django ORM |
| IoC | 手工装配 | 依赖注入 | 组合（显式装配） |
| 配置 | Viper + YAML | `@nestjs/config` + js-yaml | PyYAML + dataclass |
| 事件 | 内存 Bus + RocketMQ | `@nestjs/event-emitter` | 进程内 Publisher |
| 校验 | binding tag | class-validator | DRF Serializer |
| API 文档 | — | Swagger | drf-spectacular |

> API 契约、数据库 schema、业务规则三者在三个脚手架中完全一致，只是技术栈实现不同。

## 参考

- [Django 官方文档](https://docs.djangoproject.com/)
- [Django REST Framework](https://www.django-rest-framework.org/)
- [drf-spectacular](https://drf-spectacular.readthedocs.io/)
- 仓库相邻项目：[`gin-ddd`](https://github.com/microwind/design-patterns/tree/main/practice-projects/gin-ddd)、[`nestjs-ddd`](https://github.com/microwind/design-patterns/tree/main/practice-projects/nestjs-ddd)、[`springboot4ddd`](https://github.com/microwind/design-patterns/tree/main/practice-projects/springboot4ddd)
