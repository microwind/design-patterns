# MVVM 分层架构设计概述
MVVM（Model-View-ViewModel）通过数据绑定和观察者模式实现视图与逻辑的自动同步，降低耦合度。核心改进点：

- ​ViewModel 替代 Presenter：承担业务逻辑和数据转换，不依赖 View
- ​数据绑定机制：View 通过数据绑定自动更新，减少手动调用
​- 生命周期感知：ViewModel 管理数据生命周期，避免内存泄漏

## MVVM 结构图形示例
以客户端开发为例
```text
User Input  
    | 
    v        双向数据绑定（View <-> ViewModel）
+---------+      +------------+       +-----------+
|  View   | <==> | ViewModel  | <---> |   Model   |
+---------+      +------------+       +-----------+
       （声明式UI，无业务逻辑）         （纯数据与领域逻辑）
```

### 各层职责
- **视图层（View）**：纯UI展示（如界面布局/交互反馈），不包含业务逻辑，通过数据绑定与ViewModel同步状态
- **视图模型层（ViewModel）**：核心桥梁，封装UI相关逻辑（数据转换/命令绑定），通过双向绑定驱动View，持有Model引用但不直接操作UI
- **模型层（Model）**：独立的业务数据/逻辑（如网络请求/数据库），不感知View/ViewModel，仅通过接口供ViewModel调用

### **MVP/MVC/MVVM 分层架构的对比**

| **特性**               | **MVVM（Model-View-ViewModel）**                          | **MVP（Model-View-Presenter）**                          | **MVC（Model-View-Controller）**                          |
|------------------------|-----------------------------------------------------------|-----------------------------------------------------------|-----------------------------------------------------------|
| **架构层级**           | 三层：Model（数据）、ViewModel（状态）、View（UI）         | 三层：Model（数据）、View（界面）、Presenter（逻辑）       | 三层：Model（数据）、View（界面）、Controller（逻辑）      |
| **核心机制**           | 双向数据绑定（View ↔ ViewModel）                         | 被动视图（View 暴露接口，Presenter 主动更新）             | 单向数据流（Controller → Model → View）                 |
| **视图更新**           | 自动（数据变更触发 UI 渲染，如 Vue 的响应式系统）         | 手动（Presenter 调用 View 方法，如 `updateNum()`）        | 手动（Controller 调用 View 渲染，如 `res.render()`）     |
| **视图职责**           | 纯声明式 UI（无事件逻辑，依赖框架绑定）                   | 事件捕获 + 渲染（需实现接口，如 `OrderView.display()`）  | 模板渲染 + 简单交互（依赖框架，如 JSP/Thymeleaf）        |
| **通信方向**           | View ↔ ViewModel（双向绑定，数据驱动）                    | View ↔ Presenter（双向回调，逻辑驱动）                   | View ← Controller → Model（Controller 单向控制）        |
| **视图依赖**           | 无（View 是纯模板，ViewModel 不引用 View）                | View 需实现接口（如 Android `Activity implements View`）| View 依赖框架（如 Spring MVC 的 `ModelAndView`）        |
| **业务逻辑位置**       | ViewModel（UI 相关逻辑，如按钮点击）                      | Presenter（所有交互逻辑）                                | Controller（请求处理逻辑）                              |
| **测试性**             | ✅ 高（ViewModel 可脱离 View 测试，无 DOM 依赖）          | ✅ 高（Presenter 可 Mock View 测试）                     | ⚠️ 中（需模拟请求/响应，依赖框架上下文）                 |
| **典型场景**           | 前端单页应用（SPA）、数据驱动表单（Vue/React）            | 移动端原生开发（Android/iOS）、需严格解耦的 UI          | 传统 Web 应用（如博客、管理后台）、快速开发场景          |
| **框架示例**           | Vue（核心 MVVM）、Angular（扩展 MVVM）、React + Redux    | Android MVP、手动实现（如 JavaScript Presenter）        | Spring MVC、Ruby on Rails、Express.js                  |
| **数据流向**           | 双向：View ↔ ViewModel ↔ Model                           | 双向：View ↔ Presenter → Model（Model 被动）             | 单向：用户输入 → Controller → Model → View             |

## MVVM 的应用场景
- **数据驱动表单**（Vue 双向绑定 / React 受控组件）
- **实时交互界面**（计数器、输入框即时反馈）
- **跨平台移动应用**（Jetpack Compose/SwiftUI 原生绑定）
- **前端单页应用**（SPA）（Vue Router/React Router 状态管理）
- **复杂状态管理**（购物车多模块同步，如 Pinia/Vuex）
- **实时协作工具**（在线文档、协同编辑，自动同步 ViewModel）

