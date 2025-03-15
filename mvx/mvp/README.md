# MVP 分层架构设计概述

模型-视图-展示器（Model-View-Presenter，简称 MVP）是 MVC 的演进架构模式，通过强化视图与业务的解耦实现更清晰的职责边界。MVP 适用于需要严格隔离界面交互与业务逻辑的复杂应用场景。

## MVP 结构图形示例
以跨平台客户端开发为例
```text
User Input  
    | 
    v        由主持人代理View和Model交互
+---------+      +-----------+       +-----------+
|  View   | <--> | Presenter | <---> |   Model   |
+---------+      +-----------+       +-----------+
```

### 各层职责
```
[View]
├── 输入事件捕获（按钮点击/表单提交）
├── 数据渲染（UI组件状态更新）
└── 接口契约实现（实现 IPaymentView）

[Presenter]
├── 事件路由（将点击事件映射到业务方法）
├── 数据转换（DTO → ViewModel）
└── 生命周期管理（绑定/解绑视图引用）

[Model]
├── 领域对象封装（Order/User实体）
├── 业务规则校验（库存检查/支付状态机）
└── 持久化抽象（定义 Repository 接口）
```
### **MVP/MVC/DDD 分层架构的对比**

| **特性**               | **MVP（Model-View-Presenter）**                          | **MVC（Model-View-Controller）**                          | **DDD（领域驱动设计）**                                  |
|------------------------|-----------------------------------------------------------|-----------------------------------------------------------|-----------------------------------------------------------|
| **架构层级**           | 三层：Model（数据）、View（界面）、Presenter（逻辑）       | 三层：Model（数据）、View（界面）、Controller（逻辑）      | 多层：接口层、应用层、领域层、基础设施层（领域为核心）  |
| **职责分离**           | View 仅 UI 渲染，Presenter 处理交互，Model 纯数据。View 与 Controller 解耦          | Controller 接收请求→调用 Model→返回 View，View 与 Controller 简单耦合 | 领域层（核心业务）与应用层（流程）分离，基础设施层封装技术 |
| **通信方向**           | View ↔ Presenter ↔ Model（双向通信，Model 被动更新）       | View ← Controller → Model（Controller 单向控制）          | 接口层→应用层→领域层→基础设施层（单向依赖，领域独立）     |
| **视图层依赖**         | View 为接口（如 Android `Activity implements View`），无框架绑定 | View 依赖具体框架（如 JSP/Thymeleaf），绑定紧密            | 视图层（接口层）与领域层解耦，通过应用层间接交互         |
| **测试性**             | ✅ 高：Presenter 可脱离 View 测试（Mock View）            | ⚠️ 中：需模拟请求/View，依赖框架上下文                     | ✅ 高：领域模型独立测试，应用层通过领域服务验证           |
| **适用场景**           | 交互复杂的 UI（移动端/桌面端），需高解耦与可测试性         | 传统 Web 快速开发（如 Spring MVC 单页面）                 | 复杂业务系统（电商/金融），需领域模型驱动的长期维护       |
| **典型框架/实现**      | Android MVP、Angular（部分思想）                          | Spring MVC、Ruby on Rails                                | Spring Boot + 领域层（自定义）、Hexagonal Architecture    |


## MVP 的应用场景
- **跨平台移动应用**（Android/iOS 共享 Presenter 逻辑）
- **数据驱动型 Web** 应用（React/Vue 组件 + TypeScript Presenter）
- **桌面富客户端应用**（WPF/MVVM 模式扩展）
- **需要严格 UI 测试的项目**（Presenter 可脱离 UI 运行测试）

## MVP 的例子（C、Java、JavaScript、Go、Python）

### C 语言实现 MVP
```c
/* 视图层（View）*/
// view.c
#include <stdio.h>
#include "presenter.h"

void display_order(Order order) {
    printf("Order ID: %s\nCustomer: %s\nAmount: %.2f\n",
           order.id, order.customer_name, order.amount);
}

/* 展示层（Presenter）*/
// presenter.c
#include "presenter.h"
#include "repository.h"

void create_order_presenter(Order order) {
    save_order(order);
}

Order get_order_presenter(char* id) {
    return find_order(id);
}

/* 模型层（Model）*/
// order.h
typedef struct {
    char id[10];
    char customer_name[50];
    float amount;
} Order;

/* 数据访问层（Repository）*/
// repository.c
#include <string.h>
#include "repository.h"

static Order orders[100];
static int count = 0;

void save_order(Order order) {
    orders[count++] = order;
}

Order find_order(char* id) {
    for (int i = 0; i < count; i++) {
        if (strcmp(orders[i].id, id) == 0) {
            return orders[i];
        }
    }
    Order empty = { "", "", 0 };
    return empty;
}
```

