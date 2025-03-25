# 一文搞懂什么是多态

# 概念

面向对象程序设计有三要素：封装、继承（或组合）、多态，前两者较好理解，多态总让人困惑，不知道具体有什么作用，更不知道为什么要用多态。今天就来详细分析下什么是多态，以及多态有哪些好处，为什么要用多态？

多态是指同一行为作用于不同对象时，可以表现出多种不同的形式和结果来。例如，子类继承父类并覆盖其方法后，用父类引用指向子类对象并调用该方法时，实际执行的是子类的方法。

这种根据对象实际类型而非声明类型来确定执行方法的行为，就是多态性的体现。多态主要通过继承和接口实现，允许同一接口有多种不同的实现方式。

# 多态的分类

- 编译时多态，又称静态绑定，是指编译器在编译时通过检查引用类型的方法是否存在，来定位到相应的类及其方法，而不检查实际对象是否支持该方法。编译时多态主要体现在方法重载上，即根据参数类型、数量和顺序，在编译时确定要执行的方法。

- 运行时多态，又称动态绑定，是指程序在运行时根据对象的实际类型来确定调用哪个方法，而不是在编译时确定。这意味着方法的具体实现取决于对象的实际类型，而非其声明类型。父类引用可以指向不同的子类对象，使得相同方法调用产生不同的行为结果。通过运行时确定具体执行的方法，代码具有更好的扩展性和可维护性。

# 多态的实现方式

## 编译时多态，方法重载（Overloading）：

重载指在同一个类中可以有多个方法，这些方法名称相同但参数列表不同（参数数量或类型不同）。

编译器在编译阶段就能确定具体的方法。以下是一个重载示例，展示了多个同名方法，但参数个数或类型不同。重载的好处是简化接口设计，不需要为不同类型编写多个方法名。

```java
// OverloadExample.java  全部源码见文档链接
/**
 * 重载示例，同名方法，参数个数或类型不同。
 * 编译器在编译时确定具体的调用方法。
 */
class Calculator {
    public int add(int num1, int num2) {
        return num1 + num2;
    }

    public int add(int... nums) {
        int sum = 0;
        for (int num : nums) {
            sum += num;
        }
        return sum;
    }
}
```

## 运行时多态，方法重写（Override）与转型（Casting）：

运行时多态是在程序运行时确定实际要执行的方法。

当子类继承父类并覆盖同名方法时，这称为重写。使用父类引用来声明子类对象时，子类会向上转型为父类类型。调用该对象的方法时，实际执行的是子类的方法，而不是父类的方法。

向上转型是指使用父类引用声明子类对象，使子类对象的实际类型变为父类。通过父类引用调用子类的方法，使代码更加通用，处理一组相关对象时无需知道它们的具体类型。

向下转型则是将父类引用转换为子类引用，这需要显式进行，并且在转换前需要使用 instanceof 关键字进行类型检查。

```java
// OverrideExample.java 全部源码见文档链接
/**
 * 重写示例，子类覆盖父类同名方法，体现多态。
 * 子类向上转型为父类型，父类强制向下转型为子类型。
 */
class Shape {
  void draw() {
    System.out.println("Shape->draw");
  }

  void drawShape() {
    System.out.println("Shape->drawShape");
  }
}

class Circle extends Shape {
  @Override
  void draw() {
    System.out.println("Circle->draw");
  }

  void drawCircle() {
    System.out.println("Circle->drawCircle");
  }
}

class Square extends Shape {
  @Override
  void draw() {
    System.out.println("Square->draw");
  }

  void drawSquare() {
    System.out.println("Square->drawSquare");
  }
}

public class OverrideExample {
  public static void main(String[] args) {
    // 用父类引用声明子类对象，向上转型
    Shape shape1 = new Circle();
    Shape shape2 = new Square();

    // 子类有同名方法，动态绑定到子类，实质执行的是Circle.draw()，体现多态
    shape1.draw();

    // 报错，编译时按声明类型检查，Shape类中没有drawCircle方法
    // shape1.drawCircle();

    // 执行父类方法，输出 "Shape->drawShape"
    shape1.drawShape();

    if (shape2 instanceof Square) {
      // 向下转型，用子类重新声明，成为子类型了
      Square mySquare = (Square) shape2;

      // 输出 "Square->draw"
      mySquare.draw();

      // 输出 "Square->drawSquare"
      mySquare.drawSquare();

      // 报错。若强转为父类型，则无法调用drawSquare方法
      // ((Shape) mySquare).drawSquare();

      // 继承父类，输出 "Shape->drawShape"
      mySquare.drawShape();
    }
  }
}

```