## MVVM 的例子（C、Java、JavaScript、Go、Python）

### C 语言实现 MVVM
```c
/* 视图层（View）*/
// view.c
#include <stdio.h>
#include "viewmodel.h"

void display_order(OrderViewModel vm) {
    printf("Order ID: %s\nCustomer: %s\nAmount: %.2f\n",
           vm.id, vm.customer_name, vm.amount);
}

/* 视图模型层（ViewModel）*/
// viewmodel.h
#include "repository.h"

typedef struct {
    char id[10];
    char customer_name[50];
    float amount;
} OrderViewModel;

void init_viewmodel(OrderViewModel *vm, char *id) {
    Order order = find_order(id);
    snprintf(vm->id, sizeof(vm->id), "%s", order.id);
    snprintf(vm->customer_name, sizeof(vm->customer_name), "%s", order.customer_name);
    vm->amount = order.amount;
}

void save_order_viewmodel(OrderViewModel vm) {
    Order order;
    snprintf(order.id, sizeof(order.id), "%s", vm.id);
    snprintf(order.customer_name, sizeof(order.customer_name), "%s", vm.customer_name);
    order.amount = vm.amount;
    save_order(order);
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

### Java 语言实现 MVVM
```java
import java.util.List;

/* 视图层（View）*/
// OrderView.java
public class OrderView {
    public void displayOrders(List<OrderViewModel> vms) {
        for (OrderViewModel vm : vms) {
            System.out.println("Order ID: " + vm.getId() + ", Customer: " + vm.getCustomerName());
        }
    }
}

/* 视图模型层（ViewModel）*/
// OrderViewModel.java
import java.util.List;
import java.util.stream.Collectors;

public class OrderViewModel {
    private Long id;
    private String customerName;
    private java.math.BigDecimal amount;

    public OrderViewModel(Order order) {
        this.id = order.getId();
        this.customerName = order.getCustomerName();
        this.amount = order.getAmount();
    }

    public static List<OrderViewModel> fromOrders(List<Order> orders) {
        return orders.stream().map(OrderViewModel::new).collect(Collectors.toList());
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public java.math.BigDecimal getAmount() {
        return amount;
    }
}

/* 模型层（Model）*/
// Order.java
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;

@Entity
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String customerName;
    private BigDecimal amount;

    // Getters & Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}

/* 数据访问层（Repository）*/
// OrderRepository.java
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomerName(String name);
}

// OrderService.java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class OrderService {
    @Autowired
    private OrderRepository repository;

    public List<Order> getAllOrders() {
        return repository.findAll();
    }
}
```

### Go 语言实现 MVVM
```go
package main

import (
    "fmt"
    "net/http"
)

/* 视图层（View）*/
// view.go
func RenderOrder(w http.ResponseWriter, vm OrderViewModel) {
    fmt.Fprintf(w, "ID: %s\nCustomer: %s\nAmount: %.2f",
        vm.ID, vm.CustomerName, vm.Amount)
}

/* 视图模型层（ViewModel）*/
// viewmodel.go
type OrderViewModel struct {
    ID           string
    CustomerName string
    Amount       float64
}

