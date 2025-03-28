# Comparison of MVC with MVP/MVVM/DDD Architectures Implemented in Different Languages

## Overview of MVC Layered Architecture Design

Model-View-Controller (MVC) is a classic software architecture design pattern that decouples components to create a clear and maintainable system structure with good scalability. MVC is suitable for applications that require a clear separation of user interface, business logic, and data management. Over time, architectures like MVP, MVVM, and Domain-Driven Design (DDD) have evolved from MVC, aiming to simplify complex systems and enhance understanding.

## MVC Structural Diagram Example

Taking web backend development as an example:
```text
                     User Request  
                       |  
                       v
+---------+       +-----------+      +-----------+
|  View   |  <--- | Controller| ---> |   Model   | 
+---------+       +-----------+      +-----------+
    ^                                       v
    |         Model data mapped to View     |
    ****---------------------------------****
```

### Responsibilities of Each MVC Layer

- **View Layer**: Responsible for displaying the user interface and handling user input events.
- **Controller Layer**: Receives user requests and coordinates interactions between the model and the view.
- **Model Layer**: Encapsulates business logic and data structures.

### MVC Application Scenarios

- **Web Applications**: For example, e-commerce websites, blogging systems, etc.
- **Separation of Frontend and Backend Projects**: For example, projects using RESTful APIs with frontend frameworks.
- **Desktop GUI Applications**: For example, Java Swing, C# WinForms, etc.
- **Mobile Applications**: For example, Android Activity structures.

---

## Comparison of MVC and DDD Layered Architectures

MVC focuses on **separating the interface from data**, emphasizing rapid development. DDD centers on **domain model-driven design**, concentrating on sustainable architecture for complex business systems.

### DDD Structural Diagram Example

```text
+--------------------+
|  User Interface   |
|       Layer        |
| (Controller/UI)   |
+--------------------+
          |
          v
+--------------------+
| Application Layer |
| (Service/DTO)     |
+--------------------+
          |
          v
+--------------------+
|    Domain Layer    |
| (Model/Service)   |
+--------------------+
          |
          v
+----------------------+
| Infrastructure Layer |
| (Repository/Message) |
+----------------------+
```
For source code examples in various languages implementing DDD, visit: https://github.com/microwind/design-patterns/tree/main/domain-driven-design

### Features of MVC and DDD Layered Architectures

| Feature           | MVC                                   | DDD                                         |
|-------------------|---------------------------------------|---------------------------------------------|
| **Primary Goal**  | Separation of UI, logic, and data     | Addressing complex domain modeling issues   |
| **Core Layers**   | 3 layers (View, Controller, Model)    | 4 layers (UI, Application, Domain, Infrastructure) |
| **Suitable For**  | Web applications, front-end intensive systems | Enterprise-level complex business systems (e.g., financial transactions, supply chain management) |
| **Development Efficiency** | Rapid prototyping, suitable for small to medium projects | Requires initial domain modeling, suitable for large-scale projects with long-term evolution |

## Comparison of MVC, MVP, and MVVM Layered Architectures

MVC and MVP are generally similar, differing primarily in the degree of decoupling between the View and Model. MVP achieves complete decoupling through interface isolation, while MVC allows the View to directly access the Model. The fundamental difference between MVC and MVVM lies in their data synchronization mechanisms: MVVM utilizes two-way data binding for automatic synchronization, whereas MVC relies on manual state management.

### MVP (Model-View-Presenter) Structural Diagram

```c
User Input  
    | 
    v        Interaction between View and Model is mediated by the Presenter
+---------+      +-----------+       +-----------+
|  View   | <--> | Presenter | <---> |   Model   |
+---------+      +-----------+       +-----------+
1. Primary Use Case: MVP is primarily used in frontend development, especially when a single view needs to render data from multiple sources. In such scenarios, MVP is more suitable than MVC.​

2. Component Decoupling: In the MVP architecture, the View and Model are completely decoupled. The View does not have a direct reference to the Model; instead, data is passed through the Presenter, enhancing modularity and testability.
```
Source Code Implementations in Various Languages: https://github.com/microwind/design-patterns/tree/main/mvx/mvp

### MVVM（Model-View-ViewModel）
```c
User Input  
    | 
    v         Two-way data binding between View and Model
+---------+      +-----------+      +-----------+
|  View   | ---> | ViewModel | <--> |   Model   |
+---------+      +-----------+      +-----------+
                   ^        |
                   |        v
          Data Binding (handled by Agent observing data changes)
```