## 多态三个必要条件：
严格来说，多态需要具备以下三个条件。

1. 继承：子类继承父类或实现接口。
2. 重写：子类覆盖父类的方法。
3. 父类声明子类：使用父类引用来声明子类对象。

重载不属于严格意义上的多态，因为重载在编译阶段就确定了。以下主要探讨运行时的多态，即针对某个类型的方法调用，实际执行的方法取决于运行时的对象，而不是声明时的类型。

```java
// 父类
class Animal {
    void makeSound() {
        System.out.println("Animal makes a sound");
    }
}

// 子类继承并重写同名方法
class Dog extends Animal {
    @Override
    void makeSound() {
        System.out.println("Dog barks");
    }
}

public class Test {
    public static void main(String[] args) {
         // 父类引用声明子类
        Animal myAnimal = new Dog();
         // 运行时子类覆盖了父类方法，故输出"Dog barks"
        myAnimal.makeSound();
    }
}
```

**如何理解父类声明子类 `Parent child = new Child();` ？**

- 解释：用 Parent 类声明了一个 child 引用变量（变量存于栈中），并赋值为 Child 实例对象（对象存于堆中）。变量 child 的类型为 Parent（向上转型），它的值是一个 Child 类型的实例对象。

- 加载执行顺序：
**编译时**：JVM 编译时检查类的关系和对应方法（包括重载），确定变量的类型并定位相关方法名称，生成字节码。
**运行时**：
    1. JVM 加载 Parent 和 Child 类。
    2. 根据 Parent 和 Child 的大小分配堆内存。
    3. 初始化 new Child() 并返回对象引用。
    4. 分配栈内存给变量 child。
    5. 将对象引用赋值给 child。

- 总结：
编译时根据引用类型（不是实例对象）确定方法的名称和参数（包括重载）。
运行时如果子类覆盖了父类的方法，则调用子类（实例引用类型）的方法；如果没有覆盖，则执行父类（变量引用类型）的方法。


## 多态的好处，为什么要用多态？

在面向对象设计中，“开闭原则”是非常重要的一条。即系统中的类应该对扩展开放，而对修改关闭。这样的代码更可维护和可扩展，同时更加简洁与清晰。

延续上面的例子，假设业务需要扩充更多子类，我们可以通过以下步骤来体现开闭原则：

1. 新增子类：根据业务需求，新增符合现有类层次结构的子类，例如增加AnotherChild。

2. 继承和重写：新的子类应该继承自适当的父类，并根据需要重写父类的方法或添加新的方法。

3. 不需要修改现有的代码：遵循开闭原则，我们不修改现有的 Parent 和 Child 类的代码。

4. 使用多态：通过父类引用来声明子类，例如 Parent child = new AnotherChild();，这样代码中现有的逻辑不需要改变。

5. 编译时不变性：编译时确定方法调用的特性不改变，仍然根据引用类型来确定方法的名称和参数，子类随意增加，只要覆盖父类同名方法即可。

6. 运行时多态性：运行时根据实际对象的类型来决定要执行的方法，这使得代码具有良好的可扩展性和可维护性。