func NewOrderViewModel(order Order) OrderViewModel {
    return OrderViewModel{
        ID:           order.ID,
        CustomerName: order.CustomerName,
        Amount:       order.Amount,
    }
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

func OrderViewModelHandler(w http.ResponseWriter, id string) {
    order := GetOrder(id)
    vm := NewOrderViewModel(order)
    RenderOrder(w, vm)
}
```


### Python 语言实现 MVVM（Flask）
```py
# 视图层（View）
# view.py
class OrderView:
    def display_orders(self, vms):
        for vm in vms:
            print(f"Order ID: {vm.id}, Customer: {vm.customer_name}")

# 视图模型层（ViewModel）
# viewmodel.py
class OrderViewModel:
    def __init__(self, order):
        self.id = order.id
        self.customer_name = order.customer_name
        self.amount = order.amount

    @classmethod
    def from_orders(cls, orders):
        return [cls(order) for order in orders]

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

# 初始化及调用示例
# main.py
from view import OrderView
from viewmodel import OrderViewModel
from service import OrderService

if __name__ == "__main__":
    service = OrderService()
    orders = service.get_all_orders()
    vms = OrderViewModel.from_orders(orders)
    view = OrderView()
    view.display_orders(vms)
```

### JavaScript 实现 MVVM（Express.js）
```javascript
/* 视图层（View）*/
// views/orders.ejs
<!DOCTYPE html>
<html>
<body>
    <h1>Orders</h1>
    <ul>
        <% vms.forEach(vm => { %>
            <li><%= vm.id %> - <%= vm.customerName %></li>
        <% }) %>
    </ul>
</body>
</html>

/* 视图模型层（ViewModel）*/
// viewmodels/OrderViewModel.js
class OrderViewModel {
    constructor(order) {
        this.id = order.id;
        this.customerName = order.customerName;
        this.amount = order.amount;
    }

    static fromOrders(orders) {
        return orders.map(order => new OrderViewModel(order));
    }
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

// controllers/orderController.js
const service = new OrderService();

async function listOrdersController(req, res) {
    const orders = await service.getAllOrders();
    const vms = OrderViewModel.fromOrders(orders);
    res.render('orders', { vms });
}

module.exports = { listOrdersController };
```

### JavaScript 前端版 MVVM
功能：点击按钮增减数值并更新视图。
1. 模型层：CounterModel 类封装数据和操作逻辑，包含数值和标题的修改方法。
2. 视图模型层：CounterViewModel 类负责关联视图和模型，实现数据和视图的双向绑定。
3. 视图层：CounterView 类负责模板渲染和事件监听转发。
```javascript
// 模型层（Model）
class CounterModel {
    constructor() {
        this.title = '点击更换标题';
        this.num = 0;
        this.observers = new Set();
    }

    changeTitle() {
        this.title = '点击更换标题' + Math.floor(Math.random() * 100);
        this.notifyObservers({ title: this.title });
    }

    increment() {
        this.num++;
        this.notifyObservers({ num: this.num });
    }

    decrement() {
        this.num--;
        this.notifyObservers({ num: this.num });
    }

    // 跟MVP很大的区别之一是观察者模式，数据变化时主动通知并更新视图
    // 可以将Observer模式更换为Proxy来实现数据变化监听
    addObserver(observer) {
        this.observers.add(observer);
    }

    notifyObservers(item) {
        this.observers.forEach(observer => observer(this, item));
    }
}

// 视图层（View）
class CounterView {
    constructor(container, viewModel) {
        this.container = container;
        this.viewModel = viewModel;
        this.render({ title: '', num: '0' });
        this.invokeEvent = function () { };
        this.bindEvents();
    }

    render(data) {
        const template = `
            <div id="app" class="counter">
                <h3 class="title" id="title">${data.title}</h3>
                <button class="dec-btn">-</button>
                <span class="num" id="num">${data.num}</span>
                <button class="inc-btn">+</button>
            </div>
        `;
        this.container.innerHTML = template;
    }

    bindEvents() {
        this.container.addEventListener('click', (event) => {
            if (event.target.classList.contains('dec-btn')) {
                this.invokeEvent('decrement');
            } else if (event.target.classList.contains('inc-btn')) {
                this.invokeEvent('increment');
            } else if (event.target.id === 'title') {
                this.invokeEvent('changeTitle');
            }
        });
    }
}

// 视图模型层（ViewModel）
class CounterViewModel {
    constructor(view, data) {
        this.data = data;
        this.view = view;
        this.init();
    }

    init() {
        this.view.render(this.data);
        this.data.addObserver(this.watchData.bind(this));
        this.view.invokeEvent = this.watchEvent.bind(this);
    }

    // 监听数据
    watchData(data, item) {
        console.log(data, item);
        // 可以根据item，单个更新
        this.view.render(data);
    }

    // 监听事件
    watchEvent(eventName) {
        switch (eventName) {
            case 'decrement':
                this.data.decrement();
                break;
            case 'increment':
                this.data.increment();
                break;
            case 'changeTitle':
                this.data.changeTitle();
                break;
            default:
                break;
        }
    }
}

const model = new CounterModel();
const view = new CounterView(document.body);
const viewModel = new CounterViewModel(view, model);
```

## 总结  
- **MVVM** 适用于前端交互复杂的场景，**数据驱动视图**，通过双向绑定减少 DOM 操作（如表单实时校验）  
- **核心分工**：View 专注 UI 渲染，ViewModel 作为桥梁（数据/逻辑），Model 管理数据状态  
- **核心优势**：视图与模型解耦，状态变更自动同步（依赖 Observer/Proxy 响应式系统）  

## 最后  
- **用MVVM吗？** 当视图行为高度依赖数据（如购物车数量动态增减），想告别 `querySelector` 时。  
- **如何判断？** 需求含「状态→视图」**自动映射**（如倒计时、搜索联想），或需双向绑定（表单联动）。  
- **如何扩展？** 轻量场景用原生 Proxy 手写（200行内），中大型项目选 Vue3/Svelte（内置响应式+模板语法糖）。  