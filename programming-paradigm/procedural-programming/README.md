# 面向过程概述
面向过程的编程（Procedural Programming）是一种以过程（函数或子程序）为中心的编程范式。它通过一系列的过程或函数调用来组织代码。每个过程都是一段完成特定任务的代码，可以接受输入参数并返回结果。这种方法侧重于定义可重用的函数和数据结构，并通过控制流来解决具体问题。

## 结构图形示例

```mermaid
graph TD
    A[核心业务逻辑<br/>由多个过程组成] --> B[Procedure 1<br/>过程1]
    B --> C[Procedure 2<br/>过程2]
    C --> D[Procedure 3<br/>过程3]

    style A fill:#51cf66,stroke:#2b8a3e,stroke-width:3px,color:#fff
    style B fill:#20c997,stroke:#0ca678,stroke-width:2px,color:#fff
    style C fill:#15aabf,stroke:#0b7285,stroke-width:2px,color:#fff
    style D fill:#ffa94d,stroke:#e67700,stroke-width:2px,color:#fff
```

## 作用与优缺点

### 作用：

  - 提供了清晰的步骤和流程：通过定义明确的过程来实现功能，使程序逻辑更加直观。
  - 易于理解和学习：对于初学者来说，过程式的编程方式相对简单易懂。

### 优点：

  - 简单直接：易于理解和实现，特别适合小型项目或模块。
  - 控制流清晰：通过顺序、选择和循环三种基本控制结构，能够很好地表达算法逻辑。

### 缺点：

  - 模块化程度低：随着项目规模的增长，代码可能变得难以管理和维护。
  - 数据保护性差：由于数据通常在全局范围内共享，可能会导致意外的数据修改。

## 与其他编程范式的对比

  - 面向对象编程（OOP）：OOP 通过类和对象来组织代码，强调封装、继承和多态。面向过程编程则通过过程分解来实现模块化，适合处理简单的任务。

  - 面向切面编程（AOP）：AOP 通过切面将横切关注点从核心逻辑中分离出来，而面向过程编程通常将横切关注点直接嵌入到过程中。

  - 函数式编程（FP）：FP 强调不可变数据和纯函数，而面向过程编程则通过过程调用来实现功能。

## 应用场景
  - 小型程序：面向过程编程适合编写小型、简单的程序，例如脚本或工具。

  - 嵌入式系统：在资源受限的环境中，面向过程编程的高效性使其成为首选。

  - 算法实现：面向过程编程适合实现具体的算法逻辑，例如排序、搜索等。

# 简单例子
```c
// c语言例子，通过函数（过程）执行过程，以及通过代码来实现模块化和代码复用
#include <stdio.h>

// 过程 1：计算两个数的和
int add(int a, int b) {
    return a + b;
}

// 过程 2：计算两个数的差
int subtract(int a, int b) {
    return a - b;
}

int main() {
    int x = 10, y = 5;

    // 调用过程
    int sum = add(x, y);
    int difference = subtract(x, y);

    printf("Sum: %d\n", sum);
    printf("Difference: %d\n", difference);

    return 0;
}
```

```go
// go语言例子
package main

import "fmt"

// 过程 1：计算两个数的和
func add(a int, b int) int {
    return a + b
}

// 过程 2：计算两个数的差
func subtract(a int, b int) int {
    return a - b
}

func main() {
    x, y := 10, 5

    // 调用过程
    sum := add(x, y)
    difference := subtract(x, y)

    fmt.Printf("Sum: %d\n", sum)
    fmt.Printf("Difference: %d\n", difference)
}
```

```js
// js语言例子
// 过程 1：计算两个数的和
function add(a, b) {
    return a + b;
}

// 过程 2：计算两个数的差
function subtract(a, b) {
    return a - b;
}

let x = 10;
let y = 5;

// 调用过程
let sum = add(x, y);
let difference = subtract(x, y);

console.log("Sum: " + sum);
console.log("Difference: " + difference);
```

```py
# python语言例子
# 过程 1：计算两个数的和
def add(a, b):
    return a + b

# 过程 2：计算两个数的差
def subtract(a, b):
    return a - b

x = 10
y = 5

# 调用过程
sum_result = add(x, y)
difference = subtract(x, y)

print(f"Sum: {sum_result}")
print(f"Difference: {difference}")
```

```java
// java语言例子
public class ProceduralProgrammingJava {
    // 过程 1：计算两个数的和
    public static int add(int a, int b) {
        return a + b;
    }

    // 过程 2：计算两个数的差
    public static int subtract(int a, int b) {
        return a - b;
    }

    public static void main(String[] args) {
        int x = 10;
        int y = 5;

        // 调用过程
        int sum = add(x, y);
        int difference = subtract(x, y);

        System.out.println("Sum: " + sum);
        System.out.println("Difference: " + difference);
    }
}
```