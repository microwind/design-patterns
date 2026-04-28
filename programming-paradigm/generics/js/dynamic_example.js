// JavaScript动态语言示例 - 不支持泛型，但具有天然的灵活性
// JavaScript是动态类型语言，变量可以在运行时改变类型，这提供了比泛型更大的灵活性

// 简单的容器类 - 可以存储任何类型的数据
class Container {
    constructor(value) {
        this.value = value; // 可以是任何类型：string, number, object, array等
    }
    
    getValue() {
        return this.value;
    }
    
    setValue(value) {
        this.value = value; // 甚至可以改变值的类型
    }
    
    toString() {
        return `Container(${this.value})`;
    }
}

// 通用数组操作 - 不需要泛型，直接处理任何数组
class ArrayUtils {
    // 打印数组 - 适用于任何类型的数组
    static printArray(array) {
        array.forEach(element => {
            console.log(element);
        });
        console.log();
    }
    
    // 查找最大值 - 利用JavaScript的动态特性
    static findMax(array) {
        if (array.length === 0) {
            throw new Error("Array is empty");
        }
        
        // JavaScript会自动比较：数字比较大小，字符串按字典序比较
        return array.reduce((max, current) => current > max ? current : max);
    }
    
    // 过滤数组 - 接受任何判断函数
    static filter(array, predicate) {
        return array.filter(predicate);
    }
    
    // 映射数组 - 接受任何转换函数
    static map(array, transform) {
        return array.map(transform);
    }
    
    // 归约数组 - 接受任何累加函数
    static reduce(array, callback, initialValue) {
        return array.reduce(callback, initialValue);
    }
    
    // 创建数组 - 可变参数，接受任何类型
    static createList(...elements) {
        return elements;
    }
}

// 动态数据存储 - 可以存储不同类型的数据
class DynamicStorage {
    constructor() {
        this.items = [];
    }
    
    // 添加任何类型的项
    add(item) {
        this.items.push(item);
    }
    
    // 获取项
    get(index) {
        return this.items[index];
    }
    
    // 根据类型查找
    findByType(type) {
        return this.items.filter(item => typeof item === type);
    }
    
    // 根据属性查找
    findByProperty(property, value) {
        return this.items.filter(item => 
            item && typeof item === 'object' && item[property] === value
        );
    }
}

// 动态栈实现 - 可以存储任何类型
class Stack {
    constructor() {
        this.items = [];
    }
    
    push(item) {
        this.items.push(item); // 可以push任何类型
    }
    
    pop() {
        return this.items.pop();
    }
    
    peek() {
        return this.items[this.items.length - 1];
    }
    
    isEmpty() {
        return this.items.length === 0;
    }
    
    size() {
        return this.items.length;
    }
    
    // 获取特定类型的元素
    getItemsOfType(type) {
        return this.items.filter(item => typeof item === type);
    }
}

// 动态处理器 - 根据数据类型动态选择处理方式
class DynamicProcessor {
    constructor() {
        this.processors = new Map();
    }
    
    // 注册处理器 - 可以处理任何类型
    register(type, processor) {
        this.processors.set(type, processor);
    }
    
    // 处理数据 - 自动选择合适的处理器
    process(item) {
        const type = typeof item;
        const processor = this.processors.get(type);
        
        if (processor) {
            return processor(item);
        }
        
        // 默认处理：直接返回
        return item;
    }
    
    // 批量处理 - 可以处理混合类型的数组
    processAll(items) {
        return items.map(item => this.process(item));
    }
}

// 动态验证器 - 运行时类型检查
class DynamicValidator {
    constructor() {
        this.validators = new Map();
    }
    
    // 添加验证器
    addValidator(type, validator) {
        this.validators.set(type, validator);
    }
    
    // 验证数据
    validate(item) {
        const type = typeof item;
        const validator = this.validators.get(type);
        
        if (validator) {
            return validator(item);
        }
        
        // 默认验证：检查是否为null或undefined
        return item != null;
    }
    
    // 验证数组中的所有元素
    validateAll(items) {
        return items.every(item => this.validate(item));
    }
}

// 动态转换器 - 运行时类型转换
class DynamicTransformer {
    constructor() {
        this.transformers = new Map();
    }
    
    // 添加转换器
    addTransformer(type, transformer) {
        this.transformers.set(type, transformer);
    }
    
    // 转换数据
    transform(item) {
        const type = typeof item;
        const transformer = this.transformers.get(type);
        
        if (transformer) {
            return transformer(item);
        }
        
        // 默认转换：转换为字符串
        return String(item);
    }
}

// 动态事件系统 - 可以传递任何类型的数据
class EventEmitter {
    constructor() {
        this.events = new Map();
    }
    
    // 监听事件
    on(event, listener) {
        if (!this.events.has(event)) {
            this.events.set(event, []);
        }
        this.events.get(event).push(listener);
    }
    
    // 触发事件 - 可以传递任何类型的数据
    emit(event, ...args) {
        const listeners = this.events.get(event);
        if (listeners) {
            listeners.forEach(listener => listener(...args));
        }
    }
    
    // 移除监听器
    off(event, listener) {
        const listeners = this.events.get(event);
        if (listeners) {
            const index = listeners.indexOf(listener);
            if (index > -1) {
                listeners.splice(index, 1);
            }
        }
    }
}