### Java 语言实现 MVP
```java
/* 视图层（View）*/
// OrderView.java
public interface OrderView {
    void displayOrders(List<Order> orders);
}

// OrderViewImpl.java
public class OrderViewImpl implements OrderView {
    @Override
    public void displayOrders(List<Order> orders) {
        for (Order order : orders) {
            System.out.println("Order ID: " + order.getId() + ", Customer: " + order.getCustomerName());
        }
    }
}

/* 展示层（Presenter）*/
// OrderPresenter.java
public class OrderPresenter {
    private OrderView view;
    private OrderService service;

    public OrderPresenter(OrderView view, OrderService service) {
        this.view = view;
        this.service = service;
    }

    public void listOrders() {
        List<Order> orders = service.getAllOrders();
        view.displayOrders(orders);
    }
}

/* 模型层（Model）*/
// Order.java
@Entity
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String customerName;
    private BigDecimal amount;

    // Getters & Setters
}

/* 数据访问层（Repository）*/
// OrderRepository.java
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomerName(String name);
}

// OrderService.java
@Service
public class OrderService {
    @Autowired
    private OrderRepository repository;

    public List<Order> getAllOrders() {
        return repository.findAll();
    }
}
```

### Go 语言实现 MVP
```go
/* 视图层（View）*/
// view.go
func RenderOrder(w http.ResponseWriter, order Order) {
    fmt.Fprintf(w, "ID: %s\nCustomer: %s\nAmount: %.2f",
        order.ID, order.CustomerName, order.Amount)
}

/* 展示层（Presenter）*/
// presenter.go
func OrderPresenter(w http.ResponseWriter, id string) {
    order := repository.GetOrder(id)
    RenderOrder(w, order)
}

/* 模型层（Model）*/
// order.go
type Order struct {
    ID           string
    CustomerName string
    Amount       float64
}

/* 数据访问层（Repository）*/
// repository.go
var orders = make(map[string]Order)

func GetOrder(id string) Order {
    return orders[id]
}

func SaveOrder(order Order) {
    orders[order.ID] = order
}
```


### Python 语言实现 MVP（Flask）
```py
# 视图层（View）
# view.py
class OrderView:
    def display_orders(self, orders):
        for order in orders:
            print(f"Order ID: {order.id}, Customer: {order.customer_name}")

# 展示层（Presenter）
# presenter.py
from service import OrderService

class OrderPresenter:
    def __init__(self, view, service):
        self.view = view
        self.service = service

    def list_orders(self):
        orders = self.service.get_all_orders()
        self.view.display_orders(orders)

# 模型层（Model）
# order.py
class Order:
    def __init__(self, id, customer_name, amount):
        self.id = id
        self.customer_name = customer_name
        self.amount = amount

# 数据访问层（Repository）
# repository.py
class OrderRepository:
    def __init__(self):
        self.orders = {}

    def save(self, order):
        self.orders[order.id] = order

    def get_all(self):
        return list(self.orders.values())

# 服务层（Service）
# service.py
class OrderService:
    def __init__(self):
        self.repository = OrderRepository()

    def get_all_orders(self):
        return self.repository.get_all()
```

### JavaScript 实现 MVP（Express.js）
```javascript
/* 视图层（View）*/
// views/orders.ejs
<!DOCTYPE html>
<html>
<body>
    <h1>Orders</h1>
    <ul>
        <% orders.forEach(order => { %>
            <li><%= order.id %> - <%= order.customerName %></li>
        <% }) %>
    </ul>
</body>
</html>

/* 展示层（Presenter）*/
// presenters/orderPresenter.js
const service = require('../services/orderService');

async function listOrdersPresenter(req, res) {
    const orders = await service.getAllOrders();
    res.render('orders', { orders });
}

/* 模型层（Model）*/
// models/Order.js
class Order {
    constructor(id, customerName, amount) {
        this.id = id;
        this.customerName = customerName;
        this.amount = amount;
    }
}

/* 数据访问层（Repository）*/
// repositories/orderRepository.js
class OrderRepository {
    constructor() {
        this.db = new Map();
    }

    save(order) {
        this.db.set(order.id, order);
    }

    getAll() {
        return Array.from(this.db.values());
    }
}

// services/orderService.js
const repository = new OrderRepository();

class OrderService {
    async getAllOrders() {
        return repository.getAll();
    }
}

module.exports = { listOrdersPresenter };
```

