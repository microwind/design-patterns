# 一款Python语言Django框架DDD脚手架，适合快速搭建项目

> 一个开箱即用的 DDD（领域驱动设计）Python 脚手架，基于 Django 5 + DRF + drf-spectacular，包含双数据库、统一响应、全局异常与事件驱动示例。

## 这是什么

Django-DDD 是一个精心打造的 Python 语言 DDD 工程脚手架，帮你快速搭建符合 DDD 精髓的 Web 服务。项目内置用户与订单示例、领域事件与内存总线、多数据库路由、统一响应与全局异常处理，适合作为团队工程模板，给AI提供代码规范参考。

## 为什么要用DDD？

很多人认为 Python 没必要用 DDD，毕竟它和 Ruby、JS 一样轻巧灵活，Django 自带的 MTV（Model-Template-View）拿来就能写。确实，大多数场景下"数据驱动"的 Django 模式完全够用。

工程化无非是把接口处理、业务逻辑、数据处理区分开，让各部分各司其职，方便维护和扩展。DDD 相对更适合中大型项目：如果项目有几十个模块、上百个接口，用 DDD 设计会更合适；模块少、接口不多的话，简单分层就够了。

项目做大以后，会遇到三个常见问题：

- **业务规则散落在各处**：View 里判断状态，Model 里写校验，Service（如果有）里再来一次
- **Model 太胖**：既承担持久化，又承担业务逻辑，测试必须起 Django 才能跑
- **强耦合 Django**：业务代码离不开 `django.db.models`，替换存储引擎几乎是重构

本脚手架遵循**务实 DDD**：抓住精髓（分层 + 领域模型 + 仓储抽象 + 领域事件），不死守概念（不做 CQRS、不强制事件溯源、不要求每个业务都建聚合）。总之，是否采用 DDD 和语言无关，只跟业务规模有关。

