# AI编程：设计模式与编程思想知识库大全 [English](./README_en.md)

> 设计模式Design Patterns与编程范式Programming Paradigms大全。 **充分理解设计模式和架构思想，将有助你更好地设计系统和指导AI。**


![License](https://img.shields.io/badge/License-MIT-blue.svg) ![Last Updated](https://img.shields.io/github/last-commit/microwind/design-patterns) ![Build Status](https://img.shields.io/badge/build-passing-brightgreen) [![GitHub stars](https://img.shields.io/github/stars/microwind/design-patterns.svg?style=social&label=Star)](https://github.com/microwind/design-patterns) [![GitHub forks](https://img.shields.io/github/forks/microwind/design-patterns.svg?style=social&label=Fork)](https://github.com/microwind/design-patterns/fork)

AI时代已经到来。AI可以替代人工编码工作，但难以替代人的认知与思考。只有在深入理解编程核心（数据结构 + 算法 + 设计模式 + 系统架构）的基础上，**人们才能真正驾驭AI，并让AI产生更高的效率与价值。**
## 仓库特点
- 经典设计模式与编程范式、分层架构等详解，充分注释说明，助您彻底理解系统架构与设计思想。
- 多种语言实现，包括`C` `Java` `JavaScript` `Python` `Go`、`Rust`等，体现不同语言特性，让你理解不同语言的差异。
- 结合实际场景，合理运用设计模式与架构思想，提供可参考的实践示例，让您在实际项目中运用自如。
- 不断补充和完善，打造面向工程实践的设计模式与编程思想核心资源库。与时俱进，不断打磨。

## 设计模式结构图 Design-patterns structure

<img src="https://pic2.zhimg.com/80/v2-93c9d173e41a204ae8bb8b391cbbe9b5_1440w.webp">

## 设计模式源码 Design-patterns source

### 创建型 (Creational Patterns)
| 设计模式 | 说明 | C源码 | Java源码 | JS源码 | Python源码 | TypeScript源码 | Go源码 |
| --- | --- | --- | --- | --- | --- | --- | --- |
| **[工厂模式（Factory Pattern）](./factory-pattern)** | 由工厂类提供统一的创建对象的方法。 | [C](./factory-pattern/c) | [Java](./factory-pattern/java) | [JavaScript](./factory-pattern/js) | [Python](./factory-pattern/python) | [TypeScript](./factory-pattern/ts) | [Go](./factory-pattern/go) |
| **[抽象工厂模式（Abstract Factory Pattern）](./abstract-factory)** | 是一个超级工厂，用来创建其他工厂方法。 | [C](./abstract-factory/c) | [Java](./abstract-factory/java) | [JavaScript](./abstract-factory/js) | [Python](./abstract-factory) | [TypeScript](./abstract-factory/ts) | [Go](./abstract-factory/go) |
| **[原型模式（Prototype Pattern）](./prototype-pattern)** | 利用clone()复制已有对象的实例。 | [C](./prototype-pattern/c) | [Java](./prototype-pattern/java) | [JavaScript](./prototype-pattern/js) | [Python](./prototype-pattern/python) | [TypeScript](./prototype-pattern/ts) | [Go](./prototype-pattern/go) |
| **[建造者模式（Builder Pattern）](./builder-pattern)** | 利用多个简单对象逐步构建一个复杂对象，类似建造房子。 | [C](./builder-pattern/c) | [Java](./builder-pattern/java) | [JavaScript](./builder-pattern/js) | [Python](./builder-pattern/python) | [TypeScript](./builder-pattern/ts) | [Go](./builder-pattern/go) |
| **[单例模式（Singleton Pattern）](./singleton-pattern)** | 保证创建的类只有一个实例，并提供一个访问该实例的全局节点。 | [C](./singleton-pattern/c) | [Java](./singleton-pattern/java) | [JavaScript](./singleton-pattern/js) | [Python](./singleton-pattern/python) | [TypeScript](./singleton-pattern/ts) | [Go](./singleton-pattern/go) |

### 结构型 (Structural Patterns)
| 设计模式 | 说明 | C源码 | Java源码 | JS源码 | Python源码 | TypeScript源码 | Go源码 |
| --- | --- | --- | --- | --- | --- | --- | --- |
| **[适配器模式（Adapter Pattern）](./adapter-pattern)** | 为两个不兼容的接口提供专门兼容方案。 | [C](./adapter-pattern/c) | [Java](./adapter-pattern/java) | [JavaScript](./adapter-pattern/js) | [Python](./adapter-pattern/python) | [TypeScript](./adapter-pattern/ts) | [Go](./adapter-pattern/go) |
| **[桥接模式（Bridge Pattern）](./bridge-pattern)** | 将类拆分为抽象和实现两个独立的层次结构进行解耦。 | [C](./bridge-pattern/c) | [Java](./bridge-pattern/java) | [JavaScript](./bridge-pattern/js) | [Python](./bridge-pattern/python) | [TypeScript](./bridge-pattern/ts) | [Go](./bridge-pattern/go) |
| **[组合模式（Composite Pattern）](./composite-pattern)** | 依据树形结构来组合对象，用不同组件来构建某个部分或整体对象。 | [C](./composite-pattern/c) | [Java](./composite-pattern/java) | [JavaScript](./composite-pattern/js) | [Python](./composite-pattern/python) | [TypeScript](./composite-pattern/ts) | [Go](./composite-pattern/go) |
| **[装饰器模式（Decorator Pattern）](./decorator-pattern)** | 将对象放入到一个特殊封装的对象中，赋予其新的能力。 | [C](./decorator-pattern/c) | [Java](./decorator-pattern/java) | [JavaScript](./decorator-pattern/js) | [Python](./decorator-pattern/python) | [TypeScript](./decorator-pattern/ts) | [Go](./decorator-pattern/go) |
| **[外观模式（Facade Pattern）](./facade-pattern)** | 给现有的系统添加一个高层接口，隐藏子系统的复杂性。 | [C](./facade-pattern/c) | [Java](./facade-pattern/java) | [JavaScript](./facade-pattern/js) | [Python](./facade-pattern/python) | [TypeScript](./facade-pattern/ts) | [Go](./facade-pattern/go) |
| **[享元模式（Flyweight Pattern）](./flyweight-pattern)** | 通过共享多个对象的相同状态，让有限的内存容量载入更多对象。 | [C](./flyweight-pattern/c) | [Java](./flyweight-pattern/java) | [JavaScript](./flyweight-pattern/js) | [Python](./flyweight-pattern/python) | [TypeScript](./flyweight-pattern/ts) | [Go](./flyweight-pattern/go) |
| **[代理模式（Proxy Pattern）](./proxy-pattern)** | 用一个类来代理另一个类或几个类的功能。 | [C](./proxy-pattern/c) | [Java](./proxy-pattern/java) | [JavaScript](./proxy-pattern/js) | [Python](./proxy-pattern/python) | [TypeScript](./proxy-pattern/ts) | [Go](./proxy-pattern/go) |
| **[过滤器模式（Filter Pattern）](./filter-pattern)** | 使用不同的标准条件来过滤一组对象，并通过逻辑运算的方式把各条件连接起来。 | [C](./filter-pattern/c) | [Java](./filter-pattern/java) | [JavaScript](./filter-pattern/js) | [Python](./filter-pattern/python) | [TypeScript](./filter-pattern/ts) | [Go](./filter-pattern/go) |

### 行为型 (Behavioral Patterns)
| 设计模式 | 说明 | C源码 | Java源码 | JS源码 | Python源码 | TypeScript源码 | Go源码 |
| --- | --- | --- | --- | --- | --- | --- | --- |
| **[策略模式（Strategy Pattern）](./strategy-pattern)** | 将每一个算法策略封装到接口中，根据需要设定策略，使具体实现和策略解耦。 | [C](./strategy-pattern/c) | [Java](./strategy-pattern/java) | [JavaScript](./strategy-pattern/js) | [Python](./strategy-pattern/python) | [TypeScript](./strategy-pattern/ts) | [Go](./strategy-pattern/go) |
| **[观察者模式（Observer Pattern）](./observer-pattern)** | 当主题对象的状态发生改变时，所有依赖对象都得到通知并被自动更新。 | [C](./observer-pattern/c) | [Java](./observer-pattern/java) | [JavaScript](./observer-pattern/js) | [Python](./observer-pattern/python) | [TypeScript](./observer-pattern/ts) | [Go](./observer-pattern/go) |
| **[迭代器模式（Iterator Pattern）](./iterator-pattern)** | 给数据对象构建一套按顺序访问集合对象元素的方式。 | [C](./iterator-pattern/c) | [Java](./iterator-pattern/java) | [JavaScript](./iterator-pattern/js) | [Python](./iterator-pattern/python) | [TypeScript](./iterator-pattern/ts) | [Go](./iterator-pattern/go) |
| **[模板方法模式（Template Method Pattern）](./template-pattern)** | 定义了一个抽象公开类，包含基本的算法骨架，而将一些步骤延迟到子类中。 | [C](./template-pattern/c) | [Java](./template-pattern/java) | [JavaScript](./template-pattern/js) | [Python](./template-pattern/python) | [TypeScript](./template-pattern/ts) | [Go](./template-pattern/go) |
| **[责任链模式（Chain of Responsibility Pattern）](./chain-responsibility)** | 为请求创建了一个接收者对象的链，请求沿着处理者链进行发送和处理。 | [C](./chain-responsibility/c) | [Java](./chain-responsibility/java) | [JavaScript](./chain-responsibility/js) | [Python](./chain-responsibility/python) | [TypeScript](./chain-responsibility/ts) | [Go](./chain-responsibility/go) |
| **[命令模式（Command Pattern）](./command-pattern)** | 把请求以命令的形式包裹在对象中，并传给调用对象。 | [C](./command-pattern/c) | [Java](./command-pattern/java) | [JavaScript](./command-pattern/js) | [Python](./command-pattern/python) | [TypeScript](./command-pattern/ts) | [Go](./command-pattern/go) |
| **[备忘录模式（Memento Pattern）](./memento-pattern)** | 将对象的状态捕捉住，并以备忘录的形式存储起来，以便合适的时候还原。 | [C](./memento-pattern/c) | [Java](./memento-pattern/java) | [JavaScript](./memento-pattern/js) | [Python](./memento-pattern/python) | [TypeScript](./memento-pattern/ts) | [Go](./memento-pattern/go) |
| **[状态模式（State Pattern）](./state-pattern)** | 类的行为是基于它的状态改变的，不同的状态下有不同的行为。 | [C](./state-pattern/c) | [Java](./state-pattern/java) | [JavaScript](./state-pattern/js) | [Python](./state-pattern/python) | [TypeScript](./state-pattern/ts) | [Go](./state-pattern/go) |
| **[访问者模式（Visitor Pattern）](./visitor-pattern)** | 封装访问者类，把各元素类的操作集合起来，目的是将数据结构与数据操作分离。 | [C](./visitor-pattern/c) | [Java](./visitor-pattern/java) | [JavaScript](./visitor-pattern/js) | [Python](./visitor-pattern/python) | [TypeScript](./visitor-pattern/ts) | [Go](./visitor-pattern/go) |
| **[中介者模式（Mediator Pattern）](./mediator-pattern)** | 它用一个中介对象来封装一系列的动作，以让对象之间进行交流。 | [C](./mediator-pattern/c) | [Java](./mediator-pattern/java) | [JavaScript](./mediator-pattern/js) | [Python](./mediator-pattern/python) | [TypeScript](./mediator-pattern/ts) | [Go](./mediator-pattern/go) |
| **[解释器模式（Interpreter Pattern）](./interpreter-pattern)** | 实现了一个表达式接口，并能解释特定的上下文里面的变量和语句。 | [C](./interpreter-pattern/c) | [Java](./interpreter-pattern/java) | [JavaScript](./interpreter-pattern/js) | [Python](./interpreter-pattern/python) | [TypeScript](./interpreter-pattern/ts) | [Go](./interpreter-pattern/go) |


## 设计模式7大原则 Design-principles

| **原则** | **描述** | **示例代码** | **反例代码** |
| --- | --- | --- | --- |
| **[1. 开闭原则 (Open Closed Principle, OCP)](./design-principles/open-closed)** | 对扩展开放，对修改关闭。即在不修改现有代码的基础上，通过扩展来增加功能。 | [例子](./design-principles/open-closed) | [反例](./design-principles/open-closed/java/src/OpenClosed_counter.java) |
| **[2. 单一职责原则 (Single Responsibility Principle, SRP)](./design-principles/single-responsibility)** | 一个类应该只负责一个职责，尽量避免多重职责。 | [例子](./design-principles/single-responsibility) | [反例](./design-principles/single-responsibility/java/src/SingleResponsibility_counter.java) |
| **[3. 依赖倒置原则 (Dependency Inversion Principle, DIP)](./design-principles/dependency-inversion)** | 高层模块不应依赖低层模块，二者应依赖于抽象。具体实现应依赖于抽象接口，而非直接依赖具体类。 | [例子](./design-principles/dependency-inversion) | [反例](./design-principles/dependency-inversion/java/src/DependencyInversion_counter.java) |
| **[4. 接口隔离原则 (Interface Segregation Principle, ISP)](./design-principles/interface-segregation)** | 一个类对另一个类的依赖应建立在最小的接口上，避免不必要的接口依赖。强调高内聚低耦合。 | [例子](./design-principles/interface-segregation) | [反例](./design-principles/interface-segregation/java/src/InterfaceSegregation_counter.java) |
| **[5. 组合/聚合复用原则 (Composite/Aggregate Reuse Principle, CARP)](./design-principles/composite-reuse)** | 在代码复用时，应优先考虑使用组合而非继承。大多数情况下，组合比继承更灵活。 | [例子](./design-principles/composite-reuse) | [反例](./design-principles/composite-reuse/java/src/CompositeReuse_counter.java) |
| **[6. 迪米特法则 (Law of Demeter, LoD)](./design-principles/law-of-demeter)** | 一个实体应尽量减少对其他实体的了解，降低系统模块间的耦合性。 | [例子](./design-principles/law-of-demeter) | [反例](./design-principles/law-of-demeter/java/src/LawDemeter_counter.java) |
| **[7. 里氏代换原则 (Liskov Substitution Principle, LSP)](./design-principles/liskov-substitution)** | 子类可以替代父类出现，而不会影响程序的正确性。子类可以覆盖父类的抽象方法，但不能覆盖父类的非抽象方法。 | [例子](./design-principles/liskov-substitution) | [反例](./design-principles/liskov-substitution/java/src/LiskovSubstitution_counter.java) |


## 编程范式 Programming Paradigm

| **编程范式** | **描述** | **应用场景** |
| --- | --- | --- |
| **[面向过程编程 (Procedural Programming, PP)](./programming-paradigm/procedural-programming)** | 按步骤执行程序，关注程序流程和每个操作步骤，通常通过函数或过程来实现。 | 适用于小型项目或功能明确的任务，常见于脚本编程或系统工具开发。 |
| **[面向对象编程 (Object-Oriented Programming, OOP)](./programming-paradigm/object-oriented-programming)** | 通过类和对象来组织程序，强调封装、继承、组合和多态性，关注“谁来做”而非“怎么做”。 | 适用于大型复杂项目，特别是需要良好的模块化和可扩展性时。 |
| **[封装 (Encapsulation)](./programming-paradigm/object-oriented-programming/encapsulation/)** | 将数据和操作数据的函数包装在一起，控制外部访问方式，保障数据安全。 | 用于保证数据的完整性和安全性，通常在复杂对象建模时使用。 |
| **[继承 (Inheritance)](./programming-paradigm/object-oriented-programming/inheritance/)** | 通过父类与子类关系，子类继承父类的属性和方法，从而实现代码复用。 | 用于需要构建共享代码和功能层次结构的应用，如继承多个不同类型的动物类。 |
| **[组合 (Composition)](./programming-paradigm/object-oriented-programming/composition/)** | 使用对象之间的组合关系而不是继承来重用功能。 | 适用于灵活的模块化设计，尤其当继承关系过于复杂时，组合可以提供更高的灵活性。 |
| **[多态 (Polymorphism)](./programming-paradigm/object-oriented-programming/polymorphism/)** | 允许使用相同的接口调用不同的对象，实现方法的动态绑定。 | 常用于处理多种类型对象的统一接口，特别是在事件处理和策略模式中。 |
| **[函数式编程 (Functional Programming, FP)](./programming-paradigm/functional-programming)** | 通过纯函数和不可变数据来描述程序，强调“做什么”，而非“怎么做”。 | 适用于需要高并发、高可维护性的场景，常见于数据处理和流处理系统。 |
| **[面向切面编程 (Aspect-Oriented Programming, AOP)](./programming-paradigm/aspect-oriented-programming)** | 通过预编译和动态插入，在不修改核心业务代码的情况下，将代码挂载到指定位置。 | 适用于解耦关注点（如日志、事务管理等）的场景，通常用于企业级应用。 |
| **[事件驱动编程 (Event-Driven Programming, EDP)](./programming-paradigm/edp)** | 程序的执行流程由事件驱动，通过监听和响应事件来触发操作。 | 常见于GUI开发、服务器端开发和实时应用程序（如WebSocket、UI响应等）。 |
| **[响应式编程 (Reactive Programming, RP)](./programming-paradigm/reactive-programming)** | 以数据流和变化传播为核心，通过异步数据流实现响应式操作，自动响应数据变化。 | 用于处理实时数据流、UI更新和异步操作，常见于实时监控系统和流式数据处理应用。 |


## 设计思想与分层架构设计
| **设计思想** | **描述** | **应用场景** | **示例代码** |
| --- | --- | --- | --- |
| **[MVC (Model-View-Controller)](./mvx/mvc)** | MVC是一种常见的设计模式，旨在将应用程序分为三个部分：`模型（Model）`处理数据、`视图（View）`展示用户界面、`控制器（Controller）`处理用户输入并更新模型和视图。 | 适用于需要分离表示层和业务逻辑的应用，例如Web开发、桌面应用等。通过分离关注点，提高系统的可维护性和可扩展性。 | [演示例子](./mvx/mvc) |
| **[MVP (Model-View-Presenter)](./mvx/mvp)** | MVP模式是MVC的变种，其中`控制器（Controller）`被替换为`展示者（Presenter）`。Presenter负责将数据从模型传递给视图，视图只负责显示内容，不直接与模型交互。 | 适用于需要更精细控制视图展示和与用户交互的应用，特别是当视图需要大量更新或变化时。 | [演示例子](./mvx/mvp) |
| **[MVVM (Model-View-ViewModel)](./mvx/mvvm)** | MVVM是一种设计模式，它将视图与视图的状态和业务逻辑分开。通过引入`视图模型（ViewModel）`，将视图的显示逻辑与业务逻辑分离，ViewModel负责为视图提供数据并响应用户操作。 | 适用于需要响应式数据绑定的应用，例如现代的前端开发框架（如Angular、Vue.js）和桌面应用程序（如WPF）。 | [演示例子](./mvx/mvvm) |
| **[DDD (Domain-Driven Design)](./domain-driven-design/)** | 领域驱动设计是一种软件设计方法，旨在通过将业务逻辑与实现细节分离，提高代码的可维护性和可扩展性。`DDD`将系统划分为多个层次，每个层次都有明确的职责和边界。| 适用于复杂业务逻辑和高度演化的业务需求，适用于大型企业级应用，如金融系统、供应链管理、医疗管理等。 | [演示例子](./domain-driven-design/) |


## 实践项目 Practice Projects

| 项目 | 语言 | 架构模式 | 项目特色 | 设计模式结合点 |
|---|---|---|---|---|
| **[springwind](./practice-projects/springwind)** | Java | 框架原理实现 | 从零实现IoC、DI、AOP、MVC、JDBC模板 | **单例模式**（Bean管理）、**工厂模式**（Bean创建）、**代理模式**（AOP实现）、**模板方法模式**（JdbcTemplate）、**策略模式**（多种代理策略） |
| **[knife](./practice-projects/knife)** | Java | Spring Boot 3.x + DDD | 完整签名验证、企业工具化、分层架构 | **策略模式**（签名策略）、**建造者模式**（签名构建）、**适配器模式**（数据源适配）、**装饰器模式**（拦截器增强）、**观察者模式**（事件发布） |
| **[springboot4ddd](./practice-projects/springboot4ddd)** | Java | Spring Boot 4 + DDD | Java 21、RocketMQ、多数据源、事件驱动 | **领域事件模式**（事件驱动）、**仓储模式**（数据访问）、**命令模式**（CQRS）、**外观模式**（统一API响应）、**责任链模式**（异常处理） |
| **[gin-ddd](./practice-projects/gin-ddd)** | Go | Gin + DDD | 四层架构、领域事件、RocketMQ、严格DDD | **领域事件模式**（事件驱动架构）、**仓储模式**（数据持久化）、**策略模式**（事件处理）、**工厂模式**（事件创建）、**观察者模式**（事件订阅） |
| **[gin-mvc](./practice-projects/gin-mvc)** | Go | Gin + MVC | Controller/Service/Repository分层、双数据库 | **MVC模式**（分层架构）、**适配器模式**（数据库适配）、**策略模式**（邮件服务）、**模板方法模式**（通用CRUD）、**单例模式**（配置管理） |

### 实践项目特色说明

#### 🏗️ **架构学习路径**
- **框架原理** → **DDD实践** → **现代技术栈** → **Go语言实现**
- 从底层原理到企业应用，循序渐进掌握设计模式

#### 🎯 **设计模式深度结合**
- **创建型模式**：Bean管理、对象创建、配置构建
- **结构型模式**：分层架构、数据适配、代理增强
- **行为型模式**：事件驱动、策略选择、责任处理

#### 💡 **企业级特性**
- **事件驱动架构**：RocketMQ集成，异步解耦
- **多数据源支持**：MySQL + PostgreSQL，灵活配置
- **安全机制**：完整签名验证，API安全保障
- **监控健康**：统一健康检查，依赖状态监控

#### 🔧 **技术栈覆盖**
- **Java生态**：Spring Boot 3/4、JPA、Lombok、MapStruct
- **Go生态**：Gin框架、GORM、标准库最佳实践
- **中间件**：RocketMQ、Redis、数据库连接池
- **工具链**：Maven、Go Modules、Docker支持

---

### 相关链接：AI时代程序员成长体系
- [《AI时代，人人都是Agent工程师》](https://github.com/microwind/algorithms/blob/main/start-here/AI-Era-Programmers-as-Agent-Engineers.md)
- [《AI时代，人人都是系统设计工程师》](https://github.com/microwind/algorithms/blob/main/start-here/AI-Era-Programmers-as-System-DesignersEngineers.md)
- ai-prompt - AI编程提示词库：[https://github.com/microwind/ai-prompt](https://github.com/microwind/ai-prompt)
- ai-skills - AI编程Skill库：[https://github.com/microwind/ai-skills](https://github.com/microwind/ai-skills)
- algorithms - 算法思想与数据结构：[https://github.com/microwind/algorithms](https://github.com/microwind/algorithms)
- design-patterns - 设计模式与编程范式：[https://github.com/microwind/design-patterns](https://github.com/microwind/design-patterns)

### 联系 Contact

AI时代，我们一起学习设计模式与编程思想。

**我是Jarry 李春平**

欢迎共建 Welcome to join!

- 📧 mail: `jarryli@gmail.com`  or `lichunping@buaa.edu.cn`
- 💬 wechat: `springbuild`
- QQ: **12263539**
- 🌟 如果这个项目对你有帮助，请给个 Star 支持一下！