MVVM Overview: MVVM (Model-View-ViewModel) is a software architectural pattern that facilitates the separation of the user interface (View) from the business logic and data (Model). It achieves this separation through data binding, allowing for automatic synchronization between the View and the ViewModel. This structure enhances modularity, testability, and maintainability within applications. 

For source code implementations in various languages, you can refer to: https://github.com/microwind/design-patterns/tree/main/mvx/mvvm

### Characteristics of MVC, MVP, and MVVM Layered Architectures

| Pattern | Control Flow Description | View-Model Coupling | Component Roles |
|---------|--------------------------|---------------------|-----------------|
| **MVC** | **Request-Driven Pattern**: <br>Controller receives View requests → operates on Model → Model directly notifies View to update; View actively listens to Model events. | Exists with some coupling; View directly binds to Model. | Controller handles logic; View displays data; Model manages data. |
| **MVP** | **Mediator Pattern**: View and Presenter interact bidirectionally: user actions trigger events → Presenter calls Model to update → Presenter notifies View to update. | Completely decoupled; View interacts only with Presenter; Model does not directly notify View. | Presenter acts as mediator; View is solely responsible for display; Model manages data. |
| **MVVM** | **Reactive Programming Pattern**: Utilizes data binding: View and ViewModel are two-way bound; operations on ViewModel automatically reflect on View. | Completely decoupled; indirect communication between View and Model via data binding. | ViewModel serves as a bridge; View is a declarative UI layer; Model is a pure data structure. |

MVC was the pioneer of layered architectural thinking, followed by the popularity of MVP, MVVM, and DDD. Comparing the code implementations of these architectures can help understand their evolution: 

## Examples of MVC Implementations (C, Java, JavaScript, Go, Python, etc.)

MVC originated from the Smalltalk language and was later popularized through Java, C++, .NET, and other languages. Not only traditional object-oriented languages can implement the MVC pattern, but various high-level languages can also realize it. It's important to note that MVC is not a technology but a concept. As long as this layered thinking is adhered to, any language can implement the MVC idea.

### Implementing MVC in C Language
```c
/* View Layer (View) */
// view.c
#include <stdio.h>
#include "controller.h"

void display_order(Order order) {
    printf("Order ID: %s\nCustomer: %s\nAmount: %.2f\n",
           order.id, order.customer_name, order.amount);
}

/* Controller Layer (Controller) */
// controller.c
#include "controller.h"
#include "repository.h"

void create_order(Order order) {
    save_order(order);
}

Order get_order(char* id) {
    return find_order(id);
}

/* Model Layer (Model) */
// order.h
typedef struct {
    char id[10];
    char customer_name[50];
    float amount;
} Order;

/* Data Access Layer (Repository) */
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

### Implementing MVC in Java
```java
/* View Layer (View) */
// Thymeleaf Template (orders.html)
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<body>
    <h1>Order List</h1>
    <ul>
        <li th:each="order : ${orders}">
            <span th:text="${order.id}"></span> - 
            <span th:text="${order.customerName}"></span>
        </li>
    </ul>
</body>
</html>

/* Controller Layer (Controller) */
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

/* Model Layer (Model) */
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

/* Data Access Layer (Repository) */
// OrderRepository.java
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomerName(String name);
}
```

### Implementing MVC in Go

```go
/* View Layer (View) */
// view.go
func RenderOrder(w http.ResponseWriter, order Order) {
    fmt.Fprintf(w, "ID: %s\nCustomer: %s\nAmount: %.2f",
        order.ID, order.CustomerName, order.Amount)
}

/* Controller Layer (Controller) */
// controller.go
func OrderHandler(w http.ResponseWriter, r *http.Request) {
    id := r.URL.Query().Get("id")
    order := repository.GetOrder(id)
    RenderOrder(w, order)
}

/* Model Layer (Model) */
// order.go
type Order struct {
    ID           string
    CustomerName string
    Amount       float64
}

/* Data Access Layer (Repository) */
// repository.go
var orders = make(map[string]Order)

func GetOrder(id string) Order {
    return orders[id]
}

func SaveOrder(order Order) {
    orders[order.ID] = order
}
```

### Implementing MVC in Python with Flask

```python
# View Layer (View)
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

# Controller Layer (Controller)
# app.py
from flask import Flask, render_template
from service import OrderService

app = Flask(__name__)
service = OrderService()

@app.route('/orders')
def list_orders():
    orders = service.get_all_orders()
    return render_template('orders.html', orders=orders)

# Model Layer (Model)
# order.py
class Order:
    def __init__(self, id, customer_name, amount):
        self.id = id
        self.customer_name = customer_name
        self.amount = amount

# Data Access Layer (Repository)
# repository.py
class OrderRepository:
    def __init__(self):
        self.orders = {}

    def save(self, order):
        self.orders[order.id] = order

    def get_all(self):
        return list(self.orders.values())
