# DDD 分层架构设计概述

领域驱动设计（Domain-Driven Design，简称 DDD）是一种软件架构方法，旨在通过分层和领域建模的方式来管理复杂业务逻辑。DDD 主要通过分层架构来组织代码，以实现高内聚、低耦合。

## DDD 结构图形示例

```
+--------------------+
|     用户界面层       |
|   User Interface   |
+--------------------+
          |
          v
+--------------------+
|   应用服务层         |
|  Application Layer |
+--------------------+
          |
          v
+--------------------+
|       领域层        |
|    Domain Layer    |
+--------------------+
          |
          v
+----------------------+
|   基础设施层           |
| Infrastructure Layer |
+----------------------+
```

### 各层职责
- **用户界面（UI）层**：处理用户输入与展示信息。
- **应用服务层**：负责应用层流程逻辑，协调领域层的操作。
- **领域层**：实现核心业务逻辑，包括实体、值对象、聚合、领域服务等。
- **基础设施层**：提供数据库、外部 API、消息队列等技术支持。

## DDD 分层架构与 MVX 分层架构的对比

| 特性       | MVX（MVC/MVP/MVVM） | DDD（领域驱动设计） |
|------------|---------------------|---------------------|
| 主要目标   | 分离 UI、业务逻辑和数据 | 解决复杂领域建模与业务逻辑 |
| 关注点     | UI 驱动，适用于前端开发 | 领域驱动，适用于复杂业务系统 |
| 层次       | 3 层（Model、View、X） | 4 层（UI、应用、领域、基础设施） |
| 适用场景   | 前端框架、简单应用     | 企业级系统、复杂业务领域 |

## DDD 的应用场景

- **企业级系统**，如电商平台、ERP、银行系统。
- **业务逻辑复杂**，需要清晰的领域建模。
- **多个系统交互**，涉及数据库、外部 API、消息队列等。
- **团队协作开发**，需要业务人员和开发人员紧密合作。

## DDD 的例子（C、Java、JavaScript、Go、Python）

### C 语言实现 DDD
C 语言不支持 OOP，但可以用结构体和函数模块化实现 DDD。
```c
/* 用户界面层（UI Layer）*/
// ui.c
#include <stdio.h>
#include "application.h"

int main() {
    OrderService service;
    init_service(&service);
    
    create_order(&service, "1001", "Alice", 250.5);
    Order order = get_order(&service, "1001");

    printf("Retrieved Order: %s, %s, %.2f\n", order.id, order.customer_name, order.amount);
    return 0;
}

/* 应用服务层（Application Layer）*/
// order_service.c
#include <string.h>
#include "application.h"

void init_service(OrderService *service) {
    init_repository(&service->repo);
}

void create_order(OrderService *service, const char *id, const char *name, float amount) {
    Order order;
    strcpy(order.id, id);
    strcpy(order.customer_name, name);
    order.amount = amount;
    save_order(&service->repo, order);
}

Order get_order(OrderService *service, const char *id) {
    return find_order(&service->repo, id);
}

/* 领域层（Domain Layer）*/
// order.h
#ifndef DOMAIN_H
#define DOMAIN_H

typedef struct {
    char id[10];
    char customer_name[50];
    float amount;
} Order;

#endif

/* 基础设施层（Infrastructure Layer）*/
// order_repository.c
#include <stdio.h>
#include <string.h>
#include "infrastructure.h"

void init_repository(OrderRepository *repo) {
    repo->count = 0;
}

void save_order(OrderRepository *repo, Order order) {
    repo->orders[repo->count++] = order;
}

Order find_order(OrderRepository *repo, const char *id) {
    for (int i = 0; i < repo->count; i++) {
        if (strcmp(repo->orders[i].id, id) == 0) {
            return repo->orders[i];
        }
    }
    Order empty = { "", "", 0 };
    return empty;
}
```