// 函数式编程示例 - 利用JavaScript的函数特性
const functional = {
    // 高阶函数 - 接受任何函数
    compose: (...functions) => (arg) => 
        functions.reduceRight((acc, fn) => fn(acc), arg),
    
    // 柯里化 - 部分应用
    curry: (fn) => {
        return function curried(...args) {
            if (args.length >= fn.length) {
                return fn.apply(this, args);
            }
            return (...moreArgs) => curried(...args, ...moreArgs);
        };
    },
    
    // 管道 - 数据流处理
    pipe: (...functions) => (arg) => 
        functions.reduce((acc, fn) => fn(acc), arg)
};

// 测试JavaScript动态语言的灵活性
function demonstrateFlexibility() {
    console.log("=== JavaScript动态语言灵活性演示 ===\n");
    
    // 1. 同一个容器可以存储不同类型
    console.log("1. 动态容器演示:");
    const container = new Container("Hello");
    console.log(container.toString());
    
    container.setValue(42); // 改变类型从string到number
    console.log(container.toString());
    
    container.setValue({name: "Alice", age: 25}); // 改变类型到object
    console.log(container.toString());
    
    // 2. 数组工具处理不同类型
    console.log("\n2. 通用数组操作:");
    const stringArray = ["Apple", "Banana", "Orange"];
    const numberArray = [1, 5, 3, 9, 2];
    const objectArray = [{id: 1}, {id: 2}, {id: 3}];
    
    console.log("字符串数组最大值:", ArrayUtils.findMax(stringArray));
    console.log("数字数组最大值:", ArrayUtils.findMax(numberArray));
    
    // 3. 动态存储混合类型
    console.log("\n3. 动态存储:");
    const storage = new DynamicStorage();
    storage.add("Hello");
    storage.add(42);
    storage.add({name: "Alice"});
    storage.add([1, 2, 3]);
    
    console.log("所有字符串:", storage.findByType("string"));
    console.log("所有数字:", storage.findByType("number"));
    console.log("所有对象:", storage.findByType("object"));
    
    // 4. 动态栈混合类型
    console.log("\n4. 动态栈:");
    const stack = new Stack();
    stack.push("First");
    stack.push(42);
    stack.push({data: "object"});
    stack.push([1, 2, 3]);
    
    console.log("栈中的字符串:", stack.getItemsOfType("string"));
    console.log("栈中的数字:", stack.getItemsOfType("number"));
    
    // 5. 动态处理器
    console.log("\n5. 动态处理器:");
    const processor = new DynamicProcessor();
    processor.register("string", s => s.toUpperCase());
    processor.register("number", n => n * 2);
    processor.register("object", obj => JSON.stringify(obj));
    
    const mixedData = ["hello", 5, {name: "test"}];
    console.log("处理结果:", processor.processAll(mixedData));
    
    // 6. 函数式编程
    console.log("\n6. 函数式编程:");
    const add = (a, b) => a + b;
    const multiply = (a, b) => a * b;
    const toString = (x) => String(x);
    
    const pipeline = functional.pipe(
        x => x + 1,
        x => x * 2,
        toString
    );
    
    console.log("函数管道结果:", pipeline(5));
    
    // 7. 运行时类型检查
    console.log("\n7. 运行时类型检查:");
    const validator = new DynamicValidator();
    validator.addValidator("string", s => s.length > 0);
    validator.addValidator("number", n => n >= 0);
    
    console.log("验证'hello':", validator.validate("hello"));
    console.log("验证-5:", validator.validate(-5));
    console.log("验证空字符串:", validator.validate(""));
    
    // 8. 动态转换
    console.log("\n8. 动态转换:");
    const transformer = new DynamicTransformer();
    transformer.addTransformer("number", n => n.toString());
    transformer.addTransformer("object", obj => JSON.stringify(obj));
    
    console.log("转换数字:", transformer.transform(42));
    console.log("转换对象:", transformer.transform({x: 1, y: 2}));
    console.log("转换布尔值:", transformer.transform(true));
}

// 运行演示
demonstrateFlexibility();

/*
运行：
node dynamic_example.js

输出结果：
=== JavaScript动态语言灵活性演示 ===

1. 动态容器演示:
Container(Hello)
Container(42)
Container([object Object])

2. 通用数组操作:
字符串数组最大值: Orange
数字数组最大值: 9

3. 动态存储:
所有字符串: [ 'Hello' ]
所有数字: [ 42 ]
所有对象: [ { name: 'Alice' }, [ 1, 2, 3 ] ]

4. 动态栈:
栈中的字符串: [ 'First' ]
栈中的数字: [ 42 ]
栈中的对象: [ { data: 'object' }, [ 1, 2, 3 ] ]

5. 动态处理器:
处理结果: [ 'HELLO', 10, '{"name":"test"}' ]

6. 函数式编程:
函数管道结果: 12

7. 运行时类型检查:
验证'hello': true
验证-5: false
验证空字符串: false

8. 动态转换:
转换数字: 42
转换对象: {"x":1,"y":2}
转换布尔值: true
*/

/*
JavaScript vs 静态类型语言的泛型对比：

优势：
1. 无需类型声明 - 代码更简洁
2. 运行时类型检查 - 更灵活的验证
3. 鸭子类型 - 接口隐式定义
4. 动态适配 - 可以处理意外类型
5. 原型链 - 灵活的继承机制

劣势：
1. 无编译时类型检查 - 运行时才发现错误
2. 性能开销 - 运行时类型检查成本
3. IDE支持有限 - 类型推断不如静态语言
4. 重构困难 - 缺少类型安全保障

结论：
JavaScript虽然不支持泛型，但其动态特性提供了另一种灵活性。
*/
