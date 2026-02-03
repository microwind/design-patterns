# 面向对象设计7大原则正例与反例实战-一文彻底搞懂

## 前言
面向对象设计的7大原则旨在提高软件系统的**可维护性、可扩展性和复用性**，主要包括：`开闭原则（核心）`、`单一职责`、`里氏替换`、`依赖倒置`、`接口隔离`、`迪米特法则`和`合成复用`原则。它们通过高内聚、低耦合的设计，使系统更灵活地应对需求变化。

**面向对象设计原则是构建高质量软件系统的基石**，对于软件工程师和架构师来说，不仅要理解这些原则的理论内涵，更要能够在实际项目中灵活运用，识别出违反原则的代码并进行重构。

本文将深入探讨面向对象设计的7大原则，通过正例与反例的对比，结合代码示例，帮助你加深对于7大原则的理解和运用。
Java、Go、C、JavaScript、Python源码：[https://github.com/microwind/design-patterns/tree/main/design-principles](https://github.com/microwind/design-patterns/tree/main/design-principles)

## 1. 单一职责原则（SRP）

### 核心思想

一个类或模块只负责完成一个职责或功能，而不是将不同的功能糅杂在一起。

### 实现要点

1. 按业务功能划分类的职责，每个类只做一件事
2. 将数据访问、业务逻辑、数据验证等不同职责分离到不同的类
3. 当一个类有多个修改原因时，考虑将其拆分成多个类
4. 避免创建"万能类"（如XXXManager、XXXUtil等承担过多职责的类）

### 如何检验

**变化层面**

- ❌ 修改类的原因不止一个，不同的变化要修改同一个类
- ❌ 一个需求变更影响多个不相关的类和方法，引起跨模块和跨层次的修改
- ❌ 修改某个功能造成连锁反应，造成大面积修改
- ❌ 单元测试难以编写（需要 mock 太多依赖）

**结构层面**

- ❌ 类的代码行数过多（超过 2000 行）
- ❌ 类依赖的其他类过多（超过 10 个）
- ❌ 单个方法过长（超过 150 行）
- ❌ 私有方法过多（占比超过 50%）
- ❌ 类名难以命名（包含 "Manager"、"Handler"、"Util" 或两个以上的名词）


### 反例代码

```java
// 违反单一职责原则的订单处理类
class OrderProcessor {
    // 一个类负责订单处理、校验以及保存多重职责
    method processOrder(orderId) {
        // 1. 验证订单
        if (!this.validateId(orderId)) {
            return false
        }
        
        if (!this.validateTime(currentTime())) {
            return false
        }
        
        // 2. 业务逻辑处理
        if (orderId % 2 == 0) {
            // 处理订单数据
        }
        
        // 3. 保存订单
        this.saveOrder(orderId)
        return true
    }
    
    // 校验逻辑 - 不应该放在订单处理类中
    private method validateId(orderId) {
        return orderId % 2 == 0
    }
    
    private method validateTime(time) {
        return true
    }
    
    // 数据库操作 - 不应该放在订单处理类中
    private method saveOrder(orderId) {
        // 保存订单
        return true
    }
    
    private method deleteOrder(orderId) {
        // 删除订单
        return true
    }
}
```

### 正例代码

```java
// 订单处理类 - 只负责订单业务逻辑
class OrderProcessor {
    private OrderValidator validator
    private OrderDao dao
    
    method processOrder(orderId) {
        // 1. 验证订单
        if (!validator.validateId(orderId)) {
            return false
        }
        
        if (!validator.validateTime(currentTime())) {
            return false
        }
        
        // 2. 业务逻辑处理
        if (orderId % 2 == 0) {
            // 处理订单数据
        }
        
        // 3. 保存订单
        dao.saveOrder(orderId)
        return true
    }
}

// 订单验证类 - 只负责订单验证
class OrderValidator {
    method validateId(orderId) {
        // 验证订单ID
        return orderId % 2 == 0
    }
    
    method validateTime(time) {
        // 验证时间
        return true
    }
}

// 订单数据访问类 - 只负责数据存储
class OrderDao {
    method saveOrder(orderId) {
        // 保存订单到数据库
        return true
    }
    
    method deleteOrder(orderId) {
        // 删除订单
        return true
    }
}
```

### 实战启示

单一职责原则并不是要把类拆得越细越好，而是要根据业务逻辑的内聚性来决定。一个好的实践是：当你发现一个类需要修改的原因有多个时，就应该考虑拆分它。

## 2. 开闭原则（OCP）

### 核心思想

对扩展开放，对修改关闭，这是面向对象编程核心。当有新的需求或变化时，可以对现有代码进行扩展，以适应新的情况，而不需要修改原有代码。

### 实现要点

1. 多使用抽象类和接口
2. 多使用多态
3. 尽量通过扩展的方式来增添功能，而不是修改现有代码

### 如何检验
**变化层面**

- ❌ 每次新增功能都需要修改现有类的代码
- ❌ 代码中存在大量的 if-else 或 switch-case 判断类型
- ❌ 新增一个类型需要在多处代码中添加分支逻辑
- ❌ 修改导致需要重新测试原有功能

**结构层面**

- ❌ 类中缺少抽象层，直接依赖具体实现
- ❌ 使用硬编码的类型判断（字符串、数字标识等）
- ❌ 方法内部包含大量类型判断和分支处理
- ❌ 工厂类或调用方需要了解所有具体子类

### 反例代码

```java
// 违反开闭原则的车辆工厂
class VehicleFactory {
    method createVehicle(vehicle) {
        // 当增加新的车辆类型时，需要修改这里的逻辑
        switch (vehicle.type) {
            case 1:
                this.createCar(vehicle)
                break
            case 2:
                this.createBus(vehicle)
                break
            // 每次新增车辆类型都要在这里添加case
        }
        return vehicle
    }
    
    method createCar(vehicle) {
        print("Car has been produced")
        return vehicle
    }
    
    method createBus(vehicle) {
        print("Bus has been produced")
        return vehicle
    }
}

// 违反开闭原则的折扣计算器
class DiscountCalculator {
    method applyDiscount(itemType, price, quantity) {
        totalPrice = price * quantity
        if (itemType == "book") {
            return totalPrice * 0.9
        } else if (itemType == "clothing") {
            if (quantity > 2) {
                return totalPrice * 0.8
            }
            return totalPrice
        } else if (itemType == "electronic") {
            if (quantity >= 5) {
                return totalPrice * 0.7
            }
            return totalPrice
        }
        // 每次添加新类型都需要修改这里
        return totalPrice
    }
}
```

### 正例代码

```java
// 抽象车辆类
abstract class AbstractVehicle {
    abstract method create()
    abstract method getName()
}

// 汽车类
class Car extends AbstractVehicle {
    method create() {
        print("Car has been produced")
        return true
    }
    
    method getName() {
        return "car"
    }
}

// 巴士类
class Bus extends AbstractVehicle {
    method create() {
        print("Bus has been produced")
        return true
    }
    
    method getName() {
        return "bus"
    }
}

// 车辆工厂 - 对扩展开放，对修改关闭。符合开闭原则。
class VehicleFactory {
    static method createVehicle(AbstractVehicle vehicle) {
        // 直接调用具体对象的方法，不需要修改工厂逻辑
        vehicle.create()
        return vehicle
    }
}

// 折扣策略示例
interface DiscountStrategy {
    method applyDiscount(price, quantity)
}

class BookDiscountStrategy implements DiscountStrategy {
    method applyDiscount(price, quantity) {
        return price * quantity * 0.9 // 书籍9折
    }
}

class ClothingDiscountStrategy implements DiscountStrategy {
    method applyDiscount(price, quantity) {
        if (quantity > 2) {
            return price * quantity * 0.8 // 超过2件8折
        }
        return price * quantity
    }
}
```

### 实战启示

开闭原则是所有面向对象设计原则的核心，它要求我们在设计时就要考虑到未来的变化。通过抽象和多态，我们可以将变化的部分封装起来，使得系统更加灵活和可扩展。关键在于识别系统中的变化点，并为这些变化点设计合适的抽象层。

## 3. 依赖倒置原则（DIP）

### 核心思想

高层模块不应该依赖低层模块，两者都应该依赖其抽象；抽象不应该依赖于具体实现，具体实现应该依赖于抽象。这也是DDD领域驱动设计的核心所在（关于领域驱动设计请见：https://github.com/microwind/design-patterns/tree/main/domain-driven-design）。

### 层次划分

- 高层模块：业务层、应用层，表示要进行的操作，即"想要做什么"，属于调用方
- 低层模块：逻辑层、数据层，表示具体实现细节，即"怎么做"，属于被调用方
- 抽象层：接口或抽象类，定义高层和低层之间的契约

### 实现要点

1. 面向接口编程，而不是面向实现编程
2. 通过依赖注入（构造函数、setter方法等）传入依赖对象
3. 高层模块定义接口，低层模块实现接口
4. 使用IoC容器（如Spring）管理对象依赖关系

### 如何检验
**变化层面**

❌ 修改底层实现时，需要同时修改高层调用代码
❌ 新增一种实现方式，需要修改多处调用代码
❌ 难以进行单元测试，无法使用mock对象替换依赖
❌ 更换底层技术方案时，影响面过大

**结构层面**

❌ 高层类直接new低层类的实例
❌ 类中使用具体类型声明，而不是接口或抽象类
❌ 方法参数使用具体类型而非抽象类型
❌ 代码中充斥着new关键字创建依赖对象

### 反例代码

```java
// 违反依赖倒置原则的通知类
class Notification {
    // 直接依赖具体实现类，紧耦合
    private MessageSender messageSender = new MessageSender()
    private MailSender mailSender = new MailSender()
    private PushSender pushSender = new PushSender()
    
    // 为每种发送方式提供单独的方法
    method sendMessage(content) {
        messageSender.send(content)
    }
    
    method sendEmail(content) {
        mailSender.send(content)
    }
    
    method sendPush(content) {
        pushSender.send(content)
    }
    // 新增发送方式时，需要修改此类
}

// 具体实现类
class MessageSender {
    method send(content) {
        print("Message sent: " + content)
    }
}

class MailSender {
    method send(content) {
        print("Email sent: " + content)
    }
}

class PushSender {
    method send(content) {
        print("Push notification sent: " + content)
    }
}

// 使用上需要 new 不同的具体实现类
```

### 正例代码

```java
// 发送者接口 - 抽象层
interface Sender {
    method send(content)
}

// 具体发送实现类 - 低层模块
class MessageSender implements Sender {
    method send(content) {
        print("Message sent: " + content)
    }
}

class MailSender implements Sender {
    method send(content) {
        print("Email sent: " + content)
    }
}

class PushSender implements Sender {
    method send(content) {
        print("Push notification sent: " + content)
    }
}

// 通知业务类 - 高层依赖抽象接口，不依赖具体实现类，符合依赖倒置原则
class Notification {
    private Sender sender
    
    // 通过构造函数注入依赖
    constructor(Sender sender) {
        this.sender = sender
    }
    
    method send(content) {
        // 调用抽象方法，不关心具体实现
        this.sender.send(content)
    }
    
    // 通过setter方法切换实现
    method setSender(Sender sender) {
        this.sender = sender
    }
}

// 使用示例
notification = new Notification(new MessageSender())
notification.send("Hello")  // 发送短信

notification.setSender(new MailSender())
notification.send("Hello")  // 发送邮件
```

### 实战启示

依赖倒置原则与控制反转（IoC）和依赖注入（DI）密切相关。在实际项目中，我们通常会使用Spring等框架来实现依赖注入，从而更好地遵循依赖倒置原则。


## 4. 接口隔离原则（ISP）

### 核心思想

客户端不应该被强迫依赖它不需要的接口。将庞大的接口分解为多个小接口，每个接口服务于一个特定的子模块。

### 实现要点

1. 接口建立在最小化抽象上，功能单一明确
2. 客户端只依赖它必须的接口方法，即最小依赖原则
3. 避免接口过于臃肿，也避免接口过于原子化
4. 使用接口继承来组合多个小接口

### 如何检验
**变化层面**

- ❌ 接口变更影响到不使用该方法的实现类
- ❌ 实现类中存在大量空实现或抛出"不支持"异常的方法
- ❌ 客户端被迫依赖它不需要的方法
- ❌ 修改某个功能需要同时修改多个不相关的实现类

**结构层面**

- ❌ 接口中方法过多（超过10个方法需警惕）
- ❌ 接口包含了多个不同职责的方法
- ❌ 实现类只实现接口的部分方法，其他方法为空实现
- ❌ 接口命名模糊（如IManager、IHandler等）

### 反例代码

```java
// 违反接口隔离原则的臃肿接口
interface DeviceController {
    method turnOnTV()
    method turnOffTV()
    method adjustTVVolume(volume)
    method changeTVChannel(channel)
    method turnOnLight()
    method turnOffLight()
    method changeLightColor(color)
}

// 灯光实现类被迫实现不需要的方法
class Light implements DeviceController {
    // 不需要的TV方法，被迫提供空实现或抛出异常
    method turnOnTV() {
        throw new Exception("Not supported")
    }
    
    method turnOffTV() {
        throw new Exception("Not supported")
    }
    
    method adjustTVVolume(volume) {
        throw new Exception("Not supported")
    }
    
    method changeTVChannel(channel) {
        throw new Exception("Not supported")
    }
    
    // 真正需要的灯光方法
    method turnOnLight() {
        print("Light turned on")
    }
    
    method turnOffLight() {
        print("Light turned off")
    }
    
    method changeLightColor(color) {
        print("Light color changed to " + color)
    }
}

// 电视实现类同样存在问题
class TV implements DeviceController {
    method turnOnTV() {
        print("TV turned on")
    }
    
    method turnOffTV() {
        print("TV turned off")
    }
    
    method adjustTVVolume(volume) {
        print("TV volume adjusted to " + volume)
    }
    
    method changeTVChannel(channel) {
        print("TV channel changed to " + channel)
    }
    
    // 不需要的灯光方法
    method turnOnLight() {
        throw new Exception("Not supported")
    }
    
    method turnOffLight() {
        throw new Exception("Not supported")
    }
    
    method changeLightColor(color) {
        throw new Exception("Not supported")
    }
}
```

### 正例代码

```java
// 基础设备控制接口 - 所有设备都需要的基本功能
interface DeviceController {
    method turnOn()
    method turnOff()
}

// 灯光控制接口 - 只包含灯光特有的功能
interface LightController extends DeviceController {
    method changeLightColor(color)
}

// 电视控制接口 - 只包含电视特有的功能
interface TVController extends DeviceController {
    method adjustTVVolume(volume)
    method changeTVChannel(channel)
}

// 灯光实现类 - 只实现需要的接口，符合接口隔离原则
class Light implements LightController {
    method turnOn() {
        print("Light turned on")
    }
    
    method turnOff() {
        print("Light turned off")
    }
    
    method changeLightColor(color) {
        print("Light color changed to " + color)
    }
}

// 电视实现类 - 只实现需要的接口，符合接口隔离原则
class TV implements TVController {
    method turnOn() {
        print("TV turned on")
    }
    
    method turnOff() {
        print("TV turned off")
    }
    
    method adjustTVVolume(volume) {
        print("TV volume adjusted to " + volume)
    }
    
    method changeTVChannel(channel) {
        print("TV channel changed to " + channel)
    }
}
```

### 实战启示

接口隔离原则与单一职责原则相辅相成，都是为了保持代码的清晰和简洁。在设计接口时，应该根据客户端的实际需求来拆分接口，而不是设计一个"大而全"的接口。合理的接口拆分可以提高系统的灵活性和可维护性，同时也使代码更易于理解和测试。

## 5. 合成复用原则（CRP）

### 核心思想

尽量使用组合或聚合，而不是通过继承来达到复用的目的。组合/聚合相比继承来讲更具扩展性，松耦合性更好。

### 概念区分

- 聚合(Aggregate)：表示一个对象拥有另外的对象，一般表现为松散的整体和个体的关系，整体和个体也可以不相关
- 组合(Composite)：表示一个对象拥有其他的对象，是一种较强的拥有关系，即整体和部分的关系，整体对象强依赖部分对象

### 概念区分

- 聚合（Aggregation）：表示一个对象拥有另外的对象，是一种松散的整体和个体的关系，整体和个体可以独立存在（has-a关系），并不强依赖。
- 组合（Composition）：表示一个对象拥有其他的对象，是一种较强的拥有关系，即整体和部分的关系，整体对象强依赖部分对象，部分不能独立存在（contains-a关系）
- 继承（Inheritance）：表示一个类（称为子类或派生类）基于另一个类（称为父类、基类或超类）来构建，是"is-a"关系，子类自动拥有父类的属性和方法，并可以对其进行扩展、修改或重写。

### 实现要点

1. 优先使用组合/聚合而不是继承来实现代码复用
2. 只有在明确的"is-a"关系时才使用继承
3. 通过依赖注入将需要复用的对象传入
4. 使用接口来定义行为，通过组合实现功能

### 如何检验
**变化层面**

- ❌ 修改父类导致所有子类都受影响
- ❌ 子类无法灵活更换或组合不同的功能模块
- ❌ 一个工程内继承层次过深（超过3层需警惕，最多别超过5层）
- ❌ 无法在运行时动态改变对象的行为

**结构层面**

- ❌ 使用继承仅仅是为了复用代码，而不是"is-a"关系
- ❌ 子类重写父类方法只是为了返回空或抛出异常
- ❌ 多重继承导致的复杂依赖关系
- ❌ 继承破坏了封装性，子类依赖父类的实现细节

### 反例代码

```java
// 违反合成复用原则的设计 - 过度使用继承
abstract class Person {
    protected String name
    protected int age
    
    method getName() {
        return name
    }
    
    method getAge() {
        return age
    }
}

// 雇员类继承人物类
// 问题：雇员"is-a"人物？这种继承关系不够灵活
class Employee extends Person {
    protected int id
    protected String title
    
    method work() {
        return true
    }
}

// 工程师类继承雇员类
class Engineer extends Employee {
    constructor(name, age, id, title) {
        this.name = name
        this.age = age
        this.id = id
        this.title = title
    }
    
    method work() {
        print("Engineer is working: " + this.getName())
        return true
    }
}

// 管理者类继承雇员类
class Manager extends Employee {
    constructor(name, age, id, title) {
        this.name = name
        this.age = age
        this.id = id
        this.title = title
    }
    
    method work() {
        print("Manager is working: " + this.getName())
        return true
    }
}

// 问题总结：
// 1. 继承链过长：Engineer/Manager -> Employee -> Person
// 2. 如果Person类改变，所有子类都会受影响
// 3. 无法在运行时更换人物信息
// 4. 难以扩展：如果需要添加其他属性，继承结构会变得更复杂
```

### 正例代码

```java
// 人物类 - 独立的实体类
class Person {
    private String name
    private int age
    
    constructor(name, age) {
        this.name = name
        this.age = age
    }
    
    method getName() {
        return name
    }
    
    method getAge() {
        return age
    }
}

// 雇员抽象类 - 通过聚合方式关联人物类，符合合成复用原则
abstract class Employee {
    // 聚合人物类，松耦合，更加灵活
    protected Person person
    protected int id
    protected String title
    
    method work() {
        return true
    }
    
    method getPerson() {
        return person
    }
}

// 工程师类 - 通过组合获得人物信息，符合合成复用原则
class Engineer extends Employee {
    constructor(id, title, Person person) {
        this.id = id
        this.title = title
        this.person = person
    }
    
    method work() {
        print("Engineer is working: " + person.getName())
        return true
    }
}

// 管理者类 - 通过组合获得人物信息，符合合成复用原则
class Manager extends Employee {
    constructor(id, title, Person person) {
        this.id = id
        this.title = title
        this.person = person
    }
    
    method work() {
        print("Manager is working: " + person.getName())
        return true
    }
}

// 使用示例
person = new Person("张三", 30)
engineer = new Engineer(1001, "高级工程师", person)
engineer.work()  // 输出: Engineer is working: 张三

// 优势：
// 1. 可以灵活更换Person对象
// 2. Person类的修改不会直接影响Employee类
// 3. 继承层次简单，易于维护
// 4. 可以在运行时动态改变person属性
```

### 实战启示

继承是一种强耦合关系，它使得子类与父类紧密绑定。而组合和聚合则是一种弱耦合关系，它们更加灵活，便于扩展。在实际项目中，我们应该优先考虑组合/聚合，只有在确实存在"is-a"关系时才使用继承。记住：组合优于继承（Favor composition over inheritance）。

## 6. 迪米特法则（LoD）

### 核心思想

一个对象应当对其他对象尽可能少的了解，对象之间的耦合度应该尽可能低。一个对象应该只与密切相关的朋友（直接的成员变量、方法参数、方法内部创建的对象以及静态方法等）交互，不应该与陌生对象交互。也被称为"最少知识原则"（Least Knowledge Principle）。

### 朋友的定义
**一个对象的朋友包括：**

- 当前对象本身（this）
- 成员变量：作为类的属性存在的对象
- 方法参数：通过方法参数传入的对象
- 方法内创建的对象：在方法内部实例化的对象
- 静态成员：通过类直接访问的对象

### 陌生对象

朋友对象的属性或方法返回的对象

### 实现要点

1. 识别对象的朋友，只与朋友通信
2. 封装对象的行为，隐藏内部细节
3. 避免链式调用（如 a.getB().getC().doSomething()）
4. 合理划分模块和类的职责边界
5. 适当使用中介者、外观等设计模式

### 如何检验

**变化层面**

- ❌ 修改一个类的内部实现导致其他不相关的类也要修改
- ❌ 对象之间存在过长的调用链
- ❌ 一个类了解过多其他类的内部细节
- ❌ 难以单独测试某个类（需要构造复杂的对象依赖链）

**结构层面**

- ❌ 存在链式调用（如 obj.getA().getB().getC()）
- ❌ 方法参数过多（超过3-4个参数需警惕）
- ❌ 类直接访问其他类的内部对象
- ❌ 类与大量不相关的类存在依赖关系

### 反例代码

```java
// 商品类
class Product {
    private String name
    private double price
    
    constructor(name, price) {
        this.name = name
        this.price = price
    }
    
    method getName() {
        return name
    }
    
    method getPrice() {
        return price
    }
}

// 违反迪米特法则的顾客类
class Customer {
    private String name
    private List<Product> products
    
    constructor(name) {
        this.name = name
        this.products = new ArrayList()
    }
    
    method buy(Product product) {
        // 问题1: 直接访问Product的细节，与陌生对象交互
        if (product.getPrice() > 1000) {
            print(product.getName() + "'s price exceeds limit")
        } else {
            products.add(product)
            // 问题2: 承担了价格计算的职责，违反单一职责
            double total = this.calculateTotalPrice()
            print(name + " purchased " + product.getName() + " for " + total)
        }
    }
    
    // 问题3: 价格计算应该由专门的类处理
    private method calculateTotalPrice() {
        double total = 0
        for (Product product : products) {
            // 问题4: 遍历集合并访问每个Product的内部属性
            total += product.getPrice()
        }
        return total
    }
}

// 问题总结：
// 1. Customer直接依赖Product的内部实现（getPrice、getName）
// 2. Customer了解了太多Product的细节
// 3. Customer承担了过多职责（购买、验证、计算）
// 4. 如果Product的价格计算逻辑变化，Customer也要修改
```

### 正例代码

```java
// 商品类
class Product {
    private String name
    private double price
    
    constructor(name, price) {
        this.name = name
        this.price = price
    }
    
    method getName() {
        return name
    }
    
    method getPrice() {
        return price
    }
}

// 购物车类 - 作为Customer和Product之间的中介
class ShoppingCart {
    private List<Product> products
    
    constructor() {
        this.products = new ArrayList()
    }
    
    method addProduct(Product product) {
        products.add(product)
    }
    
    // 封装价格计算逻辑
    method calculateTotalPrice() {
        double total = 0
        for (Product product : products) {
            total += product.getPrice()
        }
        return total
    }
    
    // 封装商品验证逻辑
    method validateProduct(Product product) {
        return product.getPrice() <= 1000
    }
}

// 顾客类 - 只与购物车（直接朋友）交互，符合迪米特法则
class Customer {
    private String name
    private ShoppingCart cart  // 成员变量，是朋友
    
    constructor(name) {
        this.name = name
        this.cart = new ShoppingCart()
    }
    
    method buy(Product product) {
        // 只与cart交互，不直接接触Product的细节
        if (cart.validateProduct(product)) {
            cart.addProduct(product)
            double total = cart.calculateTotalPrice()
            print(name + " purchased " + product.getName() + " for " + total)
        } else {
            print(product.getName() + "'s price exceeds limit")
        }
    }
}

// 使用示例
customer = new Customer("张三")
product = new Product("笔记本电脑", 5000)
customer.buy(product)

// 优势：
// 1. Customer不需要了解Product的内部细节
// 2. 职责清晰：ShoppingCart负责商品管理和计算
// 3. 低耦合：修改Product不影响Customer
// 4. 易于测试和维护
```

### 实战启示

迪米特法则要求我们保持对象之间的适当距离，通过封装和中介者模式等方式来降低对象之间的耦合度。但是，过度使用迪米特法则可能会导致系统中存在大量的中介类，增加系统的复杂度。因此需要在降低耦合和保持简洁之间找到平衡点，恰如其分地使用。

**关键原则：**不要和陌生人说话（Don't talk to strangers），只和你的直接朋友通信。

## 7. 里氏替换原则（LSP）

### 核心思想

子类对象（派生类）必须能够替换其父类对象（基类）而不影响程序的正确性。所有引用基类的地方必须能透明地使用其子类的对象，程序行为不会发生变化。

### 实现要点

1. 子类必须完全实现父类的抽象方法，子类可以有自己的特性和方法
2. 行为一致性：子类重写父类方法时，表现出与父类对象一致的行为
3. 尽量不强制重写父类方法：子类应该通过增加新的方法来扩展父类的功能，而不是通过重写父类的方法来改变其行为
4. 子类的输入参数要比父类更宽松（逆变），子类的返回值要比父类更严格（协变），子类不应该抛出父类没有声明的异常

### 如何检验
**变化层面**

- ❌ 使用子类替换父类后，程序出现错误或异常
- ❌ 需要通过类型判断（instanceof）来区分不同子类
- ❌ 子类重写父类方法后，改变了原有的行为逻辑
- ❌ 子类需要特殊处理才能正常工作

**结构层面**

- ❌ 子类重写父类方法，但不调用父类方法（super）
- ❌ 子类方法的前置条件比父类更严格
- ❌ 子类方法的后置条件比父类更宽松
- ❌ 子类抛出了父类方法签名中未声明的异常
- ❌ 子类改变了父类方法的预期行为

### 反例代码

```java
// 抽象图形父类
abstract class Shape {
    method draw() {
        print("Drawing Shape. area: " + this.area())
    }
    
    abstract method area()
}

// 正方形类 - 违反里氏替换原则
class Square extends Shape {
    private double side
    
    constructor(side) {
        this.side = side
    }
    
    // 问题：重写了父类的draw方法，改变了原有行为
    method draw() {
        if (this.checkArea()) {
            print("Drawing Square. area: " + this.area())
        } else {
            // 行为不一致：父类总是会绘制，子类却可能不绘制
            print("Don't draw square")
            return  // 父类方法没有提前返回的逻辑
        }
    }
    
    method checkArea() {
        return this.area() <= 100
    }
    
    method area() {
        return side * side
    }
}

// 矩形类
class Rectangle extends Shape {
    private double width
    private double height
    
    constructor(width, height) {
        this.width = width
        this.height = height
    }
    
    method area() {
        return width * height
    }
}

// 使用场景问题演示
function processShape(Shape shape) {
    shape.draw()  // 期望所有Shape都能正常绘制
    // 但Square在面积超过100时不会绘制，违反了父类约定
}

processShape(new Rectangle(10, 20))  // 正常绘制
processShape(new Square(15))  // 不会绘制，违反预期！
```

### 正例代码

```java
// 抽象图形父类
abstract class Shape {
    method draw() {
        print("Drawing Shape. area: " + this.area())
    }
    
    abstract method area()
}

// 正方形类 - 符合里氏替换原则
class Square extends Shape {
    private double side
    
    constructor(side) {
        this.side = side
    }
    
    // 不重写父类的draw方法，保持行为一致性
    // 通过新增方法来扩展功能，而不是改变父类行为
    method drawSquare() {
        if (this.checkArea()) {
            print("Drawing Square. area: " + this.area())
        } else {
            print("Don't draw square")
        }
    }
    
    method checkArea() {
        return this.area() <= 100
    }
    
    method area() {
        return side * side
    }
}

// 矩形类
class Rectangle extends Shape {
    private double width
    private double height
    
    constructor(width, height) {
        this.width = width
        this.height = height
    }
    
    // 只实现抽象方法，不改变父类行为
    method area() {
        return width * height
    }
}

// 使用场景
function processShape(Shape shape) {
    shape.draw()  // 所有Shape子类都能正常绘制
}

processShape(new Rectangle(10, 20))  // 正常绘制
processShape(new Square(15))  // 正常绘制，符合预期

// 如果需要特殊处理，使用子类特有的方法
square = new Square(15)
square.drawSquare()  // 使用扩展方法

// 优势：
// 1. 子类完全兼容父类，可以安全替换
// 2. 保持了行为一致性
// 3. 通过新增方法扩展功能，而不是改变原有行为
// 4. 代码更容易理解和维护
```

### 实战启示

里氏替换原则是继承复用的基础，它保证了继承体系的正确性。如果一个子类不能完全替换其父类，那么这个继承关系就存在问题。在设计继承关系时，我们应该确保：

- 子类能够完全符合父类的行为约定
- 子类扩展功能时，应该新增方法而不是改变父类方法的行为
- 如果需要改变行为，考虑使用组合而不是继承

**判断标准：** 如果在代码中用子类对象替换父类对象后，程序的行为没有变化，就说明符合里氏替换原则。

## 总结

面向对象设计的7大原则并不是孤立存在的，它们相互关联、相互补充。在实际项目中，我们需要根据具体情况灵活运用这些原则，在代码质量和开发效率之间找到平衡点，而不是生搬硬套。

## 原则回顾

| 原则 | 英文缩写 | 核心目标 | 关键要点 |
|------|---------|---------|---------|
| 单一职责原则 | SRP | 保证每个类只有一个职责 | 一个类只有一个引起它变化的原因 |
| 开闭原则 | OCP | 保证系统的可扩展性 | 对扩展开放，对修改关闭 |
| 依赖倒置原则 | DIP | 保证系统的松耦合 | 依赖抽象而不是具体实现 |
| 接口隔离原则 | ISP | 保证接口的精简性 | 客户端不应依赖它不需要的接口 |
| 合成复用原则 | CRP | 保证系统的灵活性 | 优先使用组合/聚合而不是继承 |
| 迪米特法则 | LoD | 保证系统的低耦合 | 只与直接的朋友通信 |
| 里氏替换原则 | LSP | 保证继承体系的正确性 | 子类必须能够替换父类 |

### 层次关系
```
开闭原则（OCP）- 核心目标
    ├── 单一职责原则（SRP）- 基础保障
    ├── 依赖倒置原则（DIP）- 实现手段
    ├── 接口隔离原则（ISP）- 接口设计
    ├── 里氏替换原则（LSP）- 继承约束
    ├── 合成复用原则（CRP）- 复用方式
    └── 迪米特法则（LoD）- 耦合控制
```


## 写在最后

作为高级程序员和软件架构师，我们应该能够识别出违反设计原则的代码，并通过重构来改进它们。只有这样，我们才能构建出高质量、可维护、可扩展的系统。

但**设计原则不是银弹**，它们只是指导我们设计的准则，而不是我们必须恪守的信条，我们不能过于推崇设计原则与设计模式，更**不能为了设计而设计**，否则就会本末倒置，让原本简单清晰的结构变得冗余复杂。

总之，我们的核心目的是**让复杂的系统变得简洁、清晰、可维护，更易于被人理解**（高级语言是面向人的，而不是机器）。实际开发中，我们通常需要在不同的原则之间做出权衡。例如，为了满足开闭原则，我们可能需要引入更多的抽象层，这会增加系统的复杂度。因此，我们需要根据项目的具体情况，**在设计原则和实际需求之间找到平衡点。**

记住，设计原则需要灵活地运用，而不是僵化地遵照。其最终目的，是提高代码的可维护性与可扩展性。解决复杂问题的关键，**在于顺着事物本身的思路（发展）去理解**，而非强行改变其原有逻辑。正如《道德经》里的话：人法地，地法天，天法道，道法自然。**当我们顺应事物本来面貌去做事，这时候运用设计原则和设计模式就会得心应手。**“纸上得来终觉浅，绝知此事要躬行”，唯有在实践中不断运用和反思，我们才能真正掌握这些原则的精髓。

**更多设计模式和设计原则源码（多种语言实现）：[https://github.com/microwind/design-patterns](https://github.com/microwind/design-patterns)**
