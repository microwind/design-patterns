/*
多态即同一个行为具有多个不同表现形式或形态的能力。
表现形式为，子类重写父类方法，实现类实现接口方法，子类重写抽象类方法等。

多态性是指同一个方法调用可以根据对象的实际类型而有不同的行为。
当使用父类的引用变量来声明子类对象时，调用方法时将根据子类的类型而变化，这就是多态性。

多态三个必要条件：继承、重写、父类引用指向子类对象。
方法重写：重写是通过子类提供的方法来覆盖父类中的方法。子类可以提供不同的方法实现，但方法名与父类中的方法一致。
动态绑定：对于方法重写，方法的选择是在运行时（动态）绑定的而不是编译时（静态）确定的。编译器不知道对象的实际类型，只有在运行时才能够确定。 

本示例：
- 多态性的应用：
使用一个Animal类型的数组来存储不同类型的动物对象，包括Dog、Cat和Pig。
在遍历数组时，调用Animal类的eat()方法。根据多态的特性，实际上调用的是各子类的eat()方法，展示了不同对象的不同行为。

- 方法重载的应用：
在Animal类中，我们定义了两个版本的eat()方法，它们具有相同的名称但不同的参数列表。
在调用eat()方法时可以选择不同的参数，从而实现不同的行为。例如，我们可以调用animal.eat("some food")，这会调用重载形式的eat()方法。

- 方法重写的应用：
在Dog、Cat和Pig类中，我们重写了Animal类的eat()方法，以提供各自特定的行为。
这允许我们根据对象的实际类型来调用适当的方法，而不是始终调用基类的方法。例如，即使数组中存储的是Dog对象，但在遍历时实际上调用的是Dog类重写后的eat()方法。


- 好处：
  - 灵活性和可扩展性：我们可以轻松地向程序中添加新的动物类型，而不需要修改现有的代码。只需创建新的派生类并重写父类的方法即可。
  - 代码重用：通过将对象视为其超类型，我们可以在程序中重用相同的代码。例如，在遍历数组时，我们使用了相同的方式处理不同类型的动物对象。
  - 简化代码：多态性、方法重载和方法重写使得我们的代码更加简洁和清晰，因为我们不需要针对每种情况编写单独的代码，而是可以通过统一的方式处理不同的对象和行为。
*/

// 定义动物基类
class Animal {
  public String name = "Animal";

  public void eat() {
    System.out.println("Animal is eating...");
  }

  // 方法重载：同名方法，不同参数列表
  public void eat(String food) {
    System.out.println("Animal is eating " + food + "...");
  }

  public String getName() {
    return name;
  }
}

// 定义狗类，继承自动物基类
class Dog extends Animal {
  public String name = "Dog";

  // 方法重写：子类重写父类的方法
  @Override
  public void eat() {
    System.out.println("Dog is eating...");
  }

  // 方法重写：子类重写父类的方法
  @Override
  public void eat(String food) {
    System.out.println("Dog is eating " + food + "...");
  }

  // 子类自有方法
  public void bark() {
    System.out.println("Dog is barking...");
  }

  // 方法重写：子类重写父类的方法
  @Override
  public String getName() {
    return name;
  }
}

// 定义猫类，继承自动物基类
class Cat extends Animal {
  public String name = "Cat";

  // 方法重写：子类重写父类的方法
  @Override
  public void eat() {
    System.out.println("Cat is eating...");
  }

  // 方法重写：子类重写父类的方法
  @Override
  public void eat(String food) {
    System.out.println("Cat is eating " + food + "...");
  }

  // 子类自有方法
  public void meow() {
    System.out.println("Cat is meowing...");
  }

  // 方法重写：子类重写父类的方法
  @Override
  public String getName() {
    return name;
  }
}

// 定义猪类，继承自动物基类
class Pig extends Animal {
  // 方法重写：子类重写父类的方法
  @Override
  public void eat() {
    System.out.println("Pig is eating...");
  }

  // 方法重写：子类重写父类的方法
  @Override
  public void eat(String food) {
    System.out.println("Pig is eating " + food + "...");
  }

  // 子类自有方法
  public void oink() {
    System.out.println("Pig is oinking...");
  }
}

// 测试验证
public class PolymorphismExample {
  public static void main(String[] args) {
    // 创建动物数组，类型向上转型，都是Animal
    Animal[] animals = new Animal[4];
    animals[0] = new Dog(); // 第一个元素是Dog对象
    animals[1] = new Cat(); // 第二个元素是Cat对象
    animals[2] = new Pig(); // 第三个元素是Pig对象
    animals[3] = new Animal(); // 第四个元素是Animal对象

    // 遍历动物数组，调用各自的方法
    for (Animal animal : animals) {
      System.out.println("name: " + animal.getName());
      // 调用对象的eat方法
      animal.eat();
      // 调用对象的eat方法的重载形式
      animal.eat("some food");
      // 类型强制转换，从而可以调用子类的自有方法
      if (animal instanceof Dog) {
        Dog dog = (Dog) animal;
        dog.bark(); // 调用Dog类的bark方法
      } else if (animal instanceof Cat) {
        Cat cat = (Cat) animal;
        cat.meow(); // 调用Cat类的meow方法
      } else if (animal instanceof Pig) {
        Pig pig = (Pig) animal;
        pig.oink(); // 调用Pig类的oink方法
      }
    }

    /*** 分割线 ***/

    Animal dog = new Dog(); // 向上转型
    Animal cat = new Cat(); // 向上转型
    Animal pig = new Pig(); // 向上转型

    // 多态特性，在编译时按引用类型确定，在运行时根据对象的实际类型来确定，而不是引用的类型。
    dog.eat(); // 调用Dog类的eat方法
    ((Dog) dog).bark(); // 类型强制转换并调用Dog类的bark自有方法

    cat.eat(); // 调用Cat类的eat方法
    ((Cat) cat).meow(); // 类型强制转换并调用Cat类的meow自有方法

    pig.eat(); // 调用Pig类的pig方法
    ((Pig) pig).oink(); // 类型强制转换并调用Pig类的oink自有方法
  }

}

/**
 * jarry@jarrys-MBP polymorphism % javac PolymorphismExample.java
 * jarry@jarrys-MBP polymorphism % java PolymorphismExample
 * name: Dog
 * Dog is eating...
 * Dog is eating some food...
 * Dog is barking...
 * name: Cat
 * Cat is eating...
 * Cat is eating some food...
 * Cat is meowing...
 * name: Animal
 * Pig is eating...
 * Pig is eating some food...
 * Pig is oinking...
 * name: Animal
 * Animal is eating...
 * Animal is eating some food...
 * Dog is eating...
 * Dog is barking...
 * Cat is eating...
 * Cat is meowing...
 * Pig is eating...
 * Pig is oinking...
 */