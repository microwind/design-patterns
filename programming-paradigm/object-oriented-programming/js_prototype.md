
# JavaScript prototype 详解

JavaScript 中，**prototype（原型）** 是面向对象编程的核心概念之一。它通过 **原型链（Prototype Chain）** 实现继承，使对象可以共享其他对象的属性和方法。理解原型机制是掌握 JavaScript 面向对象编程的关键。

---

## 什么是 prototype？
每个 JavaScript **函数**（构造函数）都有一个 `prototype` 属性，它是一个对象。所有由该函数创建的 **实例对象** 都会继承这个原型对象的属性和方法。

```js
function Person(name) {
    this.name = name;
}

// 方法添加到原型，所有实例共享
Person.prototype.sayHello = function() {
    console.log(`Hello, my name is ${this.name}`);
};

const p1 = new Person("Joe");
const p2 = new Person("Mary");

p1.sayHello(); // Hello, my name is Joe
p2.sayHello(); // Hello, my name is Mary

// 实例的 __proto__ 指向构造函数的 prototype
console.log(p1.__proto__ === Person.prototype); // true
```
**在上面的代码中：**
- `Person.prototype` 是 `Person` 构造函数的原型对象。
- `sayHello` 方法被所有 `Person` 的实例共享，而不是每个实例都创建一份新的拷贝，节省内存。
- `p1.__proto__` 指向 `Person.prototype`，表示 p1 继承了 `Person.prototype` 上的方法。
- `__proto__` 是实例对象的隐式原型引用（非标准属性，可以用 `Object.getPrototypeOf()` 替代）。

---

## 属性`__proto__` 与 prototype 关系

JavaScript 中每个对象都有一个隐藏的 `__proto__` 属性（这个并非标准属性，虽然大部分浏览器都支持），它指向创建该对象的构造函数的 prototype：

```js
console.log(p1.__proto__ === Person.prototype); // true
console.log(Person.prototype.__proto__ === Object.prototype); // true
console.log(Object.prototype.__proto__ === null); // true
```
这个原型链的结构如下：
```js
// 访问对象属性时，若当前对象没有，则沿原型链向上查找。
// Object.prototype 是原型链的终点，其 __proto__ 为 null。
p1 → Person.prototype → Object.prototype → null
```

## 原型链继承
可以通过 `prototype` 让一个构造函数继承另一个构造函数的方法和属性。
注意，使用 Object.create 创建的子对象不会调用父构造函数，仅用于设置原型：
```js
function Parent(name) {
    this.name = name;
}

Parent.prototype.makeSound = function() {
    console.log("Parent are saying.");
};

function Child(name, age) {
    Parent.call(this, name); // 继承属性
    this.age = age;
}

// 使用 Object.create 创建新的原型对象，让 Child 继承 Parent 的方法
Child.prototype = Object.create(Parent.prototype);
// 修正 constructor 指向，否则 Child.prototype.constructor 会指向 Parent
Child.prototype.constructor = Child;

Child.prototype.speak = function() {
    console.log("Child is talking.");
};

const d = new Child("Child1", 18);
d.makeSound(); // Parent are saying.
d.speak(); // Child is talking.
```
**步骤分析：**
- **Object.create(Parent.prototype)**
创建一个新对象，其原型指向 `Parent.prototype`，确保子类原型不污染父类。

- **修复 constructor 指向**
若不修复，`Child.prototype.constructor` 将指向 `Parent`，导致实例的 `constructor` 错误。

- **构造函数借用 (Parent.call)**
在子类构造函数中调用父类构造函数，初始化实例属性。

## ES6 class 语法的 prototype
ES6 的 class 是原型的语法糖，本质仍基于原型链：
```js
class Person {
    constructor(name) {
        this.name = name;
    }
    
    // 方法自动添加到 Person.prototype
    sayHello() {
        console.log(`Hello, my name is ${this.name}`);
    }
}

const p = new Person("Tom");
p.sayHello(); // Hello, my name is Tom

// 静态方法添加到构造函数本身
Person.staticMethod = function() {
    console.log("This is a static method.");
};

console.log(Object.getPrototypeOf(p) === Person.prototype); // true
```
在这个例子中：
- `sayHello` 方法实际存储在 `Person.prototype`。
- `static` 关键字定义的方法属于构造函数本身，而非原型。