### JavaScript 前端版 MVP
功能：点击按钮增减数值并更新视图。
1. 模型层：CounterModel 类封装数据和操作逻辑，包含数值和标题的修改方法。
2. 视图层：CounterView 类负责渲染界面，与模型完全解耦，根据数值更新视图。
3. 控制层：CounterPresenter 类作为主持人，负责关联视图和模型，监听事件，并实现数据和视图的更新。
```javascript
// Model 类：封装数据逻辑
class CounterModel {
    constructor() {
        // 初始化数据
        this.title = '点击更换标题';
        this.num = 0;
    }

    // 标题操作：更换标题
    changeTitle() {
        this.title = '点击更换标题' + Math.floor(Math.random() * 100);
    }

    // 数据操作方法：增加数值
    increment() {
        this.num++;
    }

    // 数据操作方法：减少数值
    decrement() {
        this.num--;
    }
}

// View 类：处理界面渲染
class CounterView {
    template(data = {}) {
        return `
        <div class="counter">
            <h3 class="title">${data.title}</h3>
            <button class="dec-btn">-</button>
            <span class="num">${data.num}</span>
            <button class="inc-btn">+</button>
        </div>
        `;
    }

    constructor(container) {
        // 这里不绑定模型，这是跟MVC最大区别
        this.$container = container;
        this.init({title: '', num: ''});
    }

    // 初始化DOM
    init(data) {
        this.$container.innerHTML = this.template(data);
        this.$titleEl = this.$container.querySelector('.title');
        this.$numEl = this.$container.querySelector('.num');
        this.$incBtn = this.$container.querySelector('.inc-btn');
        this.$decBtn = this.$container.querySelector('.dec-btn');
    }

    // 通用render或者单独的update更新，view不知道有model
    updateView(data) {
        if (data.title != undefined) {
            this.updateTitle(data.title);
        }
        if (data.num != undefined) {
            this.updateNum(data.num);
        }
    }
    updateTitle(value) {
        this.$titleEl.textContent = value;
    }
    updateNum(value) {
        this.$numEl.textContent = value;
    }
}

// Controller 类：处理用户输入，传递模型和视图的交互，充当中介者
class CounterPresenter {
    constructor(model, view) {
        this.model = model;
        this.view = view;
        this.view.updateView(model);
        this.bindEvents();
    }

    // 绑定DOM事件
    bindEvents() {
        this.view.$titleEl.addEventListener('click', () => this.handleChangeTitle());
        this.view.$incBtn.addEventListener('click', () => this.handleIncrement());
        this.view.$decBtn.addEventListener('click', () => this.handleDecrement());
    }

    handleChangeTitle() {
        this.model.changeTitle();
        // 这里的viwerender也可以通过观察者自动更新
        this.view.updateTitle(this.model.title); // 更新视图，传递数据
    }
 
    // 事件处理：增加操作
    handleIncrement() {
        this.model.increment();
        this.view.updateNum(this.model.num); // 直接更新视图，传递数据
    }

    // 事件处理：减少操作
    handleDecrement() {
        this.model.decrement();
        this.view.updateNum(this.model.num); // 更新视图，传递数据
    }
}

// 初始化应用
const appContainer = document.body;
const model = new CounterModel();
const view = new CounterView(appContainer);
const presenter = new CounterPresenter(model, view);
```

## 总结
- **MVC** 适用于快速开发 Web 应用，强调职责分离
- **MVP** 适用于对视图和业务逻辑分离要求较高的场景，强调展示层对视图和模型的协调
- **DDD** 适用于复杂业务系统，强调领域建模
- **核心优势**：视图和业务逻辑分离，提高代码可维护性和可测试性，团队协作效率高
- **选型建议**：对于需要频繁更新视图和有较高测试需求的项目优先考虑 MVP，复杂业务系统可结合 DDD

## 最后
- **要用 MVP 吗？**  当项目需要清晰分离视图和业务逻辑，且注重测试时，MVP 是不错的选择
- **如何判断适用性？**  如果需求变化主要集中在 UI 交互和业务逻辑的解耦上，MVP 是最佳选择
- **扩展建议** ：大型项目可在 MVP 基础上增加更多的抽象层和接口，以提高代码的灵活性和可扩展性