# MVC 分层架构设计概述

模型-视图-控制器（Model-View-Controller，简称 MVC）是一种经典软件架构模式，通过职责分离实现代码的可维护性和可扩展性。MVC 适用于需要清晰分离用户界面、业务逻辑和数据管理的应用场景。

## MVC 结构图形示例
以Web后端开发为例
```text
                                请求入口
                                   |
                                   v
+-------------------+       +-------------------+
|      视图层        |<----->|    控制器层        |
|      View         |       |    Controller     |
+-------------------+       +-------------------+
                                   |
                                   v
+-------------------+       +-------------------+
|      模型层        |<----->|    数据访问层       |
|      Model        |       |    Repository     |
+-------------------+       +-------------------+
```

### 各层职责
- **视图层（View）**：处理用户界面展示和用户输入事件
- **控制器层（Controller）**：接收用户请求，协调模型和视图
- **模型层（Model）**：封装业务逻辑和数据结构
- **数据访问层（Repository）**：处理数据持久化操作

## MVC 分层架构与 DDD 分层架构的对比

| 特性 | MVC | DDD |
| --- | --- | --- |
| 主要目标 | 分离 UI、逻辑和数据 | 解决复杂领域建模问题 |
| 核心分层 | 3 层（View、Controller、Model） | 4 层（UI、应用、领域、基础设施） |
| 适用场景 | Web 应用、前端应用 | 企业级复杂业务系统 |
| 开发效率 | 快速开发，适合中小项目 | 需要领域建模，适合大型项目 |

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
功能：点击按钮增减数值并更新视图
1. 观察者模式：模型通过事件通知视图更新（如 setTimeout 异步触发回调）
2. 双向绑定：通过 input 事件监听实现模型到视图的同步
3. 分层解耦：视图不直接操作模型，控制器作为中间层处理逻辑
```javascript
// CounterModel 类，封装数据逻辑并通知数据变更，充当发布者。
class CounterModel {
    constructor() {
        this.value = 0; // 初始化计数器值
        this.listeners = []; // 存储监听数据变更的回调函数
    }

    // 增加计数器值并通知全部监听器
    add() {
        this.value++;
        this.listeners.forEach(fn => fn(this.value));
    }

    // 减少计数器值并通知全部监听器
    sub() {
        this.value--;
        this.listeners.forEach(fn => fn(this.value));
    }

    // 注册监听器
    onUpdate(fn) {
        this.listeners.push(fn);
    }
}

// CounterView 类，处理视图操作，监听 DOM 事件并触发控制器方法，充当订阅者。
class CounterView {
    constructor(controller) {
        this.$num = document.getElementById('num');
        this.$inc = document.getElementById('increase');
        this.$dec = document.getElementById('decrease');
        // 为增加按钮绑定点击事件，触发控制器的增加操作
        this.$inc.addEventListener('click', controller.increase);
        // 为减少按钮绑定点击事件，触发控制器的减少操作
        this.$dec.addEventListener('click', controller.decrease);
    }

    // 更新视图中显示的计数器值
    update(value) {
        this.$num.textContent = value;
    }
}

// 定义 CounterController 类，协调模型和视图的交互
class CounterController {
    constructor() {
        this.model = new CounterModel();
        this.view = new CounterView({
            increase: this.model.add.bind(this.model),
            decrease: this.model.sub.bind(this.model)
        });
        // 绑定视图与模型。当模型数据变更时，调用视图的更新方法
        this.model.onUpdate(this.view.update.bind(this.view));
    }
}

// 创建控制器实例，启动应用
const counterController = new CounterController();
```

## 总结
- **MVC** 适用于快速开发 Web 应用，强调职责分离
- **DDD** 适用于复杂业务系统，强调领域建模
- **核心优势**：代码可维护性强，团队协作效率高
- **选型建议**：中小型项目优先考虑 MVC，复杂业务系统可结合 DDD

## 最后
- **要用 MVC 吗？** 90% 的 Web 项目都适合 MVC 架构
- **如何判断适用性？** 如果需求变化主要集中在 UI 和流程的中小型项目，MVC 是最佳选择
- **扩展建议**：大型项目可在 MVC 基础上增加 Service 层和 DTO 对象，或者直接采用DDD架构