```java
// 定义一个通用Animal类
class Animal {
    void makeSound() {
        System.out.println("Animal makes a sound");
    }
}

// 定义Dog类，它是动物的子类
class Dog extends Animal {
    @Override
    void makeSound() {
        System.out.println("Dog barks");
    }
}

// 定义Cat类，它是动物的子类
class Cat extends Animal {
    @Override
    void makeSound() {
        System.out.println("Cat meows");
    }

    // Cat自有方法
    void meow() {
        System.out.println("Cat is meowing...");
    }
}

// 定义一个动物园类，管理不同的动物
class Zoo {
    // 传入的是抽象父类或接口，方便扩展
    void letAnimalMakeSound(Animal animal) {
        animal.makeSound();
    }
}

public class AnimalExample {
    public static void main(String[] args) {
        Zoo zoo = new Zoo();

        Animal myDog = new Dog(); // 向上转型
        Animal myCat = new Cat(); // 向上转型
        ((Cat)myCat).meow(); // 向下强转，打印自有方法

        // 通过多态性，动物园可以使用相同的方法处理不同种类的动物
        zoo.letAnimalMakeSound(myDog); // 输出 "Dog barks"
        zoo.letAnimalMakeSound(myCat); // 输出 "Cat meows"
    }
}
```

要增加新的动物（如鸟类，Bird），只需扩展 Animal 类，而无需修改现有 Zoo 类中的方法。

```java
class Bird extends Animal {
    @Override
    void makeSound() {
        System.out.println("Bird chirps");
    }
}

public class AnimalExample {
    public static void main(String[] args) {
        Zoo zoo = new Zoo();

        Animal myDog = new Dog(); // 向上转型
        Animal myCat = new Cat(); // 向上转型
        Animal myBird = new Bird(); // 向上转型

        // 通过多态性，动物园可以使用相同的方法处理不同种类的动物
        zoo.letAnimalMakeSound(myDog); // 输出 "Dog barks"
        zoo.letAnimalMakeSound(myCat); // 输出 "Cat meows"
        zoo.letAnimalMakeSound(myBird); // 输出 "Bird chirps"
    }
}
```

这种设计的好处：
- 允许新增 Animal 的子类，保持对扩展开放；
- 无需修改依赖 Zoo 的 letAnimalMakeSound 方法，实现对修改封闭。

我们的业务总在不停变化，如何使得代码底层不用大改，而表层又能跟随业务不停变动，这就显得十分重要。通过这种方式，我们在不修改现有代码的情况下，可以轻松地引入新的子类并扩展系统功能，而又不影响系统的稳定性和可靠性。

# 其他语言如何实现多态？

不同语言因为语言特性的不同，在实现多态上也有不同。Go语言有接口，有struct，但没有继承和方法重载，实现多态与Java有所不同。Python和JavaScript作为动态语言，没有接口和显式类型声明，在实现多态上跟Java有区别。C语言没有class和接口，通过struct和函数指针来模拟多态。C++有class，在多态上跟Java有点像，但其支持多重继承，且显示声明为virtual的方法才支持动态绑定，其核心机制上与Java也有所不同。

虽然各语言实现多态各不相同，但总的概念是一致的，即让代码更加灵活多变，满足“开闭原则”的设计目标。

## Go语言例子

在Go语言中，虽然没有传统意义上的类继承、父类声明子类和方法重载，但通过结构体（struct）和接口（interface）以及匿名组合等方式实现类似的功能。这样也能实现代码的组织和复用，同时保持了灵活性和简洁性。

```go
package main

import (
  "fmt"
)

// 定义一个Animal接口
type Animal interface {
  MakeSound()
}

// 定义一个 Dog 类型
type Dog struct{}

// 实现 Animal 接口的 MakeSound 方法
func (d Dog) MakeSound() {
  fmt.Println("Dog barks")
}

// 定义一个 Cat 类型
type Cat struct{}

// 实现 Animal 接口的 MakeSound 方法
func (c Cat) MakeSound() {
  fmt.Println("Cat meows")
}

// Cat自有方法
func (c *Cat) Meow() {
  fmt.Println("Cat is meowing...")
}

// 定义一个 Zoo 类型，用于管理动物
type Zoo struct{}

// 定义一个方法，让动物发出声音
func (z Zoo) LetAnimalMakeSound(a Animal) {
  a.MakeSound()
}

func main() {
  zoo := Zoo{}
  myDog := Dog{}
  // 接口断言。用接口来声明结构体，类似父类声明子类
  var myCat Animal = &Cat{}
  // 类型断言。将接口转为子类类型，打印自有方法
  (myCat.(*Cat)).Meow()

  // 使用多态性，通过接口类型处理不同的具体类型
  zoo.LetAnimalMakeSound(myDog) // 输出 "Dog barks"
  zoo.LetAnimalMakeSound(myCat) // 输出 "Cat meows"
}
```

