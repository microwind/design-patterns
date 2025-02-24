## Python DDD 目录结构说明

```bash
python-web/
├── README.md                      # 项目说明文档
├── requirements.txt               # 项目依赖管理
├── src/                           # 项目源码目录
│   ├── app.py                     # 应用入口
│   ├── __init__.py                # 包初始化文件
│   ├── application/               # 应用层（协调领域逻辑，处理业务用例）
│   │   ├── services/              # 业务服务层，处理业务逻辑
│   │   │   └── order_service.py   # 订单服务，调用领域层业务逻辑
│   ├── domain/                    # 领域层（核心业务逻辑和接口定义）
│   │   ├── order/                 # 领域模型（实体、值对象、聚合根）
│   │   │   └── order.py           # 订单实体（聚合根）
│   │   │   └── order_repository.py  # 订单仓储抽象类，充当接口
│   ├── infrastructure/            # 基础设施层（实现领域层定义的接口）
│   │   ├── repositories/          # 仓储实现
│   │   │   └── order_repository.py # 订单仓储实现
│   ├── interfaces/                # 接口层（处理外部请求，如 HTTP）
│   │   ├── controllers/           # 控制器层，处理 HTTP 请求
│   │   │   └── order_controller.py  # 订单相关的 HTTP 控制器
│   │   ├── routes/                # 路由配置
│   │   │   └── order_routes.py    # 订单路由配置
│   │   │   └── router.py          # 通用路由工具
│   └── middleware/                # 中间件（例如：鉴权、拦截、认证等） 
│   │   └── logging_middleware.py  # 自定义日志中间件
│   └── utils/                     # 工具类（响应、日期处理等）
│       ├── response_helper.py     # 辅助实用工具
├── tests/                         # 测试代码
└── scripts/                       # 脚本目录
```

## 目录结构说明：
**src/application/services/order_service.py:** 处理订单相关的应用服务逻辑，调用领域层的业务。
**src/domain/order/order.py:** 定义订单实体（聚合根），并包含相关的业务逻辑。
**src/domain/order/order_repository.py:** 订单仓储接口，定义了操作订单数据的方法。
**src/infrastructure/repository/order_repository.py:** 具体实现订单仓储接口，涉及数据存储的实际操作。
**src/interfaces/controllers/order_controller.py:**  处理 HTTP 请求的订单相关 HTTP 处理器。
**src/middleware:** 存放中间件，如身份验证、拦截等。
**src/utils:** 存放工具类代码，如日志、日期处理工具等。
**src/app.py:** 作为应用的入口，初始化服务并启动服务器。


## 运行
```bash
$ pip3 install -r requirements.txt
$ python src/app.py 
# 展示结果 Starting server on :8080 successfully.
# 通过 http://localhost:8080 访问系统
$ # 执行测试，查看测试结果
$ python tests/interfaces/routes/order_routes.test.py
```

## Python DDD（领域驱动设计）特点

### 1. 关注领域模型
DDD 强调领域模型的构建，使用 **聚合（Aggregate）**、**实体（Entity）**、**值对象（Value Object）** 组织业务逻辑。

在 Python 中，通常使用类（`class`）来定义实体和值对象：

```python
# 实体（Entity）
class User:
    def __init__(self, user_id, name):
        self.id = user_id
        self.name = name
```

### 2. 分层架构
DDD 通常采用分层架构，Python 项目可以遵循如下结构：
**领域层（Domain Layer）：** 核心业务逻辑，如 domain 目录下的实体和聚合。
**应用层（Application Layer）：** 用例（Use Cases）和业务流程编排。
**基础设施层（Infrastructure Layer）：** 数据库、缓存、外部 API 适配等。 
**接口层（Interface Layer）：** 提供 HTTP、gRPC 或 CLI 接口。

### 3. 依赖倒置（Dependency Inversion）
领域层不应直接依赖基础设施，而是通过接口（Interface）进行依赖倒置。例如：
```py
# 领域层：定义接口
class UserRepository:
    def get_by_id(self, user_id):
        raise NotImplementedError("Method not implemented")
```

```py
# 基础设施层：数据库实现
class UserRepositoryImpl(UserRepository):
    def __init__(self, db):
        self.db = db

    def get_by_id(self, user_id):
        # 数据库查询逻辑
        return self.db.query("SELECT * FROM users WHERE id = %s", (user_id,))
```

### 4. 聚合（Aggregate）管理
聚合根（Aggregate Root）管理整个聚合的生命周期：
```py
class Order:
    def __init__(self, order_id, user_id, items):
        self.id = order_id
        self.user_id = user_id
        self.items = items
        self.status = "Pending"

    def add_item(self, item):
        self.items.append(item)
```

### 5. 应用服务（Application Service）
应用服务封装领域逻辑，避免外部直接操作领域对象：
```py
class OrderService:
    def __init__(self, order_repository):
        self.order_repository = order_repository

    def create_order(self, user_id, items):
        order = Order(None, user_id, items)
        return self.order_repository.save(order)
```

### 6. 事件驱动（Event-Driven）
使用 领域事件（Domain Events）进行解耦，在 Python 中可通过 EventEmitter（pyee 库） 或发布/订阅模式实现：
```py
from pyee import EventEmitter

class OrderCreatedEvent:
    def __init__(self, order_id):
        self.order_id = order_id

event_emitter = EventEmitter()

def publish_event(event):
    event_emitter.emit('order_created', event)

event_emitter.on('order_created', lambda event: print(f"Order created with ID: {event.order_id}"))
```

### 7. 结合 CQRS（命令查询职责分离）
DDD 可结合 CQRS（Command Query Responsibility Segregation），在 Python 中可用命令（Command）处理变更操作，用查询（Query）处理数据读取：
```py
class CreateOrderCommand:
    def __init__(self, user_id, items):
        self.user_id = user_id
        self.items = items

class OrderHandler:
    def __init__(self, order_service):
        self.order_service = order_service

    def handle(self, command):
        return self.order_service.create_order(command.user_id, command.items)
```

### 总结
Python 中的 DDD 实践强调：
1. 使用类（class）作为领域模型（Entity、Value Object、Aggregate）
2. 依赖倒置，通过接口定义领域层，不直接依赖基础设施
3. 使用应用服务（Service）封装业务逻辑，避免外部直接操作领域对象
4. 事件驱动，通过 EventEmitter 或发布/订阅模式进行解耦
5. 结合 CQRS，实现命令和查询分离，提高可扩展性