# 泛型概述

泛型（Generics）是 Java 在 JDK 1.5 引入的一种语言特性，通过在类、接口和方法中使用类型参数，使代码在编译阶段就能进行严格的类型检查，从而提高类型安全性并减少强制类型转换。

## 结构图形示例
```text
使用泛型前：
+-------------+
| List        |
|  - Object[] |
+-------------+
       |
       v
   取出元素时需强制转换
       |
       v
+--------------------+
| String s = (String) l.get(0); |
+--------------------+

使用泛型后：
+-----------------+
| List<String>    |
+-----------------+
       |
       v
  编译期检查类型，取出时无需转换
       |
       v
+------------------+
| String s = list.get(0); |
+------------------+
```

## 作用与优缺点

  **作用：**
  - 在编译期进行类型检查，捕获类型不匹配错误。 
  - 提高代码复用性和可读性，无需大量的强制类型转换。

 **优点：**
  - 类型安全：编译器禁止向泛型集合插入错误类型的数据。 
  - 可读性：通过类型参数，代码意图更清晰。 
  - 复用性：同一份代码可应用于多种数据类型。

 **缺点：**
  - 类型擦除：运行时无法获取泛型类型信息，限制了某些反射和数组操作。 
  - 语法复杂：通配符、边界等用法初学者较难掌握。 
  - 与基本类型不兼容：无法直接对 int、double 等基本类型使用泛型。

## 与其他编程范式的对比
  - 面向对象编程（OOP）：OOP 关注对象和继承，泛型关注类型参数化，两者结合可编写更灵活的类和方法。 
  - 函数式编程（FP）：FP 强调不可变和高阶函数，泛型可与函数式接口（如 Function<T,R>）配合，增强函数式组件的通用性。 
  - 动态类型语言：如 JavaScript、Python，无需显式类型，灵活但类型安全性低；Java 泛型在保持静态类型优势的同时，提升了灵活性。
  - 泛型编程（GPG）：GPG 关注泛型，泛型关注类型参数化，两者结合可编写更灵活的类和方法。
  - 
## 应用场景
  - 集合框架：List<T>、Map<K,V> 等，确保集合中元素类型一致。 
  - 工具类与算法：如 Collections.sort(List<T>)、Arrays.asList(T...) 等通用操作。 
  - 自定义通用组件：例如通用缓存、数据访问层 DAO、消息处理框架等。

## 简单例子
```java
// 泛型类
public class Box<T> {
    private T value;
    public void set(T value) { this.value = value; }
    public T get() { return value; }
}

Box<String> box = new Box<>();
box.set("Hello Generics");
String s = box.get();
```

```csharp
// C# 泛型
public class Box<T> {
    public T Value { get; set; }
}

var box = new Box<int> { Value = 42 };
Console.WriteLine(box.Value);
```

```cpp
// C++ 模板
template<typename T>
class Box {
    public:
    void set(const T& v) { value = v; }
    T get() const { return value; }
    private:
    T value;
};

Box<double> box;
box.set(3.14);
std::cout << box.get();
```

```go
// Go 泛型
func Max[T comparable](a, b T) T {
    if a > b {
        return a
    }
    return b
}

fmt.Println(Max[int](1, 2))
fmt.Println(Max[string]("a", "z"))
```

```ts
// TypeScript 泛型
function identity<T>(arg: T): T {
    return arg;
}

let output = identity<string>("hello");
console.log(output);
```