```

### Implementing MVC in JavaScript with Express.js

```javascript
/* View Layer (View) */
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

/* Controller Layer (Controller) */
// routes/orderRoutes.js
const express = require('express');
const router = express.Router();
const service = require('../services/orderService');

router.get('/orders', async (req, res) => {
    const orders = await service.getAllOrders();
    res.render('orders', { orders });
});

/* Model Layer (Model) */
// models/Order.js
class Order {
    constructor(id, customerName, amount) {
        this.id = id;
        this.customerName = customerName;
        this.amount = amount;
    }
}

/* Data Access Layer (Repository) */
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

### JavaScript Frontend MVC Implementation
Function: Click the buttons to increase or decrease a value and update the view.
1. **Model Layer**: The `CounterModel` class encapsulates the data and logic operations, including methods for changing the value and the title.
2. **View Layer**: The `CounterView` class is responsible for rendering the interface, binding model data, and updating the view according to the model's state.
3. **Controller Layer**: The `CounterController` class serves as an intermediary, binding the view and model, listening for events, and updating the data and view.

```javascript
// Model class: Encapsulates data logic
class CounterModel {
    constructor() {
        // Initialize data
        this.title = 'Click to change the title';
        this.num = 0;
    }

    // Title operation: Change the title
    changeTitle() {
        this.title = 'Click to change the title' + Math.floor(Math.random() * 100);
    }

    // Data operation method: Increment the value
    increment() {
        this.num++;
    }

    // Data operation method: Decrement the value
    decrement() {
        this.num--;
    }
}

// View class: Handles interface rendering
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
        this.model = model; // Bind the model, the biggest difference from MVP
        this.$container = container;
        this.init();
    }

    // Initialize DOM
    init() {
        this.$container.innerHTML = this.template(this.model);
        this.$titleEl = this.$container.querySelector('.title');
        this.$numEl = this.$container.querySelector('.num');
        this.$incBtn = this.$container.querySelector('.inc-btn');
        this.$decBtn = this.$container.querySelector('.dec-btn');
    }

    // Update the view method
    render() {
        // The fields to update can be determined by whether the data has changed
        const data = this.model;
        this.$titleEl.textContent = data.title;
        this.$numEl.textContent = data.num;
    }
}

// Controller class: Handles user input
class CounterController {
    constructor(model, view) {
        this.model = model;
        this.view = view;
        this.bindEvents();
    }

    // Bind DOM events
    bindEvents() {
        this.view.$titleEl.addEventListener('click', () => this.changeTitleHandle());
        this.view.$incBtn.addEventListener('click', () => this.incrementHandle());
        this.view.$decBtn.addEventListener('click', () => this.decrementHandle());
    }

    changeTitleHandle() {
        this.model.changeTitle();
        this.view.render(); // Directly update the view, no need to pass the model
    }

    // Event handler: Increment operation
    incrementHandle() {
        this.model.increment();
        this.view.render(); // Directly update the view, no need to pass the model
    }

    // Event handler: Decrement operation
    decrementHandle() {
        this.model.decrement();
        this.view.render(); // Directly update the view, no need to pass the model
    }
}

// Initialize the application
const appContainer = document.body;
const model = new CounterModel();
const view = new CounterView(model, appContainer);
const controller = new CounterController(model, view);
```

## Summary

- **MVC** is suitable for rapid development of web applications, emphasizing separation of concerns.
- **MVP** is suitable for scenarios requiring a high degree of separation between the view and business logic, emphasizing the coordination between the presentation layer and the view and model.
- **MVVM** is suitable for applications with complex interactions between the view and model, utilizing data binding mechanisms to automatically synchronize the states of the view and model.
- **DDD** is suitable for complex business systems, emphasizing domain modeling.
- **Core Advantages**: Strong code maintainability and high team collaboration efficiency.
- **Selection Recommendations**: For small to medium-sized projects, consider MVC; for complex business systems, DDD can be combined.

## Final Thoughts

- **Should we use MVC?** 90% of web projects are suitable for the MVC architecture.
- **How to assess suitability?** If requirements changes mainly focus on UI and processes in small to medium-sized projects, MVC is the best choice.
- **Expansion Suggestions**: Large projects can add Service layers and DTO objects on top of MVC, or directly adopt DDD architecture.

## More design and architecture source code
[https://github.com/microwind/design-patterns](https://github.com/microwind/design-patterns)


https://medium.com/@microwind/comparison-of-mvc-with-mvp-mvvm-ddd-architectures-implemented-in-different-languages-5369a1383f49
