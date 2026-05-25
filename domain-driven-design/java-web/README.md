## Java 语言 `DDD` 目录结构（纯净教学版）

这是一个**严格遵循 DDD 架构理念**的 Java Web 教学示例，纯Java原生构建：

- 四层架构：`interfaces` / `application` / `domain` / `infrastructure`
- 领域层核心要素：**聚合根、子实体、值对象、领域事件、领域服务、工厂、仓储**
- 依赖倒置原则：仓储、事件发布器接口位于领域层，实现位于基础设施层
- 组合根模式：所有依赖在 `Application` 入口统一装配，分层之间不互相 `new`

**通过本工程你可以深入理解`DDD`领域驱动设计的理论与意义。**

参照：[https://github.com/microwind/ai-skills/tree/main/domain-driven-design](https://github.com/microwind/ai-skills/tree/main/domain-driven-design)

```bash
java-web/
└── src/
    ├── main/
    │   ├── java/com/microwind/javaweborder/
    │   │   ├── application/                # 应用层（用例编排，不含业务规则）
    │   │   │   ├── command/                       # 命令对象（CQRS 入门）
    │   │   │   │   ├── Command.java                # 命令标识接口
    │   │   │   │   ├── CreateOrderCommand.java
    │   │   │   │   └── UpdateOrderCommand.java
    │   │   │   ├── services/
    │   │   │   │   └── OrderService.java          # 订单应用服务：编排创建/取消/更新
    │   │   │   └── dto/
    │   │   │       └── OrderDTO.java              # 对外数据传输对象
    │   │   ├── domain/                     # 领域层（业务规则核心，不依赖任何基础设施）
    │   │   │   ├── order/                         # 订单聚合
    │   │   │   │   ├── Order.java                 # 聚合根（充血模型，封装业务规则）
    │   │   │   │   ├── OrderItem.java             # 值对象（不可变，属性等价）
    │   │   │   │   ├── OrderId.java               # 值对象：订单 ID（含生成策略）
    │   │   │   │   ├── Money.java                 # 值对象：金额（BigDecimal）
    │   │   │   │   ├── CustomerName.java          # 值对象：客户名称（含校验）
    │   │   │   │   ├── OrderStatus.java           # 状态枚举（含状态迁移规则）
    │   │   │   │   └── OrderFactory.java          # 工厂：封装聚合"出生"过程
    │   │   │   ├── service/                       # 领域服务（不便归属单实体的业务）
    │   │   │   │   └── OrderPricingService.java   # 折扣策略
    │   │   │   ├── event/                         # 领域事件（DDD 解耦核心）
    │   │   │   │   ├── DomainEvent.java           # 事件通用接口
    │   │   │   │   ├── DomainEventPublisher.java  # 事件发布器接口（依赖倒置）
    │   │   │   │   ├── OrderCreatedEvent.java
    │   │   │   │   ├── OrderCanceledEvent.java
    │   │   │   │   ├── OrderUpdatedEvent.java
    │   │   │   │   └── OrderDeletedEvent.java
    │   │   │   ├── exception/                     # 领域异常体系
    │   │   │   │   ├── OrderDomainException.java       # 异常基类
    │   │   │   │   ├── OrderNotFoundException.java     # → HTTP 404
    │   │   │   │   ├── InvalidOrderStateException.java # → HTTP 409
    │   │   │   │   └── InvalidOrderInputException.java # → HTTP 400
    │   │   │   └── repository/                    # 仓储接口（实现在基础设施层）
    │   │   │       ├── Repository.java            # 通用仓储 Repository<T, ID>
    │   │   │       └── OrderRepository.java       # 订单仓储
    │   │   ├── infrastructure/             # 基础设施层（领域接口的具体实现）
    │   │   │   ├── repository/
    │   │   │   │   └── OrderRepositoryImpl.java   # 内存版订单仓储（示意）
    │   │   │   ├── event/
    │   │   │   │   └── MessageQueueDomainEventPublisher.java # 事件发布器实现
    │   │   │   ├── message/
    │   │   │   │   └── MessageQueueService.java   # 消息中间件适配
    │   │   │   └── configuration/                 # 数据库 / JWT / 日志等配置
    │   │   ├── interfaces/                 # 接口层（HTTP / 路由 / 响应）
    │   │   │   ├── controllers/
    │   │   │   │   └── OrderController.java       # HTTP 控制器（构造器注入）
    │   │   │   ├── routes/
    │   │   │   │   ├── Router.java
    │   │   │   │   ├── OrderRoutes.java
    │   │   │   │   └── RouteHandler.java
    │   │   │   └── response/
    │   │   │       └── ResponseBody.java
    │   │   ├── middleware/                 # Servlet 过滤器（鉴权 / 日志 / 异常）
    │   │   ├── config/                     # 应用配置
    │   │   ├── utils/                      # 工具类
    │   │   ├── Application.java            # 组合根：装配各层依赖
    │   │   └── TomcatServer.java           # 嵌入式 Tomcat 启动入口
    │   └── resources/
    │       └── application.properties
    └── test/
        └── java/com/microwind/javaweborder/
            ├── TomcatServerTest.java
            └── interfaces/routes/
                └── OrderRoutesTest.java
```

## 关键 DDD 概念在本工程中的体现

### 1. 聚合根 (Aggregate Root)
`Order` 是订单聚合的唯一入口，对外不暴露任何 `setter`。所有状态变更只能通过业务方法 (`cancel()`、`update()`、`addItem()`) 完成。这是 **充血模型**。

```java
// [不推荐]贫血模型：业务规则散落在 Service
order.setStatus(OrderStatus.CANCELED);
messageQueue.send("order canceled: " + order.getId());

// [推荐]充血模型：业务规则封装在聚合根
order.cancel();   // 内部校验状态、记录领域事件
```

### 2. 值对象 (Value Object)
`OrderId`、`Money`、`CustomerName`、`OrderItem` 都是值对象：
- **不可变**（`final` 字段，没有 `setter`）
- **由属性值定义相等**（重写 `equals` / `hashCode`）
- **自带校验**（构造时检查非空、范围、格式）

这样消除了 **Primitive Obsession**（基本类型偏执）这一常见反模式。

特别说明：`OrderItem` 早期常被建模为带 ID 的子实体，但若"两个内容相同的订单项就是同一个东西"，把它建模为值对象更合适——本工程采用了这种做法，演示**实体 vs 值对象**的判定标准。

### 3. 领域事件 (Domain Event)
`OrderCreatedEvent` 等事件由聚合根在状态变更时累积，由应用服务在事务结束后通过 `DomainEventPublisher` 一次性发布。

```java
public void cancel() {
    if (!status.canTransitionTo(OrderStatus.CANCELED)) {
        throw new IllegalStateException(...);
    }
    this.status = OrderStatus.CANCELED;
    this.domainEvents.add(new OrderCanceledEvent(id)); // 累积事件
}
```

### 4. 工厂 (Factory)
`OrderFactory.create()` 封装了聚合"出生"过程：分配 ID、装配字段、记录 `OrderCreatedEvent`。把这些步骤从应用服务里移出来，让应用服务只关注用例编排。

### 5. 领域服务 (Domain Service)
`OrderPricingService` 承载"VIP 客户 9 折"这类**跨实体或策略型**的业务规则。注意它和**应用服务**的区别：
- **领域服务**：纯领域逻辑，无 IO、无事务、无 HTTP
- **应用服务**：协调用例，调用仓储 / 工厂 / 领域服务

### 6. 仓储 (Repository)
仓储接口位于领域层 (`domain/repository`)，实现位于基础设施层 (`infrastructure/repository`)。这就是**依赖倒置原则**：高层（领域）定义抽象，低层（基础设施）依赖抽象。

```java
public interface OrderRepository extends Repository<Order, OrderId> {
    List<Order> findByCustomerName(CustomerName customerName);
}
```

### 7. 组合根 (Composition Root)
`Application` 是组合根：所有依赖在此一次性装配完成，从基础设施 → 领域服务 → 应用服务 → 控制器，自底向上注入。任何一层都不需要直接 `new` 出另一层的实现，便于替换和测试。真实工程里这个角色通常由 Spring / Guice 等 IoC 容器自动接管。

### 8. 领域异常 (Domain Exception)
不用 `IllegalArgumentException` / `IllegalStateException` 这类平台异常承载业务错误，而是自建一套异常体系：

```
OrderDomainException (基类)
├── OrderNotFoundException        → HTTP 404
├── InvalidOrderStateException    → HTTP 409
└── InvalidOrderInputException    → HTTP 400
```

接口层据此精确映射 HTTP 状态码，错误信息更具业务语义。

### 9. 命令对象 (Command, CQRS 入门)
应用服务接受 `CreateOrderCommand` / `UpdateOrderCommand`，而不是裸的多参数：

```java
// [不推荐]多参数堆叠
public OrderDTO createOrder(String customerName, double amount);

// [推荐]命令对象：意图显式、签名稳定、便于扩展与序列化
public OrderDTO createOrder(CreateOrderCommand command);
```

这是 CQRS（命令查询职责分离）的入门形态：**命令承载写意图，查询承载读意图**。后续如果引入消息驱动或读模型分库，这一边界已经划好。

## 运行

```bash
# 安装
$ mvn clean install -U
# 打包
$ mvn clean package -P prod -DskipTests
# 执行
$ java -jar target/java-web-order-1.0.0.jar
# 看到最后出现成功提示
The Server has started. please visit localhost:8080.
# 通过 http://localhost:8080 访问系统

# 测试用例
$ mvn test
# 单个测试
$ mvn surefire:test -Dtest=com.microwind.javaweborder.TomcatServerTest
$ mvn surefire:test -Dtest=com.microwind.javaweborder.interfaces.routes.OrderRoutesTest
```

## Java DDD 实践要点

1. **充血聚合根**：业务规则尽量挂在聚合根上，避免贫血模型
2. **值对象代替基本类型**：用 `Money`/`OrderId` 等值对象表达领域概念，消除 Primitive Obsession
3. **依赖倒置**：仓储 / 事件发布器接口在领域层，实现在基础设施层
4. **应用服务只编排**：应用服务负责加载聚合、调业务方法、保存、发事件；不写规则
5. **工厂集中创建逻辑**：ID 生成、字段装配、初始事件记录都放在工厂里
6. **领域事件解耦**：聚合根累积事件，应用服务在边界处发布
7. **组合根装配依赖**：所有 `new` 集中在入口处，分层之间不互相依赖具体实现

## 与原版（贫血风格）的对比

| 维度 | 重构前 | 重构后 |
| --- | --- | --- |
| `Order` | 大量 `setter`，业务规则在 `OrderService` | 充血聚合根，业务规则内置 |
| `OrderItem` | 带 ID、反向引用 Order 的子实体 | 不可变值对象，属性等价 |
| 金额 | `double amount` | `Money` 值对象（`BigDecimal` 精度） |
| 订单 ID | `long`、生成方法在应用层 | `OrderId` 值对象、生成在领域工厂 |
| 取消逻辑 | `System.out.println` 报错 | 抛 `InvalidOrderStateException` |
| 异常体系 | `IllegalArgument` / `IllegalState` / 平台异常 | 领域异常 + HTTP 状态码精确映射 |
| 应用服务入参 | `createOrder(String, double)` 多参数 | `createOrder(CreateOrderCommand)` 命令对象 |
| 事件 | 应用服务手工拼字符串发消息 | 聚合根记录领域事件，发布器统一发出 |
| 依赖装配 | `OrderController` 内部 `new` Service / Repository | 组合根 `Application` 装配并注入 |
| 仓储签名 | `findById(long)` | `findById(OrderId)` |