当需要增加Bird类型时，直接增加即可。同样无需修改Zoo类里面的LetAnimalMakeSound方法。

```go
type Bird struct{}

// 实现 Animal 接口的 MakeSound 方法
func (b Bird) MakeSound() {
    fmt.Println("Bird chirps")
}

func main() {
  zoo := Zoo{}
  myDog := Dog{}
  var myCat Animal = &Cat{}
  (myCat.(*Cat)).Meow()
  myBird := Bird{}

  // 使用多态性，通过接口类型处理不同的具体类型
  zoo.LetAnimalMakeSound(myDog)  // 输出 "Dog barks"
  zoo.LetAnimalMakeSound(myCat)  // 输出 "Cat meows"
  zoo.LetAnimalMakeSound(myBird) // 输出 "Bird chirps"
}
```

严格的多态概念，包括子类继承父类、方法重写以及父类声明子类等，这些特性在Go语言中无法实现。Go语言没有class概念，它的struct虽然包含方法，看起来像class，但实际上没有继承和重载的支持，它们本质上仍是结构体。

Go语言摒弃了传统面向对象语言中的class和继承概念，我们需要用新的视角来理解什么是多态。

## JavaScript语言例子

JavaScript是一种动态弱类型的基于对象的语言，其一切皆是对象。它通过对象的原型链来实现面向对象编程。尽管JavaScript具有class和继承的能力，但由于缺少强类型系统，因此无法实现传统意义上的多态。

当然，JavaScript作为动态语言，具有天然的动态性优势。这使得它在灵活性和扩展性方面更具优势。

```js
// 定义一个通用Animal类
class Animal {
    makeSound() {
        console.log("Animal makes a sound");
    }
}

// 定义Dog类，它是动物的子类
class Dog extends Animal {
    makeSound() {
        console.log("Dog barks");
    }
}

// 定义Cat类，它是动物的子类
class Cat extends Animal {
    makeSound() {
        console.log("Cat meows");
    }
    // Cat自有函数
    meow() {
        console.log("Cat is meowing...", this);
    }
}

// 定义一个动物园类，管理不同的动物
class Zoo {
    // JS没有严格类型，出原始数据类型外，其他均是Object
    // 说出传入的对象只要有makeSound方法即可。
    letAnimalMakeSound(animal) {
        animal.makeSound();
    }
}

// 测试代码
const zoo = new Zoo();
// JS没有父类定义子类概念，直接声明即可，无需向上转型
// 通过instanceof类型判断时可得到子类和父类类型
const myDog = new Dog();
const myCat = new Cat();

// 直接调用自有函数
myCat.meow();

// 可以动态给对象设置函数并绑定对象 
myDog.meow = myCat.meow.bind(myDog); 
myDog.meow();

// 动物园可以使用相同的方法处理不同种类的动物
// 当需要增加其他动物时，直接建立新的类继承Animal，而无需修改Zoo。
zoo.letAnimalMakeSound(myDog); // 输出 "Dog barks"
zoo.letAnimalMakeSound(myCat); // 输出 "Cat meows"

```

可以看出JS要实现Java意义的多态是做不到的，但JavaScript更加灵活方便，声明对象无需类型，还可以动态添加函数和绑定对象。