## prototype验证
**构造函数与原型的关系**
```js
// 验证 Object 是 Function 的实例
console.log(Object instanceof Function); // 输出: true

// 验证 Function 继承自 Object.prototype
console.log(Object.getPrototypeOf(Function.prototype) === Object.prototype); // 输出: true

// 自定义函数和对象
function A() {}
const a = new A();

// 验证自定义函数是 Function 的实例
console.log(A instanceof Function); // 输出: true

// 验证自定义对象的原型是自定义函数的 prototype
console.log(Object.getPrototypeOf(a) === A.prototype); // 输出: true

// 验证自定义函数的 prototype 的原型是 Object.prototype
console.log(Object.getPrototypeOf(A.prototype) === Object.prototype); // 输出: true

// 原型链的终点
console.log(Object.prototype.__proto__); // null
```

## prototype图形展示
```text
// 参考：https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Inheritance_and_the_prototype_chain

+----------------+  constructor  +---------------------+
|   Function     | <------------ |  Function.prototype |
| (Function 本身) | ------------> |    (Function 原型)   |
+----------------+   prototype   +---------------------+
                     __proto__       ^     |
                                     |     |
       |------------------------------     | __proto__
       |          __proto__                |
       |                                   v
+----------------+  constructor   +------------------+               +--------+
|    Function    | <----------  |  Object.prototype | --------->   |  终点   | 
| (Object 函数)   | ---------->  |  (所有对象的基类)   |  __proto__   |  null  |
+----------------+  prototype   +------------------+               +--------+
                                          ^
                                          |
                                          | __proto__
                                          |
+----------------+  constructor +----------------------+
| function Foo() |  <---------  |   Foo.prototype      |
|  (自定义函数)    |  ----------> |  (自定义函数原型对象)   |
+----------------+   prototype  +----------------------+
                                          ^
                                          |
                                          |
+----------------+                        |
|   new Foo()    | -----------------------| 
|  (函数实例对象)  |      __proto__
+----------------+           
```
<!-- 
## 函数对象实例化步骤
```js
const newFoo = new Foo()
1、创建一个新对象
function Foo() {}
// `newFoo` 是新创建的空对象
const newFoo = {};

2、将新对象的原型指向构造函数的 constructor.prototype 属性
// 此时 `newFoo` 的原型链指向了 `Foo.prototype`，
// 这意味着 `newFoo` 可以继承 `Foo.prototype` 上的属性和方法。
newFoo.__proto__ = Foo.prototype;  

3、执行构造函数，并将 this 绑定到新对象上
// `apply` 方法将 `newFoo` 作为 `this` 传入 `Foo` 构造函数。
// 如果 `Foo` 构造函数返回一个对象，那么 `result` 就会是该对象。
const result = Foo.apply(newFoo, args);  

4、// 返回新创建的对象
return newFoo;  // 如果 `Foo` 构造函数没有显式返回一个对象，则返回 `newFoo`。
``` -->

## 实例化对象步骤(new 关键字的执行过程)
实例化 `const newFoo = new Foo();` 的步骤
```javascript
function Foo() {}
const newFoo = new Foo();
```

### **1. 创建一个新对象**  
JavaScript 先创建一个新的空对象 `newFoo`。
```javascript
const newFoo = {};
```

### **2. 设置新对象的原型**  
`newFoo.__proto__` 被设置为 `Foo.prototype`，即 `newFoo` 继承了 `Foo.prototype` 的属性和方法。
```javascript
newFoo.__proto__ = Foo.prototype;
```

### **3. 执行构造函数，并绑定 `this`**  
```js
const result = Foo.apply(newFoo, arguments);
```
调用 `Foo` 构造函数，并将 `newFoo` 作为 `this` 传入。  
若 `Foo` 显式返回一个对象，则 `new` 操作符返回该对象；否则返回 `newFoo`。

### 返回对象
若构造函数返回对象，则返回该对象。否则返回新创建的 obj。
```javascript
return typeof result === "object" && result !== null ? result : newFoo;
```

---

基于上面 `newFoo = new Foo();` 进行分析。

## **原型链分析**
```javascript
newFoo.__proto__ === Foo.prototype   // ✅ `newFoo` 的原型是 `Foo.prototype`
Foo.prototype.__proto__ === Object.prototype   // ✅ `Foo.prototype` 的原型是 `Object.prototype`
Object.prototype.__proto__ === null   // ✅ `Object.prototype` 的原型是 `null`（即原型链的终点）
```