**源码地址：**[https://github.com/microwind/design-patterns/tree/main/practice-projects/django-ddd](https://github.com/microwind/design-patterns/tree/main/practice-projects/django-ddd)

## 核心特点

- 严格 DDD 四层架构：领域层、应用层、基础设施层、接口层
- Django 5 + DRF：成熟生态 + 灵活 APIView
- 事件驱动：领域事件 + 进程内 Publisher（可替换为 Kafka/RocketMQ）
- 双数据库支持：用户库 + 订单库独立配置（默认 MySQL + PostgreSQL）
- 统一响应格式：`{ code, message, data }` + 全局异常处理器
- 领域层零 Django 依赖：纯 Python dataclass，方便单元测试
- 自动 OpenAPI：drf-spectacular 生成 Swagger UI，零警告零错误
- Docker 一键起停：MySQL + PostgreSQL + 应用整套编排

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Python | 3.10+ | 语言版本（使用 `str \| None` 等新语法） |
| Django | 5.1 | Web 框架 |
| DRF | 3.15 | REST 接口层 |
| drf-spectacular | 0.27 | OpenAPI/Swagger 生成 |
| MySQL | 8.0+ | 用户库默认 |
| PostgreSQL | 13+ | 订单库默认 |
| YAML | - | 配置文件格式 |

## 工程结构

### DDD 四层架构

```mermaid
%%{init: {'flowchart': {'nodeSpacing': 10, 'rankSpacing': 30}}}%%
flowchart LR
    subgraph Layers["DDD 四层架构"]
        UI["接口层 Interfaces<br/>DRF APIView · URLConf · Serializer"]
        APP["应用层 Application<br/>用例编排 · 事务边界 · Command/DTO"]
        DOMAIN["领域层 Domain<br/>聚合根 · 业务规则 · 领域事件"]
        INFRA["基础设施层 Infrastructure<br/>Django ORM · 事件发布器 · 数据库"]
    end

    UI --> APP
    APP --> DOMAIN
    APP -. 依赖接口 .-> INFRA
    INFRA -. 实现接口 .-> DOMAIN

    classDef ui fill:#1D9E75,stroke:#0F6E56,color:#ffffff,stroke-width:2px
    classDef app fill:#534AB7,stroke:#3C3489,color:#ffffff,stroke-width:2px
    classDef domain fill:#D85A30,stroke:#993C1D,color:#ffffff,stroke-width:2px
    classDef infra fill:#BA7517,stroke:#854F0B,color:#ffffff,stroke-width:2px

    class UI ui
    class APP app
    class DOMAIN domain
    class INFRA infra
    style Layers fill:#F5F5F5,stroke:#CCCCCC,color:#333333
```

重点：**外层只依赖内层**；基础设施通过实现领域层定义的接口（Repository、EventPublisher）完成对内供给，保证领域层零框架依赖。

### 工程结构图

```mermaid
flowchart TB
    subgraph IF["接口层 Interfaces"]
        View["DRF APIView"]
        Serializer["Serializer 请求校验"]
        URL["URLConf 路由"]
    end

    subgraph APP["应用层 Application"]
        AppService["应用服务"]
        Command["Command / DTO"]
    end

    subgraph DOMAIN["领域层 Domain"]
        Aggregate["聚合根 User · Order"]
        RepoInterface["Repository 接口"]
        Event["领域事件"]
    end

    subgraph INFRA["基础设施层 Infrastructure"]
        OrmModel["Django ORM Model"]
        RepoImpl["Repository 实现"]
        Publisher["EventPublisher 实现"]
        MySQL[("MySQL<br/>frog")]
        PG[("PostgreSQL<br/>seed")]
    end

    View --> Serializer
    View --> AppService
    AppService --> Command
    AppService --> Aggregate
    AppService --> RepoInterface
    AppService -. 发布事件 .-> Publisher
    RepoInterface -. 实现 .-> RepoImpl
    RepoImpl --> OrmModel
    OrmModel --> MySQL
    OrmModel --> PG
    Publisher -. 产生 .-> Event

    classDef if fill:#1D9E75,stroke:#0F6E56,color:#ffffff,stroke-width:2px
    classDef app fill:#534AB7,stroke:#3C3489,color:#ffffff,stroke-width:2px
    classDef domain fill:#D85A30,stroke:#993C1D,color:#ffffff,stroke-width:2px
    classDef infra fill:#BA7517,stroke:#854F0B,color:#ffffff,stroke-width:2px
    classDef db fill:#185FA5,stroke:#0C447C,color:#ffffff,stroke-width:2px
    classDef event fill:#993556,stroke:#72243E,color:#ffffff,stroke-width:2px

    class View,Serializer,URL if
    class AppService,Command app
    class Aggregate,RepoInterface domain
    class Event event
    class OrmModel,RepoImpl,Publisher infra
    class MySQL,PG db

    style IF fill:#F5F5F5,stroke:#CCCCCC,color:#333333
    style APP fill:#F5F5F5,stroke:#CCCCCC,color:#333333
    style DOMAIN fill:#F5F5F5,stroke:#CCCCCC,color:#333333
    style INFRA fill:#F5F5F5,stroke:#CCCCCC,color:#333333
```

### 工程结构列表

```
django-ddd/
├── config/
│   ├── config.yaml                        # 本地配置（与 gin-ddd 字段一致）
│   └── config.docker.yaml                 # docker compose 专用
├── docs/
│   ├── init.mysql.sql                     # 用户库 DDL + 样例数据
│   └── init.postgres.sql                  # 订单库 DDL + 样例数据
├── src/
│   ├── project/                           # Django project
│   │   ├── settings.py                    # 读 YAML → DATABASES 等
│   │   ├── urls.py                        # 根路由：健康检查 + /api + 文档
│   │   ├── routers.py                     # app_label → database 路由器
│   │   ├── wsgi.py / asgi.py
│   │   └── __init__.py
│   ├── shared/                            # 共享层
│   │   ├── apps.py                        # 启动时构建事件发布器
│   │   ├── domain/
│   │   │   ├── events.py                  # DomainEvent 基类
│   │   │   └── publisher.py               # EventPublisher 抽象
│   │   └── infrastructure/
│   │       ├── config.py                  # YAML → dataclass
│   │       ├── response.py                # 统一响应 + ApiResponseSerializer
│   │       ├── exceptions.py              # 领域异常 + 全局 handler
│   │       └── events.py                  # InMemoryEventPublisher
│   ├── user/                              # 用户 bounded context
│   │   ├── domain/{user,repository,events}.py      # 聚合根/接口/事件
│   │   ├── application/{dto,service}.py            # Command + 应用服务
│   │   ├── infrastructure/{models,repository,listeners}.py  # ORM + 仓储实现 + 订阅者
│   │   ├── interfaces/{serializers,views,urls}.py  # DRF 视图 + 路由
│   │   ├── models.py                      # re-export 供 Django 发现
│   │   └── apps.py                        # AppConfig.ready() 注册监听器
│   └── order/                             # 订单 bounded context（含状态机）
│       └── ... 同 user 结构
├── Dockerfile
├── compose.yaml
├── manage.py
├── pyproject.toml
└── requirements.txt
```

## 各层职责说明

| 层级 | 位置 | 职责 | 关键原则 |
|------|------|------|---------|
| 领域层 | `*/domain/` | 聚合根、值对象、领域事件、仓储接口 | **绝不 import django**，业务规则内聚 |
| 应用层 | `*/application/` | 用例编排，Command/DTO，事务边界 | 薄而清晰，不实现业务规则 |
| 基础设施层 | `*/infrastructure/` | ORM 模型、仓储实现、事件发布器 | 实现领域接口，技术细节下沉 |
| 接口层 | `*/interfaces/` | DRF Serializer、APIView、URL | 只处理 HTTP 交互，不含业务规则 |

## 请求生命周期

以"创建订单"为例，展示一条 HTTP 请求在四层之间的流转：

```mermaid
%%{init: {'flowchart': {'nodeSpacing': 20, 'rankSpacing': 30}}}%%
flowchart TB
    subgraph Flow["POST /api/orders 完整链路"]
        direction TB
        C["客户端<br/>发送 JSON"] --> V["接口层<br/>CreateOrderRequest 校验"]
        V --> Ctrl["OrderListView.post<br/>转换为 Command"]
        Ctrl --> App["应用层<br/>OrderApplicationService.create"]
        App --> Check["应用层<br/>检查用户是否存在"]
        Check --> Agg["领域层<br/>Order.create() 校验 + 业务规则"]
        Agg --> Repo["基础设施层<br/>DjangoOrderRepository.save"]
        Repo --> DB[("PostgreSQL<br/>INSERT orders")]
        DB --> Pub["基础设施层<br/>InMemoryEventPublisher.publish"]
        Pub --> Event["OrderCreatedEvent<br/>触发监听器"]
        Event --> Resp["接口层<br/>api_response 包装<br/>{code, message, data}"]
        Resp --> C
    end

    classDef client fill:#1D9E75,stroke:#0F6E56,color:#ffffff,stroke-width:2px
    classDef if fill:#1D9E75,stroke:#0F6E56,color:#ffffff,stroke-width:2px
    classDef app fill:#534AB7,stroke:#3C3489,color:#ffffff,stroke-width:2px
    classDef domain fill:#D85A30,stroke:#993C1D,color:#ffffff,stroke-width:2px
    classDef infra fill:#BA7517,stroke:#854F0B,color:#ffffff,stroke-width:2px
    classDef db fill:#185FA5,stroke:#0C447C,color:#ffffff,stroke-width:2px
    classDef event fill:#993556,stroke:#72243E,color:#ffffff,stroke-width:2px

    class C client
    class V,Ctrl,Resp if
    class App,Check app
    class Agg domain
    class Repo,Pub infra
    class DB db
    class Event event

    style Flow fill:#F5F5F5,stroke:#CCCCCC,color:#333333
```

关键点：

- **跨越层次由内层接口控制**：应用层调用 `OrderRepository`、`EventPublisher` 接口，具体实现由基础设施在运行时注入。
- **业务规则集中在聚合根**：`Order.create()` 内部完成金额合法性、订单号生成等校验，应用层不再写 if/else。
- **事件与主流程解耦**：`publish` 失败只写日志，不阻塞请求响应。

## 快速开始

### 1. 环境准备

- Python 3.10+（项目使用了 `str | None` 等 PEP 604 语法）
- MySQL 8.0+ 与 PostgreSQL 13+（或自行选择其一）
- Docker（可选，用于一键起停）

### 2. 初始化数据库

默认配置使用双数据库：
- 用户库：MySQL（`frog`）
- 订单库：PostgreSQL（`seed`）

> Django 的 ORM Meta 配置为 `managed = False`，**不会自动改表结构**，
> 表结构完全由 `docs/init.*.sql` 负责，行为和 gin-ddd / nestjs-ddd 对齐。

MySQL 用户库（直接使用 `docs/init.mysql.sql`）：

```sql
CREATE DATABASE IF NOT EXISTS frog CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE frog;

CREATE TABLE users (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(50)  NOT NULL UNIQUE,
    email         VARCHAR(100) NOT NULL UNIQUE,
    phone         VARCHAR(20),
    address       VARCHAR(255),
    created_time  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_time  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

PostgreSQL 订单库（与当前订单仓储字段一致）：

```sql
CREATE DATABASE seed;
\c seed;

CREATE TABLE orders (
    id            BIGSERIAL      PRIMARY KEY,
    order_no      VARCHAR(50)    NOT NULL UNIQUE,
    user_id       BIGINT         NOT NULL,
    total_amount  DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    status        VARCHAR(20)    NOT NULL DEFAULT 'PENDING',
    created_at    TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

数据库适配注意：
- 若只用一个数据库，在 `src/project/routers.py` 中把 `APP_DB_MAPPING` 全部指向 `default` 即可
- 若订单库改为 MySQL，字段 `BIGSERIAL / TIMESTAMP` 需替换为 `BIGINT AUTO_INCREMENT / TIMESTAMP`

### 3. 配置应用

编辑 `config/config.yaml`，与 gin-ddd 字段一致：

```yaml
server:
  host: "0.0.0.0"
  port: 8080
  debug: true

database:
  user:
    engine: "mysql"
    host: "localhost"
    port: 3306
    username: "root"
    password: "your_password"
    name: "frog"
  order:
    engine: "postgres"
    host: "localhost"
    port: 5432
    username: "postgres"
    password: "your_password"
    name: "seed"

logger:
  level: "INFO"

event:
  publisher: "memory"     # memory / kafka / rocketmq（预留）
```

说明：
- 环境变量 `CONFIG_FILE` 可覆盖默认配置路径，Docker 里指向 `config/config.docker.yaml`
- `event.publisher: memory` 使用进程内发布器；切换为 Kafka/RocketMQ 只需在 `configure_publisher()` 里加分支

### 4. 安装依赖

```bash
cd practice-projects/django-ddd

python -m venv .venv
source .venv/bin/activate          # Windows: .venv\Scripts\activate

pip install -r requirements.txt
```

> `mysqlclient` 依赖本地 libmysqlclient；如果装不上，可换成 `PyMySQL` 并在 `manage.py` 里加 `pymysql.install_as_MySQLdb()`。

### 5. 启动应用

```bash
# 本地开发
python manage.py runserver 0.0.0.0:8080

# 生产部署
gunicorn --chdir src project.wsgi:application --bind 0.0.0.0:8080 --workers 3
```

Docker 一键起停：

```bash
docker compose up -d --build       # 起 MySQL + PostgreSQL + App
docker compose logs -f app         # 查看日志
docker compose down -v             # 停止并清理数据卷
```

### 6. 验证接口

```bash
curl http://localhost:8080/health
curl http://localhost:8080/api/users
curl http://localhost:8080/api/orders
```

API 文档：

- Swagger UI：<http://localhost:8080/api/docs>
- OpenAPI Schema：<http://localhost:8080/api/schema>

## 如何基于脚手架开发新功能

示例：新增"商品管理"模块（`product`）。

步骤 1：新增领域聚合 `src/product/domain/product.py`

```python
from __future__ import annotations

from dataclasses import dataclass, field
from datetime import datetime


@dataclass
class Product:
    name: str
    price: float
    stock: int = 0
    id: int | None = None
    created_at: datetime = field(default_factory=datetime.utcnow)
    updated_at: datetime = field(default_factory=datetime.utcnow)

    @classmethod
    def create(cls, name: str, price: float, stock: int) -> "Product":
        if not name:
            raise ValueError("商品名不能为空")
        if price <= 0:
            raise ValueError("价格必须大于 0")
        return cls(name=name, price=price, stock=stock)
```

步骤 2：新增仓储接口 `src/product/domain/repository.py`

```python
from abc import ABC, abstractmethod

from product.domain.product import Product


class ProductRepository(ABC):
    @abstractmethod
    def save(self, p: Product) -> Product: ...
    @abstractmethod
    def find_by_id(self, pid: int) -> Product | None: ...
    @abstractmethod
    def list_all(self, offset: int, limit: int) -> tuple[list[Product], int]: ...
```

步骤 3：新增 ORM 模型与仓储实现 `src/product/infrastructure/`

```python
# models.py
from django.db import models


class ProductModel(models.Model):
    name = models.CharField(max_length=100, unique=True)
    price = models.DecimalField(max_digits=10, decimal_places=2)
    stock = models.IntegerField(default=0)
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)

    class Meta:
        app_label = "product"
        db_table = "products"
        managed = False              # 表结构由 docs/init.*.sql 管理
```

```python
# repository.py
from product.domain.product import Product
from product.domain.repository import ProductRepository
from product.infrastructure.models import ProductModel


class DjangoProductRepository(ProductRepository):
    def save(self, p: Product) -> Product:
        if p.id is None:
            row = ProductModel.objects.create(name=p.name, price=p.price, stock=p.stock)
        else:
            row = ProductModel.objects.get(pk=p.id)
            row.name, row.price, row.stock = p.name, p.price, p.stock
            row.save()
        return _to_domain(row)
    # ...
```

步骤 4：新增应用服务 `src/product/application/service.py`

```python
from product.application.dto import CreateProductCommand, ProductDTO
from product.domain.product import Product
from product.domain.repository import ProductRepository


class ProductApplicationService:
    def __init__(self, repository: ProductRepository):
        self._repo = repository

    def create(self, cmd: CreateProductCommand) -> ProductDTO:
        p = Product.create(cmd.name, cmd.price, cmd.stock)
        p = self._repo.save(p)
        return ProductDTO.of(p)
```

步骤 5：新增 HTTP 入口 `src/product/interfaces/`

`serializers.py` 用于请求字段校验、`views.py` 写 APIView、`urls.py` 暴露路由，
再在 `src/project/urls.py` 中 `include("product.interfaces.urls")`。

步骤 6：注册 AppConfig

```python
# src/product/apps.py
from django.apps import AppConfig

class ProductConfig(AppConfig):
    name = "product"
    label = "product"
    default_auto_field = "django.db.models.BigAutoField"
```

在 `src/project/settings.py::INSTALLED_APPS` 中追加 `"product.apps.ProductConfig"`。

步骤 7：新增数据库表

```sql
CREATE TABLE IF NOT EXISTS products (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100)   NOT NULL UNIQUE,
    price       DECIMAL(10, 2) NOT NULL,
    stock       INT            NOT NULL DEFAULT 0,
    created_at  TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP
                               ON UPDATE CURRENT_TIMESTAMP
);
```

## 事件驱动与领域事件

### 事件类型

订单事件：
- order.created
- order.paid
- order.shipped
- order.delivered
- order.cancelled
- order.refunded

用户事件：
- user.created
- user.email.updated
- user.phone.updated
- user.deleted

### 消息流转

领域层只定义事件接口，业务代码只和 `EventPublisher` 打交道，**实现可以在内存总线 / Kafka / RocketMQ / RabbitMQ 之间随意切换**，上层零改动。

```mermaid
flowchart LR
    subgraph Source["事件产生"]
        App["OrderApplicationService<br/>UserApplicationService"]
    end

    subgraph Abstract["事件抽象"]
        Bus["EventPublisher 接口"]
    end

    subgraph Impl["发布器实现（可替换）"]
        Mem["InMemoryEventPublisher<br/>（默认 · 进程内）"]
        MQ["KafkaEventPublisher<br/>RocketMQ / RabbitMQ"]
    end

    subgraph Handler["事件处理"]
        L1["user.infrastructure.listeners<br/>日志 · 审计 · 通知"]
        L2["order.infrastructure.listeners<br/>发货 · 下游同步"]
        Consumer["外部消费者服务"]
    end

    App -- publish --> Bus
    Bus --> Mem
    Bus -. 可切换 .-> MQ
    Mem --> L1
    Mem --> L2
    MQ --> Consumer

    classDef app fill:#534AB7,stroke:#3C3489,color:#ffffff,stroke-width:2px
    classDef bus fill:#993556,stroke:#72243E,color:#ffffff,stroke-width:2px
    classDef impl fill:#BA7517,stroke:#854F0B,color:#ffffff,stroke-width:2px
    classDef listener fill:#1D9E75,stroke:#0F6E56,color:#ffffff,stroke-width:2px
    classDef mq fill:#185FA5,stroke:#0C447C,color:#ffffff,stroke-width:2px

    class App app
    class Bus bus
    class Mem impl
    class MQ mq
    class L1,L2 listener
    class Consumer mq

    style Source fill:#F5F5F5,stroke:#CCCCCC,color:#333333
    style Abstract fill:#F5F5F5,stroke:#CCCCCC,color:#333333
    style Impl fill:#F5F5F5,stroke:#CCCCCC,color:#333333
    style Handler fill:#F5F5F5,stroke:#CCCCCC,color:#333333
```

### 订单状态机

订单的所有合法转移封装在 `Order` 聚合根内部，外部只能通过 `pay()` / `ship()` / `deliver()` / `cancel()` / `refund()` 等方法触发，任何非法转移都会抛 `DomainError`：

```mermaid
flowchart LR
    subgraph StateMachine["订单状态机"]
        direction LR
        PENDING(["PENDING<br/>待支付"])
        PAID(["PAID<br/>已支付"])
        SHIPPED(["SHIPPED<br/>已发货"])
        DELIVERED(["DELIVERED<br/>已送达"])
        CANCELLED(["CANCELLED<br/>已取消"])
        REFUNDED(["REFUNDED<br/>已退款"])
    end

    PENDING -- pay --> PAID
    PENDING -- cancel --> CANCELLED
    PAID -- ship --> SHIPPED
    PAID -- refund --> REFUNDED
    SHIPPED -- deliver --> DELIVERED
    SHIPPED -- refund --> REFUNDED

    classDef pending fill:#BA7517,stroke:#854F0B,color:#ffffff,stroke-width:2px
    classDef active fill:#534AB7,stroke:#3C3489,color:#ffffff,stroke-width:2px
    classDef success fill:#1D9E75,stroke:#0F6E56,color:#ffffff,stroke-width:2px
    classDef terminal fill:#993556,stroke:#72243E,color:#ffffff,stroke-width:2px

    class PENDING pending
    class PAID,SHIPPED active
    class DELIVERED success
    class CANCELLED,REFUNDED terminal

    style StateMachine fill:#F5F5F5,stroke:#CCCCCC,color:#333333
```

### 事件发布与消费关键点

- **发布器抽象**：`shared/domain/publisher.py :: EventPublisher`，默认 `InMemoryEventPublisher`
- **装配时机**：`SharedConfig.ready()` 构建发布器，各 BC 的 `AppConfig.ready()` 注册监听器
- **BC 自治**：`user` 只订阅自己的事件、`order` 只订阅自己的，shared 层永不知道业务事件类型
- **发布失败不影响主流程**：发布器内部 try/except，仅记录日志
- **替换中间件**：写一个 `KafkaEventPublisher(EventPublisher)`，在 `configure_publisher(kind)` 里加一个分支即可，上层代码零改动

```python
# src/shared/apps.py
class SharedConfig(AppConfig):
    name = "shared"
    def ready(self):
        kind = getattr(settings, "DDD_EVENT_PUBLISHER_KIND", "memory")
        _events.configure_publisher(kind)

# src/user/apps.py
class UserConfig(AppConfig):
    name = "user"
    def ready(self):
        from shared.infrastructure.events import get_publisher
        from user.infrastructure.listeners import register_listeners
        register_listeners(get_publisher())
```

`INSTALLED_APPS` 的顺序保证 `shared → user → order`，发布器先于订阅者初始化。

## 统一响应与全局异常

所有成功响应都经 `api_response(data, message, code, http_status)`：

```json
{ "code": 0, "message": "success", "data": { "id": 1, "name": "jarry" } }
```

领域层抛 `DomainError / NotFoundError / ValidationError`，
DRF 全局异常处理器 `shared.infrastructure.exceptions.global_exception_handler`
统一包装成相同结构，避免裸异常栈泄露到响应。

```python
# settings.py
REST_FRAMEWORK = {
    "EXCEPTION_HANDLER": "shared.infrastructure.exceptions.global_exception_handler",
}
```

## 多数据库与路由器

```python
# src/project/routers.py
APP_DB_MAPPING = {
    "user":  "default",     # MySQL (frog)
    "order": "order_db",    # PostgreSQL (seed)
}

class AppLabelRouter:
    def db_for_read(self, model, **hints):
        return APP_DB_MAPPING.get(model._meta.app_label, "default")
    db_for_write = db_for_read
    def allow_migrate(self, db, app_label, **hints):
        return db == APP_DB_MAPPING.get(app_label, "default")
```

通过 `app_label` 自动路由，业务代码感知不到多数据库的存在。
新增第三个库时，只需在 `APP_DB_MAPPING` 里加一行，不用改业务。

## 开发规范

命名建议：
- 领域聚合：名词，如 `Order`、`User`、`Product`
- 应用服务：`XxxApplicationService`
- 仓储接口：`XxxRepository`
- 仓储实现：`DjangoXxxRepository`
- 视图：`XxxView`（APIView 子类）
- 命令对象：`XxxCommand`（application/dto.py）

分层原则：
- 领域层不依赖 Django、不依赖 DRF、不依赖任何 ORM
- 应用层只做编排与事务协调，不写 if/else 业务判断
- 基础设施只提供技术实现，不暴露 ORM 对象给上层
- 接口层只负责 HTTP 交互，Serializer 只做格式校验

跨上下文原则：
- `user` 与 `order` 互不 import，跨 BC 的只读组合视图放在 `project/urls.py`
- `shared` 层不 import 任何 BC，订阅者都放在各自 BC 里

## 常用命令

```bash
# 依赖管理
pip install -r requirements.txt
pip freeze > requirements.txt

# 启动
python manage.py runserver 0.0.0.0:8080
python manage.py check
python manage.py spectacular --file docs/openapi.yaml

# Docker
docker compose up -d --build
docker compose logs -f app
docker compose down -v

# 测试（推荐）
pytest
```

## 设计模式与架构思想源码地址

[https://github.com/microwind/design-patterns](https://github.com/microwind/design-patterns)

其他语言脚手架：

- [gin-ddd (Go)](https://github.com/microwind/design-patterns/tree/main/practice-projects/gin-ddd)
- [nestjs-ddd (TypeScript)](https://github.com/microwind/design-patterns/tree/main/practice-projects/nestjs-ddd)
- [springboot4ddd (Java)](https://github.com/microwind/design-patterns/tree/main/practice-projects/springboot4ddd)
