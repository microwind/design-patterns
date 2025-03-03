## Node.js DDD 目录结构说明

```bash
node-web/
├── index.js                      # 应用入口，启动服务器
├── package.json                  # 项目依赖和脚本管理
├── src/                           # 主要业务代码
│   ├── application/               # 应用层（协调领域逻辑，处理业务用例）
│   │   └── services/              # 业务服务层，处理业务逻辑
│   │   │   └── order-service.js   # 订单服务，调用领域层业务逻辑
│   ├── domain/                    # 领域层（核心业务逻辑和接口定义）
│   │   └── order/                 # 订单聚合
│   │   │   ├── order.js           # 订单实体（聚合根），包含核心业务逻辑
│   │   │   └── order-repository.js # 订单仓储接口或抽象类，供基础层继承实现
│   ├── infrastructure/            # 基础设施层（实现领域层定义的接口）
│   │   └── repository/            # 仓储层，数据存储相关实现
│   │   │   ├── repository.js      # 通用仓库接口（CRUD 操作封装）【可选】
│   │   │   └── order-repository-impl.js # 订单仓储实现，数据库操作
│   ├── interfaces/                # 接口层（处理外部请求，如 HTTP）
│   │   │── controllers/           # 控制器层，处理 HTTP 请求
│   │   │   └── order-controller.js # 订单相关的 HTTP 控制器
│   │   └── routes/
│   │   │   ├── router.js          # 基础路由工具设置
│   │   │   └── order-routes.js    # 订单路由地址配置
│   ├── middleware/                # 中间件（如日志、鉴权）
│   │   └── logging-middleware.js  # 日志中间件
│   ├── config/                    # 配置文件
│   │   └── server-config.js       # 服务器配置，如端口、环境变量
│   ├── utils/                     # 工具类（日志、日期处理等）
│   │   └── body-parser.js         # 请求Body解析工具
└── public/                        # 静态资源（前端相关）
    ├── index.html                 # 前端首页
    ├── styles.css                 # 样式文件
    └── script.js                  # 前端脚本
└── test/                          # 测试代码，包含单元测试和集成测试
```

### 目录结构说明：

- **src/application/services/order-service.js**: 处理订单相关的应用服务逻辑，调用领域层的业务。
- **src/domain/order/order.js**: 定义订单实体（聚合根），并包含相关的业务逻辑。
- **src/domain/order/order-repository.js**: 【可选】订单仓储接口，定义了操作订单数据的方法。
- **src/infrastructure/repository/order-repository.js**: 具体实现订单仓储接口，涉及数据存储的实际操作。
- **src/interfaces/controllers/order-controller.js**: 处理 HTTP 请求的订单相关 HTTP 处理器。
- **src/middleware**: 存放中间件，如身份验证、拦截等。
- **src/utils**: 存放工具类代码，如日志、日期处理工具等。
- **index.js**: 作为应用的入口，初始化服务并启动服务器。


## 运行
```bash
$ node ./index.js
# 展示结果 Starting server on :8080 successfully.
# 通过 http://localhost:8080 访问系统
```

## Node.js DDD（领域驱动设计）特点

### 1. 关注领域模型
DDD 强调领域模型的构建，使用 **聚合（Aggregate）**、**实体（Entity）**、**值对象（Value Object）** 组织业务逻辑。

在 Node.js 中，通常使用 JavaScript 的类（`class`）或对象字面量来定义实体和值对象：

```js
// 实体（Entity）
class User {
    constructor(id, name) {
        this.id = id;
        this.name = name;
    }
}
```

### 2. 分层架构
DDD 通常采用 分层架构，Node.js 项目可以遵循如下结构：

**领域层（Domain Layer）：**核心业务逻辑，如 domain 目录下的实体和聚合。
**应用层（Application Layer）：**用例（Use Cases）和业务流程编排。
**基础设施层（Infrastructure Layer）：**数据库、缓存、外部 API 适配等。
**接口层（Interface Layer）：**提供 HTTP、gRPC 或 CLI 接口。

### 3. 依赖倒置（Dependency Inversion）
领域层不应直接依赖基础设施，而是通过 接口（Interface） 进行依赖倒置。例如：
```js
// 领域层：定义接口
class UserRepository {
    getById(id) {
        throw new Error("Method not implemented");
    }
}
```

```js
// 基础设施层：数据库实现
class UserRepositoryImpl extends UserRepository {
    constructor(db) {
        super();
        this.db = db;
    }

    async getById(id) {
        // 数据库查询逻辑
        return await this.db.query("SELECT * FROM users WHERE id = ?", [id]);
    }
}
```

### 4. 聚合（Aggregate）管理
聚合根（Aggregate Root）管理整个聚合的生命周期：
```js
class Order {
    constructor(id, userId, items) {
        this.id = id;
        this.userId = userId;
        this.items = items;
        this.status = "Pending";
    }

    addItem(item) {
        this.items.push(item);
    }
}
```

### 5. 应用服务（Application Service）
应用服务封装领域逻辑，避免外部直接操作领域对象：
```js
class OrderService {
    constructor(orderRepository) {
        this.orderRepository = orderRepository;
    }

    async createOrder(userId, items) {
        const order = new Order(null, userId, items);
        return await this.orderRepository.save(order);
    }
}
```

### 6. 事件驱动（Event-Driven）
使用 领域事件（Domain Events） 进行解耦，在 Node.js 中可通过 事件（EventEmitter） 或 Pub/Sub 实现：
```js
const EventEmitter = require('events');

class OrderCreatedEvent {
    constructor(orderId) {
        this.orderId = orderId;
    }
}

const eventEmitter = new EventEmitter();

function publishEvent(event) {
    eventEmitter.emit('orderCreated', event);
}

eventEmitter.on('orderCreated', (event) => {
    console.log(`Order created with ID: ${event.orderId}`);
});
```

### 7. 结合 CQRS（命令查询职责分离）
DDD 可结合 CQRS（Command Query Responsibility Segregation），在 Node.js 中可用 命令（Command） 处理变更操作，用 查询（Query） 处理数据读取：
```js
class CreateOrderCommand {
    constructor(userId, items) {
        this.userId = userId;
        this.items = items;
    }
}

class OrderHandler {
    constructor(orderService) {
        this.orderService = orderService;
    }

    async handle(command) {
        return await this.orderService.createOrder(command.userId, command.items);
    }
}
```

### 总结

Node.js 的 DDD 实践强调：

1. **使用类（class）作为领域模型**（Entity、Value Object、Aggregate）
2. **依赖倒置**，通过接口定义领域层，不直接依赖基础设施
3. **使用应用服务（Service）封装业务逻辑**，避免外部直接操作领域对象
4. **事件驱动**，通过 EventEmitter 或 Pub/Sub 进行解耦
5. **结合 CQRS**，实现命令和查询分离，提高可扩展性