---

## **构造器关系**
```javascript
newFoo.constructor === Foo.prototype.constructor   // ✅ `newFoo` 的构造函数是 `Foo`
Foo.prototype.constructor === Foo   // ✅ `Foo.prototype` 的 `constructor` 指向 `Foo` 本身
Foo.prototype.constructor.prototype === Foo.prototype   // ✅ `Foo.prototype.constructor` 的 `prototype` 仍然是 `Foo.prototype`
```
**说明：**
- 当我们创建一个新对象时，它的 `constructor` 属性通常来源于它的原型（即 `Foo.prototype.constructor`）。
- 使用 `Object.create` 或修改原型时，有可能需要手动修正 `constructor` 指向。

---

## **`Function` 和 `Object` 互相指向**
```javascript
Foo.prototype.__proto__.constructor.__proto__ === Function.prototype   // ✅ `Object` 构造函数的 `__proto__` 指向 `Function.prototype`
Function.prototype === Object.__proto__   // ✅ `Function.prototype` 就是 `Object` 的 `__proto__`
Function.prototype.__proto__.__proto__ === null   // ✅ `Function.prototype.__proto__` 是 `Object.prototype`，再往上是 `null`
```

---

## **构造器和原型链的循环指向**
```javascript
Foo.prototype.constructor.prototype.constructor === Foo   // ✅ 循环指向 `Foo`
Foo.prototype.constructor.prototype.constructor.prototype === Foo.prototype   // ✅ 再次循环指向 `Foo.prototype`
Foo.prototype.constructor === Foo   // ✅ `Foo.prototype.constructor` 仍然指向 `Foo`
```

---

## **`Object` 和 `Function` 之间的关系**
```javascript
Object.prototype.constructor === Object   // ✅ `Object.prototype` 的 `constructor` 是 `Object`
Object.prototype.constructor.__proto__ === Function.prototype   // ✅ `Object` 构造函数本身是 `Function` 的一个实例
Function.constructor.__proto__ === Function.prototype   // ✅ `Function` 构造函数的 `__proto__` 也是 `Function.prototype`
Function.prototype.__proto__ === Object.prototype   // ✅ `Function.prototype` 继承自 `Object.prototype`
Function.__proto__.__proto__ === Object.prototype   // ✅ `Function.__proto__` 继承自 `Function.prototype`，最终指向 `Object.prototype`
Object.prototype.__proto__ === null   // ✅ `Object.prototype` 是原型链终点
```

---

## 原型使用的注意事项
- 避免直接修改内置原型
如 `Array.prototype.myMethod` = ... 可能导致兼容性问题。

- 原型属性的共享特性
引用类型（如数组）的属性可能被所有实例意外修改：
```js
function MyClass() {}
MyClass.prototype.data = [];

const a = new MyClass();
a.data.push(1); // 所有实例的 data 都会变化
```
- 性能优化
将方法定义在原型上，而非构造函数内，减少内存占用。

---

## **总结**
- **`prototype` 属性**
1. 每个 JavaScript 函数 都有一个 `prototype` 属性（除了箭头函数）。
2. `prototype` 是一个对象，所有由该函数创建的实例都会共享 `prototype` 上的方法。
3. `__proto__` 指向该对象的原型（即构造函数的 `prototype`），形成原型链。
4. 通过 `Object.create()` 进行原型继承，ES6 `class` 语法是 `prototype` 的语法糖。
5. 原型链终点 为 Object.prototype，其 __proto__ 为 null。

- **`new` 关键字的作用**
1. 创建一个新对象 `newFoo`
2. 设置 `newFoo.__proto__ = Foo.prototype`
3. 执行 `Foo` 并绑定 `this`
4. 返回 `newFoo` 或构造函数返回的对象

- **构造函数、原型和 `Object` 的关系**
1. `Foo.prototype` 继承自 `Object.prototype`
2. `Object.prototype` 是所有对象的原型链终点
3. `Object` 和 `Function` 互相指向，`Object` 也是 `Function` 的一个实例
4. `Function.prototype.__proto__ === Object.prototype`，最终 `Function` 也继承自 `Object`

更多链接：
[https://github.com/microwind/design-patterns](https://github.com/microwind/design-patterns/tree/main/programming-paradigm/object-oriented-programming)