## Python语言例子
```py
# 定义一个通用Animal类  
class Animal:  
    def make_sound(self):  
        print("Animal makes a sound")  
  
# 定义Dog类，继承Animal
class Dog(Animal):  
    name = "Dog"
    def make_sound(self):  
        print("Dog barks")  
  
# 定义Cat类，继承Animal
class Cat(Animal):  
    name = "Cat"
    def make_sound(self):  
        print("Cat meows")  
  
    # Cat自有方法  
    def meow(self):  
        print(self.name + " is meowing...")  
  
# 定义Bird类，它是动物的子类  
class Bird(Animal):  
    def make_sound(self):  
        print("Bird chirps")  

# 定义管理类
class Zoo:  
    # python与js一样为动态语言，使用duck typing，不需要显式声明接口
    def let_animal_make_sound(self, animal):  
        animal.make_sound()  
  
# 测试代码
if __name__ == "__main__":
    zoo = Zoo()

    # 直接创建实例，Python中不需要向上转型
    my_dog = Dog()
    my_cat = Cat()
    my_bird = Bird()

    # 直接调用自有方法
    my_cat.meow()

    # Python中可直接给对象设置方法，self不会改变
    my_dog.meow = my_cat.meow
    my_dog.meow()

    # 动物园可以使用相同的方法处理不同种类的动物
    zoo.let_animal_make_sound(my_dog)  # 输出 "Dog barks"
    zoo.let_animal_make_sound(my_cat)  # 输出 "Cat meows"
    zoo.let_animal_make_sound(my_bird)  # 输出 "Bird chirps"
```
Python是一种动态语言，它使用 self 参数来引用实例，无需像其他语言那样使用 new 关键字来实例化对象。Python没有严格的接口概念，不需要像其他语言那样显示声明对象的接口。Python通过继承和方法重写来实现多态概念，但不支持传统意义上的父类声明子类和方法重载。

因此，Python在多态性上的表现与JavaScript相似，都是基于动态语言特性，灵活而动态，通过继承和重写实现对象行为的多样性。

## Java多态实例详解

以下是一个简单而详尽的例子，帮助你全面理解Java中多态的工作机制。

