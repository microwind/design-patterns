# Object-Oriented Design: 7 Core Principles Explained with Good and Bad Examples

## Introduction

The 7 principles of object-oriented design aim to improve the **maintainability, extensibility, and reusability** of software systems. They include: `Open-Closed Principle (Core)`, `Single Responsibility`, `Liskov Substitution`, `Dependency Inversion`, `Interface Segregation`, `Law of Demeter`, and `Composite Reuse` principles. Through high cohesion and low coupling design, these principles make systems more flexible in responding to changing requirements.

**Object-oriented design principles are the cornerstone of building high-quality software systems**. For software engineers and architects, it's not enough to just understand the theoretical implications of these principles; they must also be able to apply them flexibly in actual projects, identify code that violates these principles, and refactor it accordingly.

This article will delve into the seven principles of object-oriented design, using positive and negative examples, along with code examples, to help you deepen your understanding and application of these principles.

Java, Go, C, JavaScript, and Python source code: [https://github.com/microwind/design-patterns/tree/main/design-principles](https://github.com/microwind/design-patterns/tree/main/design-principles)


## 1. Single Responsibility Principle (SRP)

### Core Idea

A class or module should be responsible for only one responsibility or function, rather than mixing different functions together.

### Implementation Points

1. Divide class responsibilities according to business functions, each class does only one thing
2. Separate different responsibilities such as data access, business logic, and data validation into different classes
3. When a class has multiple reasons for modification, consider splitting it into multiple classes
4. Avoid creating "omnipotent classes" (such as XXXManager, XXXUtil, etc. that take on too many responsibilities)

### How to Verify

**Change Level**

- ❌ More than one reason to modify the class, different changes require modifying the same class
- ❌ A requirement change affects multiple unrelated classes and methods, causing cross-module and cross-level modifications
- ❌ Modifying a feature causes a chain reaction, resulting in large-scale modifications
- ❌ Unit tests are difficult to write (need to mock too many dependencies)

**Structure Level**

- ❌ Class has too many lines of code (more than 2000 lines)
- ❌ Class depends on too many other classes (more than 10)
- ❌ Single method is too long (more than 150 lines)
- ❌ Too many private methods (more than 50% of total methods)
- ❌ Class name is difficult to name (contains "Manager", "Handler", "Util" or more than two nouns)

### Negative Example Code

```java
// Order processing class that violates Single Responsibility Principle
class OrderProcessor {
    // One class responsible for order processing, validation, and saving
    method processOrder(orderId) {
        // 1. Validate order
        if (!this.validateId(orderId)) {
            return false
        }
        
        if (!this.validateTime(currentTime())) {
            return false
        }
        
        // 2. Business logic processing
        if (orderId % 2 == 0) {
            // Process order data
        }
        
        // 3. Save order
        this.saveOrder(orderId)
        return true
    }
    
    // Validation logic - shouldn't be in order processing class
    private method validateId(orderId) {
        return orderId % 2 == 0
    }
    
    private method validateTime(time) {
        return true
    }
    
    // Database operation - shouldn't be in order processing class
    private method saveOrder(orderId) {
        // Save order
        return true
    }
    
    private method deleteOrder(orderId) {
        // Delete order
        return true
    }
}
```

### Positive Example Code

```java
// Order processing class - only responsible for order business logic
class OrderProcessor {
    private OrderValidator validator
    private OrderDao dao
    
    method processOrder(orderId) {
        // 1. Validate order
        if (!validator.validateId(orderId)) {
            return false
        }
        
        if (!validator.validateTime(currentTime())) {
            return false
        }
        
        // 2. Business logic processing
        if (orderId % 2 == 0) {
            // Process order data
        }
        
        // 3. Save order
        dao.saveOrder(orderId)
        return true
    }
}

// Order validation class - only responsible for order validation
class OrderValidator {
    method validateId(orderId) {
        // Validate order ID
        return orderId % 2 == 0
    }
    
    method validateTime(time) {
        // Validate time
        return true
    }
}

// Order data access class - only responsible for data storage
class OrderDao {
    method saveOrder(orderId) {
        // Save order to database
        return true
    }
    
    method deleteOrder(orderId) {
        // Delete order
        return true
    }
}
```

### Practical Insights

The Single Responsibility Principle doesn't mean we should split classes into the smallest possible pieces, but rather determine splits based on the cohesion of business logic. A good practice is: when you find a class has multiple reasons for modification, you should consider splitting it.

## 2. Open-Closed Principle (OCP)

### Core Idea

Open for extension, closed for modification. This is the core of object-oriented programming. When there are new requirements or changes, existing code can be extended to adapt to new situations without modifying the original code.

### Implementation Points

1. Use more abstract classes and interfaces
2. Use more polymorphism
3. Try to add functionality through extension rather than modifying existing code

### How to Verify
**Change Level**

- ❌ Every new feature requires modifying existing class code
- ❌ A lot of if-else or switch-case statements for type judgment in code
- ❌ Adding a new type requires adding branch logic in multiple places
- ❌ Modifications require retesting original functionality

**Structure Level**

- ❌ Lack of abstraction layer in class, direct dependency on concrete implementation
- ❌ Use of hard-coded type judgments (strings, numeric identifiers, etc.)
- ❌ Methods contain a lot of type judgment and branch processing
- ❌ Factory classes or callers need to understand all concrete subclasses

### Negative Example Code

```java
// Vehicle factory that violates Open-Closed Principle
class VehicleFactory {
    method createVehicle(vehicle) {
        // When adding new vehicle types, need to modify logic here
        switch (vehicle.type) {
            case 1:
                this.createCar(vehicle)
                break
            case 2:
                this.createBus(vehicle)
                break
            // Need to add case for each new vehicle type
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

// Discount calculator that violates Open-Closed Principle
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
        // Need to modify here for each new type
        return totalPrice
    }
}
```

### Positive Example Code

```java
// Abstract vehicle class
abstract class AbstractVehicle {
    abstract method create()
    abstract method getName()
}

// Car class
class Car extends AbstractVehicle {
    method create() {
        print("Car has been produced")
        return true
    }
    
    method getName() {
        return "car"
    }
}

// Bus class
class Bus extends AbstractVehicle {
    method create() {
        print("Bus has been produced")
        return true
    }
    
    method getName() {
        return "bus"
    }
}

// Vehicle factory - open for extension, closed for modification. Complies with Open-Closed Principle.
class VehicleFactory {
    static method createVehicle(AbstractVehicle vehicle) {
        // Directly call concrete object's method, no need to modify factory logic
        vehicle.create()
        return vehicle
    }
}

// Discount strategy example
interface DiscountStrategy {
    method applyDiscount(price, quantity)
}

class BookDiscountStrategy implements DiscountStrategy {
    method applyDiscount(price, quantity) {
        return price * quantity * 0.9 // 10% discount for books
    }
}

class ClothingDiscountStrategy implements DiscountStrategy {
    method applyDiscount(price, quantity) {
        if (quantity > 2) {
            return price * quantity * 0.8 // 20% discount for more than 2 items
        }
        return price * quantity
    }
}
```

### Practical Insights

The Open-Closed Principle is the core of all object-oriented design principles. It requires us to consider future changes when designing. Through abstraction and polymorphism, we can encapsulate changes, making the system more flexible and extensible. The key is to identify change points in the system and design appropriate abstraction layers for these points.

## 3. Dependency Inversion Principle (DIP)

### Core Idea

High-level modules should not depend on low-level modules; both should depend on abstractions. Abstractions should not depend on concrete implementations; concrete implementations should depend on abstractions. This is also the core of DDD (Domain-Driven Design) (For more on Domain-Driven Design, see: https://github.com/microwind/design-patterns/tree/main/domain-driven-design).

### Level Division

- High-level modules: Business layer, application layer, represent operations to be performed, i.e., "what to do", belong to the caller
- Low-level modules: Logic layer, data layer, represent specific implementation details, i.e., "how to do", belong to the called
- Abstract layer: Interfaces or abstract classes, define contracts between high-level and low-level

### Implementation Points

1. Program to interfaces, not to implementations
2. Pass dependent objects through dependency injection (constructor, setter methods, etc.)
3. High-level modules define interfaces, low-level modules implement interfaces
4. Use IoC containers (such as Spring) to manage object dependencies

### How to Verify
**Change Level**

- ❌ When modifying underlying implementation, need to modify high-level calling code simultaneously
- ❌ Adding a new implementation method requires modifying multiple calling codes
- ❌ Difficult to conduct unit tests, cannot replace dependencies with mock objects
- ❌ When replacing underlying technical solutions, the impact is too large

**Structure Level**

- ❌ High-level classes directly new low-level class instances
- ❌ Use concrete types for declarations in classes instead of interfaces or abstract classes
- ❌ Use concrete types for method parameters instead of abstract types
- ❌ Code is filled with new keywords to create dependent objects

### Negative Example Code

```java
// Notification class that violates Dependency Inversion Principle
class Notification {
    // Directly depend on concrete implementation classes, tight coupling
    private MessageSender messageSender = new MessageSender()
    private MailSender mailSender = new MailSender()
    private PushSender pushSender = new PushSender()
    
    // Provide separate methods for each sending method
    method sendMessage(content) {
        messageSender.send(content)
    }
    
    method sendEmail(content) {
        mailSender.send(content)
    }
    
    method sendPush(content) {
        pushSender.send(content)
    }
    // Need to modify this class when adding new sending methods
}

// Concrete implementation classes
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

// Usage requires new different concrete implementation classes
```

### Positive Example Code

```java
// Sender interface - abstract layer
interface Sender {
    method send(content)
}

// Concrete sender implementation classes - low-level modules
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

// Notification business class - high-level depends on abstract interface, not concrete implementation class, complies with Dependency Inversion Principle
class Notification {
    private Sender sender
    
    // Inject dependency through constructor
    constructor(Sender sender) {
        this.sender = sender
    }
    
    method send(content) {
        // Call abstract method, don't care about concrete implementation
        this.sender.send(content)
    }
    
    // Switch implementation through setter method
    method setSender(Sender sender) {
        this.sender = sender
    }
}

// Usage example
notification = new Notification(new MessageSender())
notification.send("Hello")  // Send text message

notification.setSender(new MailSender())
notification.send("Hello")  // Send email
```

### Practical Insights

The Dependency Inversion Principle is closely related to Inversion of Control (IoC) and Dependency Injection (DI). In actual projects, we usually use frameworks like Spring to implement dependency injection, thus better following the Dependency Inversion Principle.


## 4. Interface Segregation Principle (ISP)

### Core Idea

Clients should not be forced to depend on interfaces they don't need. Break down large interfaces into multiple small interfaces, each serving a specific submodule.

### Implementation Points

1. Interfaces are built on minimal abstraction, with single and clear functions
2. Clients only depend on the interface methods they must have, i.e., the principle of minimal dependency
3. Avoid overly bloated interfaces, but also avoid overly atomized interfaces
4. Use interface inheritance to combine multiple small interfaces

### How to Verify
**Change Level**

- ❌ Interface changes affect implementation classes that don't use the method
- ❌ Implementation classes have a lot of empty implementations or methods that throw "not supported" exceptions
- ❌ Clients are forced to depend on methods they don't need
- ❌ Modifying a feature requires modifying multiple unrelated implementation classes

**Structure Level**

- ❌ Too many methods in interface (more than 10 methods need attention)
- ❌ Interface contains methods for multiple different responsibilities
- ❌ Implementation classes only implement part of the interface methods, other methods are empty implementations
- ❌ Vague interface naming (such as IManager, IHandler, etc.)

### Negative Example Code

```java
// Bloated interface that violates Interface Segregation Principle
interface DeviceController {
    method turnOnTV()
    method turnOffTV()
    method adjustTVVolume(volume)
    method changeTVChannel(channel)
    method turnOnLight()
    method turnOffLight()
    method changeLightColor(color)
}

// Light implementation class forced to implement unnecessary methods
class Light implements DeviceController {
    // Unnecessary TV methods, forced to provide empty implementations or throw exceptions
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
    
    // Actual needed light methods
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

// TV implementation class has the same problem
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
    
    // Unnecessary light methods
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

### Positive Example Code

```java
// Basic device control interface - basic functions needed by all devices
interface DeviceController {
    method turnOn()
    method turnOff()
}

// Light control interface - only contains light-specific functions
interface LightController extends DeviceController {
    method changeLightColor(color)
}

// TV control interface - only contains TV-specific functions
interface TVController extends DeviceController {
    method adjustTVVolume(volume)
    method changeTVChannel(channel)
}

// Light implementation class - only implements needed interfaces, complies with Interface Segregation Principle
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

// TV implementation class - only implements needed interfaces, complies with Interface Segregation Principle
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

### Practical Insights

The Interface Segregation Principle complements the Single Responsibility Principle, both aiming to keep code clear and concise. When designing interfaces, we should split them according to the actual needs of clients, rather than designing a "comprehensive" interface. Reasonable interface splitting can improve the flexibility and maintainability of the system, while also making code easier to understand and test.

## 5. Composite Reuse Principle (CRP)

### Core Idea

Try to use composition or aggregation instead of inheritance to achieve reuse. Composition/aggregation is more extensible and has better loose coupling than inheritance.

### Concept Distinction

- Aggregation: Indicates that an object owns another object, which is a loose whole-part relationship where the whole and part can exist independently (has-a relationship), with no strong dependency.
- Composition: Indicates that an object owns other objects, which is a stronger ownership relationship, i.e., a whole-part relationship where the whole object strongly depends on the part objects, and the part cannot exist independently (contains-a relationship)
- Inheritance: Indicates that a class (called a subclass or derived class) is built based on another class (called a parent class, base class, or superclass), which is an "is-a" relationship. The subclass automatically has the properties and methods of the parent class and can extend, modify, or override them.

### Implementation Points

1. Prioritize composition/aggregation over inheritance to achieve code reuse
2. Only use inheritance when there is a clear "is-a" relationship
3. Pass objects to be reused through dependency injection
4. Use interfaces to define behavior and implement functionality through composition

### How to Verify
**Change Level**

- ❌ Modifying the parent class affects all subclasses
- ❌ Subclasses cannot flexibly replace or combine different functional modules
- ❌ Too deep inheritance hierarchy within a project (more than 3 levels need attention, no more than 5 levels at most)
- ❌ Cannot dynamically change object behavior at runtime

**Structure Level**

- ❌ Using inheritance merely to reuse code, not for "is-a" relationship
- ❌ Subclass overriding parent class methods just to return null or throw exceptions
- ❌ Complex dependency relationships caused by multiple inheritance
- ❌ Inheritance breaks encapsulation, with subclasses depending on parent class implementation details

### Negative Example Code

```java
// Design that violates Composite Reuse Principle - excessive use of inheritance
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

// Employee class inherits Person class
// Problem: Is an employee "is-a" person? This inheritance relationship is not flexible enough
class Employee extends Person {
    protected int id
    protected String title
    
    method work() {
        return true
    }
}

// Engineer class inherits Employee class
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

// Manager class inherits Employee class
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

// Problem summary:
// 1. Too long inheritance chain: Engineer/Manager -> Employee -> Person
// 2. If Person class changes, all subclasses will be affected
// 3. Cannot replace Person information at runtime
// 4. Difficult to extend: If other attributes need to be added, the inheritance structure will become more complex
```

### Positive Example Code

```java
// Person class - independent entity class
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

// Abstract Employee class - associates Person class through aggregation, complies with Composite Reuse Principle
abstract class Employee {
    // Aggregates Person class, loose coupling, more flexible
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

// Engineer class - obtains Person information through composition, complies with Composite Reuse Principle
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

// Manager class - obtains Person information through composition, complies with Composite Reuse Principle
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

// Usage example
person = new Person("Zhang San", 30)
engineer = new Engineer(1001, "Senior Engineer", person)
engineer.work()  // Output: Engineer is working: Zhang San

// Advantages:
// 1. Can flexibly replace Person objects
// 2. Changes to Person class do not directly affect Employee class
// 3. Simple inheritance hierarchy, easy to maintain
// 4. Can dynamically change person attribute at runtime
```

### Practical Insights

Inheritance is a strong coupling relationship that tightly binds subclasses to parent classes. Composition and aggregation, on the other hand, are weak coupling relationships that are more flexible and easier to extend. In actual projects, we should prioritize composition/aggregation, only using inheritance when there is indeed an "is-a" relationship. Remember: Favor composition over inheritance.

## 6. Law of Demeter (LoD)

### Core Idea

An object should know as little as possible about other objects, and the coupling between objects should be as low as possible. An object should only interact with its close friends (direct member variables, method parameters, objects created inside methods, and static methods, etc.), and should not interact with陌生 objects. Also known as the "Least Knowledge Principle".

### Definition of Friends
**An object's friends include:**

- The current object itself (this)
- Member variables: Objects that exist as class properties
- Method parameters: Objects passed through method parameters
- Objects created within methods: Objects instantiated inside methods
- Static members: Objects accessed directly through the class

### Stranger Objects

Objects returned by the properties or methods of friend objects

### Implementation Points

1. Identify an object's friends and only communicate with friends
2. Encapsulate object behavior and hide internal details
3. Avoid chain calls (such as a.getB().getC().doSomething())
4. Reasonably divide module and class responsibility boundaries
5. Appropriately use design patterns such as mediator and facade

### How to Verify

**Change Level**

- ❌ Modifying the internal implementation of a class causes other unrelated classes to also need modification
- ❌ Too long call chains between objects
- ❌ A class knows too many internal details of other classes
- ❌ Difficult to test a class alone (need to construct complex object dependency chains)

**Structure Level**

- ❌ Presence of chain calls (such as obj.getA().getB().getC())
- ❌ Too many method parameters (more than 3-4 parameters need attention)
- ❌ Class directly accesses internal objects of other classes
- ❌ Class has dependency relationships with many unrelated classes

### Negative Example Code

```java
// Product class
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

// Customer class that violates Law of Demeter
class Customer {
    private String name
    private List<Product> products
    
    constructor(name) {
        this.name = name
        this.products = new ArrayList()
    }
    
    method buy(Product product) {
        // Problem 1: Directly access Product details, interact with stranger objects
        if (product.getPrice() > 1000) {
            print(product.getName() + "'s price exceeds limit")
        } else {
            products.add(product)
            // Problem 2: Takes on price calculation responsibility, violating Single Responsibility
            double total = this.calculateTotalPrice()
            print(name + " purchased " + product.getName() + " for " + total)
        }
    }
    
    // Problem 3: Price calculation should be handled by a specialized class
    private method calculateTotalPrice() {
        double total = 0
        for (Product product : products) {
            // Problem 4: Traverses collection and accesses each Product's internal properties
            total += product.getPrice()
        }
        return total
    }
}

// Problem summary:
// 1. Customer directly depends on Product's internal implementation (getPrice, getName)
// 2. Customer knows too many details about Product
// 3. Customer takes on too many responsibilities (purchasing, validation, calculation)
// 4. If Product's price calculation logic changes, Customer also needs modification
```

### Positive Example Code

```java
// Product class
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

// Shopping cart class - acts as intermediary between Customer and Product
class ShoppingCart {
    private List<Product> products
    
    constructor() {
        this.products = new ArrayList()
    }
    
    method addProduct(Product product) {
        products.add(product)
    }
    
    // Encapsulate price calculation logic
    method calculateTotalPrice() {
        double total = 0
        for (Product product : products) {
            total += product.getPrice()
        }
        return total
    }
    
    // Encapsulate product validation logic
    method validateProduct(Product product) {
        return product.getPrice() <= 1000
    }
}

// Customer class - only interacts with shopping cart (direct friend), complies with Law of Demeter
class Customer {
    private String name
    private ShoppingCart cart  // Member variable, is a friend
    
    constructor(name) {
        this.name = name
        this.cart = new ShoppingCart()
    }
    
    method buy(Product product) {
        // Only interact with cart, not directly with Product details
        if (cart.validateProduct(product)) {
            cart.addProduct(product)
            double total = cart.calculateTotalPrice()
            print(name + " purchased " + product.getName() + " for " + total)
        } else {
            print(product.getName() + "'s price exceeds limit")
        }
    }
}

// Usage example
customer = new Customer("Zhang San")
product = new Product("Laptop", 5000)
customer.buy(product)

// Advantages:
// 1. Customer doesn't need to understand Product's internal details
// 2. Clear responsibilities: ShoppingCart is responsible for product management and calculation
// 3. Low coupling: Modifying Product doesn't affect Customer
// 4. Easy to test and maintain
```

### Practical Insights

The Law of Demeter requires us to maintain appropriate distance between objects, reducing coupling between objects through encapsulation and mediator patterns. However, excessive use of the Law of Demeter may lead to a large number of mediator classes in the system, increasing system complexity. Therefore, it's necessary to find a balance between reducing coupling and maintaining simplicity, and use it appropriately.

**Key principle:** Don't talk to strangers, only communicate with your direct friends.

## 7. Liskov Substitution Principle (LSP)

### Core Idea

Subclass objects (derived classes) must be able to replace their parent class objects (base classes) without affecting program correctness. All places referencing the base class must be able to transparently use objects of its subclasses, and the program behavior will not change.

### Implementation Points

1. Subclasses must fully implement the abstract methods of the parent class, and subclasses can have their own features and methods
2. Behavioral consistency: When subclass overrides parent class methods, it should exhibit behavior consistent with the parent class object
3. Try not to force overriding parent class methods: Subclasses should extend parent class functionality by adding new methods rather than changing behavior by overriding parent class methods
4. Subclass input parameters should be more relaxed than parent class (contravariance), subclass return values should be stricter than parent class (covariance), and subclasses should not throw exceptions not declared by the parent class

### How to Verify
**Change Level**

- ❌ Using subclass to replace parent class causes program errors or exceptions
- ❌ Need to distinguish different subclasses through type judgment (instanceof)
- ❌ After subclass overrides parent class method, the original behavior logic is changed
- ❌ Subclass needs special handling to work normally

**Structure Level**

- ❌ Subclass overrides parent class method but doesn't call parent class method (super)
- ❌ Subclass method preconditions are stricter than parent class
- ❌ Subclass method postconditions are looser than parent class
- ❌ Subclass throws exceptions not declared in parent class method signature
- ❌ Subclass changes the expected behavior of parent class method

### Negative Example Code

```java
// Abstract shape parent class
abstract class Shape {
    method draw() {
        print("Drawing Shape. area: " + this.area())
    }
    
    abstract method area()
}

// Square class - violates Liskov Substitution Principle
class Square extends Shape {
    private double side
    
    constructor(side) {
        this.side = side
    }
    
    // Problem: Overrides parent class's draw method, changing original behavior
    method draw() {
        if (this.checkArea()) {
            print("Drawing Square. area: " + this.area())
        } else {
            // Inconsistent behavior: Parent class always draws, subclass may not draw
            print("Don't draw square")
            return  // Parent class method has no early return logic
        }
    }
    
    method checkArea() {
        return this.area() <= 100
    }
    
    method area() {
        return side * side
    }
}

// Rectangle class
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

// Usage scenario problem demonstration
function processShape(Shape shape) {
    shape.draw()  // Expect all Shape to draw normally
    // But Square won't draw when area exceeds 100, violating parent class contract
}

processShape(new Rectangle(10, 20))  // Draws normally
processShape(new Square(15))  // Won't draw, violates expectation!
```

### Positive Example Code

```java
// Abstract shape parent class
abstract class Shape {
    method draw() {
        print("Drawing Shape. area: " + this.area())
    }
    
    abstract method area()
}

// Square class - complies with Liskov Substitution Principle
class Square extends Shape {
    private double side
    
    constructor(side) {
        this.side = side
    }
    
    // Doesn't override parent class's draw method, maintains behavioral consistency
    // Extends functionality by adding new methods instead of changing parent class behavior
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

// Rectangle class
class Rectangle extends Shape {
    private double width
    private double height
    
    constructor(width, height) {
        this.width = width
        this.height = height
    }
    
    // Only implements abstract method, doesn't change parent class behavior
    method area() {
        return width * height
    }
}

// Usage scenario
function processShape(Shape shape) {
    shape.draw()  // All Shape subclasses can draw normally
}

processShape(new Rectangle(10, 20))  // Draws normally
processShape(new Square(15))  // Draws normally, meets expectation

// If special handling is needed, use subclass-specific methods
square = new Square(15)
square.drawSquare()  // Use extension method

// Advantages:
// 1. Subclass is fully compatible with parent class and can be safely replaced
// 2. Maintains behavioral consistency
// 3. Extends functionality by adding new methods instead of changing original behavior
// 4. Code is easier to understand and maintain
```

### Practical Insights

The Liskov Substitution Principle is the foundation of inheritance reuse, which ensures the correctness of the inheritance hierarchy. If a subclass cannot completely replace its parent class, then this inheritance relationship has problems. When designing inheritance relationships, we should ensure:

- Subclasses can fully comply with the parent class's behavioral contract
- When extending functionality, subclasses should add new methods instead of changing the behavior of parent class methods
- If behavior changes are needed, consider using composition instead of inheritance

**Judgment standard:** If replacing a parent class object with a subclass object in code doesn't change the program's behavior, it means it complies with the Liskov Substitution Principle.

## Summary

The 7 principles of object-oriented design are not isolated; they are interrelated and complementary. In actual projects, we need to flexibly apply these principles according to specific situations, finding a balance between code quality and development efficiency, rather than applying them rigidly.

## Principle Review

| Principle | English Abbreviation | Core Goal | Key Points |
|-----------|---------------------|-----------|------------|
| Single Responsibility Principle | SRP | Ensure each class has only one responsibility | A class has only one reason to change |
| Open-Closed Principle | OCP | Ensure system extensibility | Open for extension, closed for modification |
| Dependency Inversion Principle | DIP | Ensure system loose coupling | Depend on abstractions not concrete implementations |
| Interface Segregation Principle | ISP | Ensure interface simplicity | Clients should not depend on interfaces they don't need |
| Composite Reuse Principle | CRP | Ensure system flexibility | Prioritize composition/aggregation over inheritance |
| Law of Demeter | LoD | Ensure system low coupling | Only communicate with direct friends |
| Liskov Substitution Principle | LSP | Ensure inheritance hierarchy correctness | Subclasses must be able to replace parent classes |

### Hierarchical Relationship
```
Open-Closed Principle (OCP) - Core Goal
    ├── Single Responsibility Principle (SRP) - Basic Guarantee
    ├── Dependency Inversion Principle (DIP) - Implementation Method
    ├── Interface Segregation Principle (ISP) - Interface Design
    ├── Liskov Substitution Principle (LSP) - Inheritance Constraint
    ├── Composite Reuse Principle (CRP) - Reuse Method
    └── Law of Demeter (LoD) - Coupling Control
```


## Final Words

As senior programmers and software architects, we should be able to identify code that violates design principles and improve them through refactoring. Only in this way can we build high-quality, maintainable, and extensible systems.

But **design principles are not silver bullets**. They are just guidelines for our design, not creeds we must adhere to. We cannot overly admire design principles and design patterns, let alone **design for the sake of design**, otherwise, we will put the cart before the horse, making the originally simple and clear structure redundant and complex.

In short, our core purpose is **to make complex systems simple, clear, maintainable, and easier for people to understand** (high-level languages are for people, not machines). In actual development, we usually need to make trade-offs between different principles. For example, to satisfy the Open-Closed Principle, we may need to introduce more abstraction layers, which will increase system complexity. Therefore, we need to **find a balance between design principles and actual requirements** according to the specific situation of the project.

Remember, design principles need to be applied flexibly, not followed rigidly. Their ultimate goal is to improve code maintainability and extensibility. The key to solving complex problems **lies in understanding things according to their own思路 (development)** rather than forcibly changing their original logic. As stated in the Tao Te Ching: Man follows Earth, Earth follows Heaven, Heaven follows Tao, and Tao follows Nature. **When we do things according to the true nature of things, we will be able to use design principles and design patterns得心应手.** "What's learned from books is superficial after all; it's crucial to have it personally tested somehow." Only through continuous application and reflection in practice can we truly master the essence of these principles.

**More design patterns and design principles source code: [https://github.com/microwind/design-patterns](https://github.com/microwind/design-patterns)**