### Java 语言实现 DDD
```java
/* 用户界面层（UI Layer）*/
// UI.java
public class UI {
    public static void main(String[] args) {
        OrderService service = new OrderService();
        service.createOrder("1001", "Alice", 250.5);
        Order order = service.getOrder("1001");

        System.out.println("Retrieved Order: " + order);
    }
}

/* 应用服务层（Application Layer）*/
// OrderService.java
class OrderService {
    private OrderRepository repository = new OrderRepository();

    public void createOrder(String id, String name, double amount) {
        Order order = new Order(id, name, amount);
        repository.save(order);
    }

    public Order getOrder(String id) {
        return repository.findById(id);
    }
}

/* 领域层（Domain Layer）*/
// Order.java
class Order {
    String id, customerName;
    double amount;

    public Order(String id, String name, double amount) {
        this.id = id;
        this.customerName = name;
        this.amount = amount;
    }

    public String toString() {
        return id + ", " + customerName + ", " + amount;
    }
}

/* 基础设施层（Infrastructure Layer）*/
// OrderRepository.java
import java.util.*;

class OrderRepository {
    private Map<String, Order> db = new HashMap<>();

    public void save(Order order) {
        db.put(order.id, order);
    }

    public Order findById(String id) {
        return db.get(id);
    }
}
```

### Go 语言实现 DDD
```go

/* 用户界面层（UI Layer）*/
// ui.go
func main() {
	repo := NewOrderRepository()
	service := NewOrderService(repo)

	service.CreateOrder("1001", "Alice", 250.5)
	order := service.GetOrder("1001")

	fmt.Println("Retrieved Order:", order)
}

/* 应用服务层（Application Layer）*/
// order_service.go
type OrderService struct {
	repo *OrderRepository
}

func NewOrderService(repo *OrderRepository) *OrderService {
	return &OrderService{repo: repo}
}

func (s *OrderService) CreateOrder(id, name string, amount float64) {
	order := Order{ID: id, CustomerName: name, Amount: amount}
	s.repo.Save(order)
}

func (s *OrderService) GetOrder(id string) Order {
	return s.repo.FindById(id)
}

/* 领域层（Domain Layer）*/
// order.go
type Order struct {
	ID           string
	CustomerName string
	Amount       float64
}

/* 基础设施层（Infrastructure Layer）*/
// order_repository.go
type OrderRepository struct {
	data map[string]Order
}

func NewOrderRepository() *OrderRepository {
	return &OrderRepository{data: make(map[string]Order)}
}

func (r *OrderRepository) Save(order Order) {
	r.data[order.ID] = order
}

func (r *OrderRepository) FindById(id string) Order {
	return r.data[id]
}
```


### Python 语言实现 DDD
```py

# 用户界面层（UI Layer）
# ui.py
from application import OrderService

service = OrderService()
service.create_order("1001", "Alice", 250.5)
order = service.get_order("1001")

print(f"Retrieved Order: {order}")


# 应用服务层（Application Layer）
# order_service.py
from domain import Order
from infrastructure import OrderRepository

class OrderService:
    def __init__(self):
        self.repository = OrderRepository()

    def create_order(self, order_id, name, amount):
        order = Order(order_id, name, amount)
        self.repository.save(order)

    def get_order(self, order_id):
        return self.repository.get(order_id)


# 领域层（Domain Layer）
# order.py
class Order:
    def __init__(self, order_id, customer_name, amount):
        self.order_id = order_id
        self.customer_name = customer_name
        self.amount = amount

    def __str__(self):
        return f"{self.order_id}, {self.customer_name}, {self.amount}"

# 基础设施层（Infrastructure Layer）
# order_repository.py
class OrderRepository:
    def __init__(self):
        self.db = {}

    def save(self, order):
        self.db[order.order_id] = order

    def get(self, order_id):
        return self.db.get(order_id, None)

```

