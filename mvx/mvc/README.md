# MVC 分层架构设计概述

模型-视图-控制器（Model-View-Controller，简称 MVC）是一种经典软件架构模式，通过职责分离实现代码的可维护性和可扩展性。MVC 适用于需要清晰分离用户界面、业务逻辑和数据管理的应用场景。

## MVC 结构图形示例
以Web后端开发为例
```text
                     用户请求  
                       |  
                       v
+---------+       +-----------+      +-----------+
|  View   |  <--- | Controller| ---> |   Model   | 
+---------+       +-----------+      +-----------+
    ^                                      v
    |            Model数据映射到View         |
    ****--------------------------------****
```

### 各层职责
- **视图层（View）**：处理用户界面展示和用户输入事件
- **控制器层（Controller）**：接收用户请求，协调模型和视图
- **模型层（Model）**：封装业务逻辑和数据结构

## MVC 分层架构与 DDD 分层架构的对比

| 特性 | MVC | DDD |
| --- | --- | --- |
| 主要目标 | 分离 UI、逻辑和数据 | 解决复杂领域建模问题 |
| 核心分层 | 3 层（View、Controller、Model） | 4 层（UI、应用、领域、基础设施） |
| 适用场景 | Web 应用、前端应用 | 企业级复杂业务系统 |
| 开发效率 | 快速开发，适合中小项目 | 需要领域建模，适合大型项目 |

## MVC与MVP的主要区别
MVC与MVP总体上一致，只是在View与Model是否完全解耦上有差别。
### MVC
单向控制：Controller 接收 View 请求 → 操作 Model → Model 直接通知 View 更新。
View 主动：View 直接监听 Model 事件（如 model.onUpdate(this.view.update)）。
View与Model有简单耦合，View绑定Model。

### MVP
双向通信：View 和 Presenter 双向交互（事件触发 → 数据更新）。
Model 被动：Model 不直接通知 View，需通过 Presenter 中转。
View与Model无耦合，View不知道有Model。

## MVC 的应用场景
- **Web 应用程序**（如电商网站、博客系统）
- **前后端分离项目**（RESTful API + 前端框架）
- **桌面 GUI 应用**（Java Swing、C# WinForms）
- **移动端应用**（Android Activity 结构）

## MVC 的例子（C、Java、JavaScript、Go、Python）

### C 语言实现 MVC
```c
/* 视图层（View）*/
// view.c
#include <stdio.h>
#include "controller.h"

void display_order(Order order) {
    printf("Order ID: %s\nCustomer: %s\nAmount: %.2f\n",
           order.id, order.customer_name, order.amount);
}

/* 控制器层（Controller）*/
// controller.c
#include "controller.h"
#include "repository.h"

void create_order(Order order) {
    save_order(order);
}

Order get_order(char* id) {
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

### Java 语言实现 MVC
```java
/* 视图层（View）*/
// Thymeleaf 模板 (orders.html)
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<body>
    <h1>订单列表</h1>
    <ul>
        <li th:each="order : ${orders}">
            <span th:text="${order.id}"></span> - 
            <span th:text="${order.customerName}"></span>
        </li>
    </ul>
</body>
</html>

/* 控制器层（Controller）*/
// OrderController.java
@Controller
@RequestMapping("/orders")
public class OrderController {
    private final OrderService service;

    @Autowired
    public OrderController(OrderService service) {
        this.service = service;
    }