```java
// PolymorphismSimple.java
// 父类A
class A {
    public String show(D object) {
        return ("A and D");
    }

    public String show(A object) {
        return ("A and A");
    }

    // 默认注释掉。可开关注释测试下
    // public String show(B object) {
    // return ("A and B");
    // }
}

// 子类B
class B extends A {
    public String show(B object) {
        return ("B and B");
    }

    public String show(A object) {
        return ("B and A");
    }
}

// 孙子类C
class C extends B {
}

// 孙子类D
class D extends B {
}

// 测试验证
public class PolymorphismSimple {
    public static void main(String[] args) {
        // 父类声明自己
        A a = new A();
        // 父类声明子类
        A ab = new B();
        // 子类声明自己
        B b = new B();
        C c = new C();
        D d = new D();

        // 1) A and A。b的类型是B，也是B的实例，A里没有show(B)方法，但有show(A)方法。B的父类是A，因此定位到A.show(A)。
        System.out.println("1) " + a.show(b));

        // 2) A and A。c的类型是C，也是C的实例，C继承B，B继承A。A里没有show(C)方法，也没有show(B)方法，最后指向A.show(A)。
        System.out.println("2) " + a.show(c));

        // 3) A and D, d的类型是D，也是D的实例，D继承B，B继承A。A里有show(D)方法，直接定位到A.show(D)。
        System.out.println("3) " + a.show(d));

        // 4) B and A, ab是B的实例，但用A声明，即向上转型得到的类型是A，运行时才能确定具体该调用哪个方法。
        // ab是B的实例对象，但引用类型是A。类型是在编译时确定，因此从类型开始定位方法。
        // A类中没有show(B)方法，但有show(A)方法，因为A是B的父类，ab也是A的实例，于是定位到A.show(A)方法。
        // 由于B是A的子类，且B重写了A的show(A)，A的方法被覆盖了，于是定位到B.show(A)，这就是动态绑定。
        // 虽然B中有show(B)方法，但是因为ab的类型是A，编译时根据类型定位到A的方法，而不是B。
        // 以下几种可开关打开/注释代码测试下。
        // - 若A里有show(A)和show(B)，B里有show(B)有show(A)，则编译时关联到A.show(B)，因B覆盖了A.show(B)，动态绑定到B.show(B)。
        // - 若A里有show(A)和show(B)，B里无show(B)有show(A)，则编译时关联到A.show(B)，因B无覆盖，则直接调用A.show(B)。
        // - 若A里有show(A)无show(B)，B里无show(B)有show(A)，则编译时关联到A.show(A)，因B覆盖了A.show(A)，动态绑定到B.show(A)。
        // - 若A里有show(A)无show(B)，B里无show(A)有show(B)，则编译时关联到A.show(A)，因B无覆盖，则直接调用A.show(A)。
        // 查找顺序为：编译时根据引用类型确定所属类 -> 根据重载参数类型定位（类型按子->父->祖逐级往上查找）到类的具体方法（包括继承的方法） ->
        // 运行时实例对象覆盖（覆盖只有子->父一层）了引用类型的同名方法 -> 定位到实例对象的方法。
        System.out.println("4) " + ab.show(b));

        // 5) B and A。ab是B的实例，类型是A。从A类没找到show(C)方法，也没找到A.show(B)方法，找到A.show(A)方法。A.show(A)被B.show(A)覆盖，因此调用B.show(A)。
        System.out.println("5) " + ab.show(c));

        // 6) A and D。A里面有show(D)的方法，直接定位到。
        System.out.println("6) " + ab.show(d));

        // 7) B and B。B里面有show(B)的方法，直接定位到。
        System.out.println("7) " + b.show(b));

        // 8) B and B。B没有show(c)方法，但有show(B)方法。C继承自B，父类型是B，因此调用B.show(B)。
        System.out.println("8) " + b.show(c));

        // 9) A and D。B中没有show(D)方法，B继承A，A里有show(D), 故调用A.show(D)方法。
        System.out.println("9) " + b.show(d));

        // 10) B and A。父类声明子类，存在向上转型。A里有show(A)，被B.show(A)覆盖了，因此定位到B.show(A)。
        System.out.println("10) " + ab.show(a));

    }
}
```

# 总结

多态包括编译时多态和运行时多态。编译时多态，即静态绑定，通常通过方法重载实现。运行时多态则是在代码运行时确定具体调用的方法。

从Java的角度看，严格意义上的多态需要满足三个条件：继承、方法覆盖和父类引用子类对象。Java完全符合这些要求，实现了严格意义上的多态。

Go语言、Python和JavaScript不完全符合严格意义上的多态，但具备多态特性，能够达成动态确定实际要执行的方法，从而使代码更加灵活、易于维护和扩展。

## 各语言完整示例

[https://github.com/microwind/design-pattern/tree/main/programming-paradigm/oop/polymorphism](https://github.com/microwind/design-patterns/tree/main/programming-paradigm/object-oriented-programming/polymorphism)

[PolymorphismExample.java](./PolymorphismExample.java) 
[PolymorphismExample.go](./PolymorphismExample.go) 
[polymorphism_example.c](./polymorphism_example.c) 
[PolymorphismExample.cpp](./PolymorphismExample.cpp) 
[PolymorphismExample.js](./PolymorphismExample.js) 
[PolymorphismExample.py](./PolymorphismExample.py) 
[PolymorphismExample.ts](./PolymorphismExample.ts) 

## 简单示例

[PolymorphismSimple.java](./PolymorphismSimple.java) 
[PolymorphismSimple.go](./PolymorphismSimple.go) 
[polymorphism_simple.c](./polymorphism_simple.c) 
[PolymorphismSimple.cpp](./PolymorphismSimple.cpp) 
[PolymorphismSimple.js](./PolymorphismSimple.js) 
[PolymorphismSimple.py](./PolymorphismSimple.py) 
[PolymorphismSimple.ts](./PolymorphismSimple.ts) 