### JavaScript (Node.js版)  实现 DDD
```javascript
/* 用户界面层（UI Layer）*/
// UI.js
const OrderService = require("./OrderService");

const service = new OrderService();
service.createOrder("1001", "Alice", 250.5);
console.log("Retrieved Order:", service.getOrder("1001"));

/* 应用服务层（Application Layer）*/
// OrderService.js
const Order = require("./Order");
const OrderRepository = require("./OrderRepository");

class OrderService {
    constructor() {
        this.repository = new OrderRepository();
    }

    createOrder(id, name, amount) {
        const order = new Order(id, name, amount);
        this.repository.save(order);
    }

    getOrder(id) {
        return this.repository.get(id);
    }
}

module.exports = OrderService;

/* 领域层（Domain Layer）*/
// Order.js
class Order {
    constructor(id, customerName, amount) {
        this.id = id;
        this.customerName = customerName;
        this.amount = amount;
    }
}

module.exports = Order;

/* 基础设施层（Infrastructure Layer）*/
// OrderRepository.js
class OrderRepository {
    constructor() {
        this.db = {};
    }

    save(order) {
        this.db[order.id] = order;
    }

    get(id) {
        return this.db[id] || null;
    }
}

module.exports = OrderRepository;
```

### JavaScript (前端版)  实现 DDD
```javascript
/* 用户界面层（UI Layer）*/
// UI.js
class View {
    constructor(selector) {
        this.$ele = document.querySelector(selector);
        this.template = `
            <div>
                <h2>订单管理</h2>
                <form id="orderForm">
                    <input type="text" id="orderId" placeholder="订单 ID" required>
                    <input type="text" id="customerName" placeholder="客户名称" required>
                    <input type="number" id="amount" placeholder="金额" required>
                    <button type="submit">提交订单</button>
                </form>
                <ul id="orderList"></ul>
            </div>
        `;
    }

    init() {
        this.$ele.innerHTML = this.template;
        this.bindEvents();
    }

    bindEvents() {
        const $form = this.$ele.querySelector("#orderForm");
        $form.addEventListener("submit", async (event) => {
            event.preventDefault();
            const id = this.$ele.querySelector("#orderId").value;
            const name = this.$ele.querySelector("#customerName").value;
            const amount = parseFloat(this.$ele.querySelector("#amount").value);

            await orderService.createOrder(id, name, amount);
            this.updateOrderList();
        });
    }

    async updateOrderList() {
        const orders = await orderService.getAllOrders();
        const $orderList = this.$ele.querySelector("#orderList");
        $orderList.innerHTML = orders
            .map(order => `<li>ID: ${order.id}, 客户: ${order.customerName}, 金额: ${order.amount}</li>`)
            .join("");
    }
}

// **初始化 UI**
const app = new View("#app");
app.init();
app.updateOrderList();


/* 应用服务层（Application Layer）*/
// OrderService.js
class OrderService {
    constructor(repository) {
        this.repository = repository;
    }

    async createOrder(id, name, amount) {
        const order = new Order(id, name, amount);
        await this.repository.save(order);
    }

    async getAllOrders() {
        return await this.repository.getAll();
    }
}

const orderService = new OrderService(new OrderRepository());

/* 领域层（Domain Layer）*/
// Order.js
class Order {
    constructor(id, customerName, amount) {
        this.id = id;
        this.customerName = customerName;
        this.amount = amount;
    }
}


/* 基础设施层（Infrastructure Layer）*/
// 前端应用里面对于数据操作主要是调用接口，此处可以改为OrderAPI.js
// OrderRepository.js
class OrderRepository {
    constructor() {
        this.apiUrl = "http://localhost:3000/api/orders"; // 后端API地址
    }

    async save(order) {
        await fetch(this.apiUrl, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(order)
        });
    }

    async getAll() {
        const response = await fetch(this.apiUrl);
        return response.json();
    }
}
```


## 总结
- **DDD 适用于复杂业务系统**，强调领域建模和高内聚低耦合。
- **MVX 适用于 UI 结构清晰的前端应用**，重点在于 UI 与业务逻辑的分离。
- **不同语言都可以实现 DDD**，关键是遵循分层架构原则。

## 最后
- **要用DDD吗？** 不是每个项目都适合DDD，是否采用DDD应当根据项目的实际情况来。
- **什么叫适合？** 就是开发团队开发和维护起来最清晰、最靠谱、最轻松就是最适合的。