    @GetMapping
    public String listOrders(Model model) {
        model.addAttribute("orders", service.getAllOrders());
        return "orders";
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
```

### Go 语言实现 MVC
```go
/* 视图层（View）*/
// view.go
func RenderOrder(w http.ResponseWriter, order Order) {
    fmt.Fprintf(w, "ID: %s\nCustomer: %s\nAmount: %.2f",
        order.ID, order.CustomerName, order.Amount)
}

/* 控制器层（Controller）*/
// controller.go
func OrderHandler(w http.ResponseWriter, r *http.Request) {
    id := r.URL.Query().Get("id")
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


### Python 语言实现 MVC（Flask）
```py
# 视图层（View）
# templates/orders.html
<html>
<body>
    <h1>Orders</h1>
    <ul>
        {% for order in orders %}
            <li>{{ order.id }} - {{ order.customer_name }}</li>
        {% endfor %}
    </ul>
</body>
</html>

# 控制器层（Controller）
# app.py
from flask import Flask, render_template
from service import OrderService

app = Flask(__name__)
service = OrderService()

@app.route('/orders')
def list_orders():
    orders = service.get_all_orders()
    return render_template('orders.html', orders=orders)

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
```

### JavaScript 实现 MVC（Express.js）
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

/* 控制器层（Controller）*/
// routes/orderRoutes.js
const express = require('express');
const router = express.Router();
const service = require('../services/orderService');

router.get('/orders', async (req, res) => {
    const orders = await service.getAllOrders();
    res.render('orders', { orders });
});

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
```

### JavaScript 前端版 MVC
功能：点击按钮增减数值并更新视图。
1. 模型层：CounterModel 类封装数据和操作逻辑，包含数值和标题的修改方法。
2. 视图层：CounterView 类负责渲染界面，绑定模型数据，根据模型状态更新视图。
3. 控制层：CounterController 类作为中间层，绑定视图和模型，监听事件，实现数据和视图的更新。
```javascript
// Model 类：封装数据逻辑
class CounterModel {
    constructor() {
        // 初始化数据
        this.title = '点击更换标题';
        this.num = 0;
    }

    // 标题操作：增加标题
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

    constructor(model, container) {
        this.model = model; // 绑定模型，这是跟MVP最大区别
        this.$container = container;
        this.init();
    }

    // 初始化DOM
    init() {
        this.$container.innerHTML = this.template(this.model);
        this.$titleEl = this.$container.querySelector('.title');
        this.$numEl = this.$container.querySelector('.num');
        this.$incBtn = this.$container.querySelector('.inc-btn');
        this.$decBtn = this.$container.querySelector('.dec-btn');
    }

    // 更新视图方法
    render() {
        // 可以根据数据是否有变化来确定要更新哪个字段
        const data = this.model
        this.$titleEl.textContent = data.title;
        this.$numEl.textContent = data.num;
    }
}

// Controller 类：处理用户输入
class CounterController {
    constructor(model, view) {
        this.model = model;
        this.view = view;
        this.bindEvents();
    }

    // 绑定DOM事件
    bindEvents() {
        this.view.$titleEl.addEventListener('click', () => this.changeTitleHandle());
        this.view.$incBtn.addEventListener('click', () => this.incrementHandle());
        this.view.$decBtn.addEventListener('click', () => this.decrementHandle());
    }

    changeTitleHandle() {
        this.model.changeTitle();
        this.view.render(); // 直接更新视图，不必传递model
    }
 
    // 事件处理：增加操作
    incrementHandle() {
        this.model.increment();
        this.view.render(); // 直接更新视图，不必传递model
    }

    // 事件处理：减少操作
    decrementHandle() {
        this.model.decrement();
        this.view.render(); // 直接更新视图，不必传递model
    }
}

// 初始化应用
const appContainer = document.body;
const model = new CounterModel();
const view = new CounterView(model, appContainer);
const controller = new CounterController(model, view);
```

## 总结
- **MVC** 适用于快速开发 Web 应用，强调职责分离
- **MVP** 适用于对视图和业务逻辑分离要求较高的场景，强调展示层对视图和模型的协调
- **DDD** 适用于复杂业务系统，强调领域建模
- **核心优势**：代码可维护性强，团队协作效率高
- **选型建议**：中小型项目优先考虑 MVC，复杂业务系统可结合 DDD

## 最后
- **要用 MVC 吗？** 90% 的 Web 项目都适合 MVC 架构
- **如何判断适用性？** 如果需求变化主要集中在 UI 和流程的中小型项目，MVC 是最佳选择
- **扩展建议**：大型项目可在 MVC 基础上增加 Service 层和 DTO 对象，或者直接采用DDD架构