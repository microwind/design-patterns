// 泛型类示例
class GenericsExample<T> {
    private value: T;
    
    // 泛型构造函数
    constructor(value: T) {
        this.value = value;
    }
    
    // 泛型方法
    getValue(): T {
        return this.value;
    }
    
    setValue(value: T): void {
        this.value = value;
    }
    
    // 静态泛型方法
    static printArray<E>(array: E[]): void {
        array.forEach(element => {
            console.log(element);
        });
    }
    
    // 泛型方法比较两个值
    static findMax<T>(array: T[]): T {
        if (array.length === 0) {
            throw new Error("Array is empty");
        }
        
        return array.reduce((max, current) => {
            return current > max ? current : max;
        });
    }
    
    // 泛型集合示例
    static createList<T>(...elements: T[]): T[] {
        return elements;
    }
}

// 泛型接口示例
interface IRepository<T> {
    getById(id: number): T;
    add(item: T): void;
}

class Repository<T> implements IRepository<T> {
    private items: T[] = [];
    
    getById(id: number): T {
        return this.items[id];
    }
    
    add(item: T): void {
        this.items.push(item);
    }
}

// 泛型函数示例
function identity<T>(arg: T): T {
    return arg;
}

// 泛型约束示例
interface Lengthwise {
    length: number;
}

function getLength<T extends Lengthwise>(arg: T): number {
    return arg.length;
}

// 泛型工具类型示例（自定义版本，避免与内置类型冲突）
type MyPartial<T> = {
    [P in keyof T]?: T[P];
};

type MyRequired<T> = {
    [P in keyof T]-?: T[P];
};

// 泛型条件类型示例
type IsString<T> = T extends string ? true : false;

// 泛型映射类型示例
type MyReadonly<T> = {
    readonly [P in keyof T]: T[P];
};

// 泛型函数 - 过滤数组
function filter<T>(array: T[], predicate: (item: T) => boolean): T[] {
    return array.filter(predicate);
}

// 泛型函数 - 映射数组
function map<T, U>(array: T[], transform: (item: T) => U): U[] {
    return array.map(transform);
}

// 泛型函数 - 归约数组
function reduce<T, U>(array: T[], callback: (accumulator: U, item: T) => U, initialValue: U): U {
    return array.reduce(callback, initialValue);
}

// 泛型类 - 栈实现
class Stack<T> {
    private items: T[] = [];
    
    push(item: T): void {
        this.items.push(item);
    }
    
    pop(): T | undefined {
        return this.items.pop();
    }
    
    peek(): T | undefined {
        return this.items[this.items.length - 1];
    }
    
    isEmpty(): boolean {
        return this.items.length === 0;
    }
    
    size(): number {
        return this.items.length;
    }
}

// 泛型类 - 链表节点
class ListNode<T> {
    value: T;
    next: ListNode<T> | null;
    
    constructor(value: T) {
        this.value = value;
        this.next = null;
    }
}

class LinkedList<T> {
    private head: ListNode<T> | null = null;
    
    append(value: T): void {
        const newNode = new ListNode(value);
        
        if (!this.head) {
            this.head = newNode;
            return;
        }
        
        let current = this.head;
        while (current.next) {
            current = current.next;
        }
        current.next = newNode;
    }
    
    toArray(): T[] {
        const result: T[] = [];
        let current = this.head;
        
        while (current) {
            result.push(current.value);
            current = current.next;
        }
        
        return result;
    }
}

// 泛型函数 - 交换两个值
function swap<T>(a: T, b: T): [T, T] {
    return [b, a];
}

// 泛型函数 - 创建对象
function createObject<K extends string | number, V>(keys: K[], values: V[]): Record<K, V> {
    const obj = {} as Record<K, V>;
    
    keys.forEach((key, index) => {
        obj[key] = values[index];
    });
    
    return obj;
}

// 测试代码
function main(): void {
    // 泛型类使用
    const stringBox = new GenericsExample("Hello Generics");
    console.log(`String value: ${stringBox.getValue()}`);
    
    const integerBox = new GenericsExample(42);
    console.log(`Integer value: ${integerBox.getValue()}`);
    
    // 泛型方法使用
    const stringArray = ["Apple", "Banana", "Orange"];
    console.log("String array:");
    GenericsExample.printArray(stringArray);
    
    const intArray = [1, 5, 3, 9, 2];
    console.log("Integer array:");
    GenericsExample.printArray(intArray);
    
    // 泛型方法查找最大值
    console.log(`Max number: ${GenericsExample.findMax(intArray)}`);
    console.log(`Max string: ${GenericsExample.findMax(stringArray)}`);
    
    // 泛型集合
    const stringList = GenericsExample.createList("Hello", "World", "Generics");
    console.log(`String list: ${stringList.join(", ")}`);
    
    const intList = GenericsExample.createList(1, 2, 3, 4, 5);
    console.log(`Integer list: ${intList.join(", ")}`);
    
    // 泛型接口示例
    const repository = new Repository<string>();
    repository.add("Item 1");
    repository.add("Item 2");
    console.log(`Item by id 0: ${repository.getById(0)}`);
    
    // 泛型函数示例
    const output = identity<string>("hello");
    console.log(`Identity function output: ${output}`);
    
    // 泛型约束示例
    const strLength = getLength("Hello World");
    console.log(`String length: ${strLength}`);
    
    // 泛型函数过滤
    const evenNumbers = filter(intList, n => n % 2 === 0);
    console.log(`Even numbers: ${evenNumbers.join(", ")}`);
    
    // 泛型函数映射
    const strLengths = map(stringList, s => s.length);
    console.log(`String lengths: ${strLengths.join(", ")}`);
    
    // 泛型函数归约
    const sum = reduce(intList, (acc, n) => acc + n, 0);
    console.log(`Sum of numbers: ${sum}`);
    
    // 泛型栈使用
    const intStack = new Stack<number>();
    intStack.push(1);
    intStack.push(2);
    intStack.push(3);
    
    console.log("Stack operations:");
    while (!intStack.isEmpty()) {
        const value = intStack.pop();
        console.log(`Popped: ${value}`);
    }
    
    // 泛型链表使用
    const linkedList = new LinkedList<string>();
    linkedList.append("First");
    linkedList.append("Second");
    linkedList.append("Third");
    
    console.log(`Linked list: ${linkedList.toArray().join(" -> ")}`);
    
    // 泛型函数交换
    const [swappedA, swappedB] = swap(10, 20);
    console.log(`Swapped values: a=${swappedA}, b=${swappedB}`);
    
    // 泛型对象创建
    const obj = createObject(["name", "age"], ["Alice", 25]);
    console.log(`Created object:`, obj);
}

// 运行测试
main();

/*
编译和运行：
tsc generics_example.ts
node generics_example.js

输出结果：
String value: Hello Generics
Integer value: 42
String array:
Apple
Banana
Orange
Integer array:
1
5
3
9
2
Max number: 9
Max string: Orange
String list: Hello, World, Generics
Integer list: 1, 2, 3, 4, 5
Item by id 0: Item 1
Identity function output: hello
String length: 11
Even numbers: 2, 4
String lengths: 5, 5, 8
Sum of numbers: 15
Stack operations:
Popped: 3
Popped: 2
Popped: 1
Linked list: First -> Second -> Third
Swapped values: a=20, b=10
Created object: { name: 'Alice', age: 25 }
*/
