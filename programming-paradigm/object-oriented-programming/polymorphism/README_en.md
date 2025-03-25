# Understanding Polymorphism: Implementations in Java/Go/Python/JavaScript

# Concept

Object-oriented programming has three main elements: encapsulation, inheritance (or composition), and polymorphism. The first two are relatively easy to understand, but polymorphism often confuses people—they don’t know what it really does or why it’s needed. Today, we will analyze in detail what polymorphism is, its benefits, and why we use it.

Polymorphism refers to the ability of the same operation to exhibit different behaviors or results when applied to different objects. For example, when a subclass inherits from a superclass and overrides one of its methods, calling that method through a superclass reference pointing to a subclass object will execute the subclass’s method.

This behavior—where the method executed is determined by the actual type of the object rather than its declared type—is the essence of polymorphism. It is mainly achieved through inheritance and interfaces, allowing the same interface to have multiple implementations.

# Types of Polymorphism

- **Compile-time Polymorphism (Static Binding):**
    This refers to the compiler determining the appropriate class and method by checking at compile time whether a method exists for the reference type, without considering whether the actual object supports that method. Compile-time polymorphism is primarily realized through method overloading, where the method to be executed is determined during compilation based on the type, number, and order of parameters.

- **Runtime Polymorphism (Dynamic Binding):**
    This means that at runtime, the actual method to be called is determined based on the actual type of the object rather than its declared type. In other words, the specific implementation of a method depends on the object’s real type. A superclass reference can point to different subclass objects, so that the same method call produces different behaviors. Determining the method at runtime makes the code more extensible and maintainable.

# Implementations of Polymorphism

## Compile-time Polymorphism: Method Overloading

    Overloading means that a class can have multiple methods with the same name but different parameter lists (i.e., different in number or type). The compiler determines the specific method to call during compilation. The example below demonstrates overloading with methods that share the same name but differ in parameters. The benefit of overloading is that it simplifies interface design—you don’t need to create multiple method names for different parameter types.

```java
// OverloadExample.java  Full source code available via the documentation link
/**
 * Example of overloading: methods with the same name but different parameter counts or types.
 * The compiler determines the specific method to call at compile time.
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

## Runtime Polymorphism: Method Overriding and Casting

Runtime polymorphism is when the method to be executed is determined during program execution.

When a subclass inherits from a superclass and overrides a method with the same name, this is called overriding. When a subclass object is referenced by a superclass variable (upcasting), the subclass’s method is executed when that method is called, not the superclass’s method.

- **Upcasting** refers to using a superclass reference to point to a subclass object, making the object’s actual type the subclass while the reference type is the superclass. This makes the code more generic, allowing you to process a group of related objects without knowing their exact types.

- **Downcasting** is converting a superclass reference to a subclass reference. This must be done explicitly and should be preceded by a type check using the instanceof keyword.

```java
// OverrideExample.java  Full source code available via the documentation link
/**
 * Example of overriding: a subclass overrides a superclass method to demonstrate polymorphism.
 * It shows upcasting the subclass to the parent type and downcasting back to the child type.
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
    // Using superclass references to declare subclass objects (upcasting)
    Shape shape1 = new Circle();
    Shape shape2 = new Square();

    // The subclass has a method with the same name; dynamic binding calls Circle.draw(),
    // demonstrating polymorphism.
    shape1.draw();

    // Error: At compile time, the Shape class does not have a drawCircle method
    // shape1.drawCircle();

    // Executes the parent class method, outputting "Shape->drawShape"
    shape1.drawShape();

    if (shape2 instanceof Square) {
      // Downcasting: cast the superclass reference to the subclass type.
      Square mySquare = (Square) shape2;

      // Output "Square->draw"
      mySquare.draw();

      // Output "Square->drawSquare"
      mySquare.drawSquare();

      // Error: If cast to the parent type, cannot call the drawSquare method
      // ((Shape) mySquare).drawSquare();

      // Inherited parent method, outputs "Shape->drawShape"
      mySquare.drawShape();
    }
  }
}

```

## Three Necessary Conditions for Polymorphism
Strictly speaking, polymorphism requires the following three conditions:

1. **Inheritance:** The subclass inherits from the superclass or implements an interface.
2. **Overriding:** The subclass overrides the methods of the superclass.
3. **Superclass Reference to Subclass Object:** A superclass reference is used to refer to a subclass object.

Note that overloading is not considered polymorphism in the strict sense because it is determined at compile time. The focus here is on runtime polymorphism, where the method executed depends on the object at runtime rather than its declared type.

```java
// Superclass
class Animal {
    void makeSound() {
        System.out.println("Animal makes a sound");
    }
}

// Subclass inherits and overrides the method with the same name
class Dog extends Animal {
    @Override
    void makeSound() {
        System.out.println("Dog barks");
    }
}

public class Test {
    public static void main(String[] args) {
         // Declaring a subclass object using a superclass reference
        Animal myAnimal = new Dog();
         // At runtime, the subclass’s overridden method is executed, so it outputs "Dog barks"
        myAnimal.makeSound();
    }
}
```

** How to Understand the Expression `Parent child = new Child();`?**

- Explanation: 
    A variable child is declared using the Parent class (stored on the stack) and is assigned an instance of Child (stored on the heap). The variable’s declared type is Parent (upcasting), but its value is an instance of Child.

- Loading and Execution Order:
**Compile-time:**：The JVM checks the relationships between classes and their corresponding methods (including overloading), determines the variable’s type, and locates the relevant method names, generating bytecode.
**Runtime:**：
    1. The JVM loads the `Parent` and `Child` classes.
    2. Heap memory is allocated based on the sizes of `Parent` and `Child`.
    3. `new Child()` is initialized, and an object reference is returned.
    4. Stack memory is allocated for the variable `child`.
    5. The object reference is assigned to `child`.

- Summary:
At compile time, the method’s name and parameters (including overloading) are determined based on the reference type (not the actual instance). At runtime, if the subclass has overridden the superclass’s method, the subclass’s method (based on the actual object) is called; otherwise, the superclass’s method (based on the variable’s declared type) is executed.

## The Benefits of Polymorphism: Why Use It?

In `object-oriented` design, the `Open/Closed` Principle is critical. It states that classes in a system should be open for extension but closed for modification. This leads to code that is more maintainable, extensible, simple, and clear.

Continuing with the example above, suppose the business needs to add more subclasses. We can demonstrate the `Open/Closed Principle` as follows:

1. **Add New Subclasses:**
Based on business requirements, add new subclasses that conform to the existing class hierarchy—for example, adding AnotherChild.

2. **Inherit and Override:**
The new subclass should inherit from the appropriate superclass and override its methods or add new ones as needed.

3. **No Need to Modify Existing Code:**
In keeping with the `Open/Closed Principle`, we do not modify the code of the existing `Parent` and `Child` classes.

4. **Use Polymorphism:**
Declare the subclass with a superclass reference (e.g., `Parent child = new AnotherChild();`), so that the existing logic does not need to change.

5. **Compile-time Invariance:**
The property of determining method calls at compile time remains unchanged—the method’s name and parameters are determined based on the reference type. Subclasses can be added arbitrarily as long as they override the method with the same name from the superclass.

6. **Runtime Polymorphism:**
At runtime, the method executed is determined by the actual type of the object, which makes the code highly extensible and maintainable.

```java
// Define a general Animal class
class Animal {
    void makeSound() {
        System.out.println("Animal makes a sound");
    }
}

// Define Dog class, which is a subclass of Animal
class Dog extends Animal {
    @Override
    void makeSound() {
        System.out.println("Dog barks");
    }
}

// Define Cat class, which is a subclass of Animal
class Cat extends Animal {
    @Override
    void makeSound() {
        System.out.println("Cat meows");
    }

    // Cat-specific method
    void meow() {
        System.out.println("Cat is meowing...");
    }
}

// Define a Zoo class to manage different animals
class Zoo {
    // Accepts an abstract parent or interface, making it easier to extend
    void letAnimalMakeSound(Animal animal) {
        animal.makeSound();
    }
}

public class AnimalExample {
    public static void main(String[] args) {
        Zoo zoo = new Zoo();

        Animal myDog = new Dog(); // Upcasting
        Animal myCat = new Cat(); // Upcasting
        ((Cat) myCat).meow();     // Downcasting to call the specific method

        // Through polymorphism, the zoo can use the same method to handle different types of animals
        zoo.letAnimalMakeSound(myDog); // Outputs "Dog barks"
        zoo.letAnimalMakeSound(myCat); // Outputs "Cat meows"
    }
}
```

To add new animals (e.g., a `Bird`), simply extend the `Animal` class without modifying the existing methods in the `Zoo` class.

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

        Animal myDog = new Dog();  // Upcasting
        Animal myCat = new Cat();  // Upcasting
        Animal myBird = new Bird(); // Upcasting

        // Through polymorphism, the zoo can use the same method to handle different types of animals
        zoo.letAnimalMakeSound(myDog);  // Outputs "Dog barks"
        zoo.letAnimalMakeSound(myCat);  // Outputs "Cat meows"
        zoo.letAnimalMakeSound(myBird); // Outputs "Bird chirps"
    }
}
```

**Benefits of this Design:**
- It allows new subclasses of `Animal` to be added, keeping the system open for extension.
- There is no need to modify the `letAnimalMakeSound` method in the `Zoo` class, thereby adhering to the closed-for-modification principle.

Our business is constantly changing. It is crucial that the underlying code remains stable without major changes while the higher-level logic can evolve with the business. This approach allows us to easily introduce new subclasses and extend system functionality without modifying existing code, ensuring system stability and reliability.

# How Do Other Languages Implement Polymorphism?
Different languages have different characteristics, so their implementations of polymorphism vary:

**Go:**
Go has interfaces and structs but does not support inheritance or method overloading. Its approach to polymorphism is different from Java's.

**Python and JavaScript:**
As dynamic languages, they do not have interfaces or explicit type declarations. Their approach to polymorphism differs from Java's.

**C:**
C does not have classes or interfaces; it simulates polymorphism using structs and function pointers.

**C++:**
C++ has classes and its polymorphism is somewhat similar to Java's, but it supports multiple inheritance and requires methods to be explicitly declared as virtual to support dynamic binding. Its core mechanism differs from Java's.

Although the implementations vary, the fundamental concept remains the same: to make the code more flexible and adaptable, fulfilling the design goal of the Open/Closed Principle.

## Example in Go
In Go, although there is no traditional class inheritance, superclass declaration, or method overloading, similar functionality is achieved using structs, interfaces, and anonymous composition. This enables organized and reusable code while maintaining flexibility and simplicity.

```go
package main

import (
  "fmt"
)

// Define an Animal interface
type Animal interface {
  MakeSound()
}

// Define a Dog type
type Dog struct{}

// Implement the MakeSound method for Dog (satisfies the Animal interface)
func (d Dog) MakeSound() {
  fmt.Println("Dog barks")
}

// Define a Cat type
type Cat struct{}

// Implement the MakeSound method for Cat (satisfies the Animal interface)
func (c Cat) MakeSound() {
  fmt.Println("Cat meows")
}

// Cat-specific method
func (c *Cat) Meow() {
  fmt.Println("Cat is meowing...")
}

// Define a Zoo type to manage animals
type Zoo struct{}

// Define a method to make an animal produce a sound
func (z Zoo) LetAnimalMakeSound(a Animal) {
  a.MakeSound()
}

func main() {
  zoo := Zoo{}
  myDog := Dog{}
  // Interface assertion: declare a struct with an interface, similar to a superclass reference to a subclass
  var myCat Animal = &Cat{}
  // Type assertion: cast the interface to the concrete type to call its specific method
  (myCat.(*Cat)).Meow()

  // Using polymorphism, handle different concrete types via the interface
  zoo.LetAnimalMakeSound(myDog) // Outputs "Dog barks"
  zoo.LetAnimalMakeSound(myCat) // Outputs "Cat meows"
}

```
When you need to add a new type, such as `Bird`, simply add it without modifying the `LetAnimalMakeSound` method in `Zoo`.
```go
type Bird struct{}

// Implement the MakeSound method for Bird
func (b Bird) MakeSound() {
    fmt.Println("Bird chirps")
}

func main() {
  zoo := Zoo{}
  myDog := Dog{}
  var myCat Animal = &Cat{}
  (myCat.(*Cat)).Meow()
  myBird := Bird{}

  // Using polymorphism, handle different concrete types via the interface
  zoo.LetAnimalMakeSound(myDog)  // Outputs "Dog barks"
  zoo.LetAnimalMakeSound(myCat)  // Outputs "Cat meows"
  zoo.LetAnimalMakeSound(myBird) // Outputs "Bird chirps"
}
```

Note that the strict concept of polymorphism—which includes subclass inheritance, method overriding, and declaring a subclass using a superclass reference—cannot be fully implemented in Go. Go does not have classes; its structs, though they may contain methods and resemble classes, do not support inheritance or overloading.

## Example in JavaScript
JavaScript is a dynamically-typed, object-based language in which everything is an object. It uses the prototype chain for object-oriented programming. Although JavaScript supports classes and inheritance, its lack of a strong type system means it cannot fully implement traditional polymorphism.

However, as a dynamic language, JavaScript has inherent flexibility and extensibility.

```js
// Define a general Animal class
class Animal {
    makeSound() {
        console.log("Animal makes a sound");
    }
}

// Define a Dog class, which is a subclass of Animal
class Dog extends Animal {
    makeSound() {
        console.log("Dog barks");
    }
}

// Define a Cat class, which is a subclass of Animal
class Cat extends Animal {
    makeSound() {
        console.log("Cat meows");
    }
    // Cat-specific method
    meow() {
        console.log("Cat is meowing...", this);
    }
}

// Define a Zoo class to manage different animals
class Zoo {
    // JavaScript does not have strict types; aside from primitive types, everything is an Object.
    // The passed object only needs to have a makeSound method.
    letAnimalMakeSound(animal) {
        animal.makeSound();
    }
}

// Test code
const zoo = new Zoo();
// In JavaScript, there's no concept of a superclass defining a subclass; simply declare the classes without upcasting.
// Using instanceof for type checking can determine subclass and superclass relationships.
const myDog = new Dog();
const myCat = new Cat();

// Directly call the specific method
myCat.meow();

// Dynamically assign a method to an object and bind it
myDog.meow = myCat.meow.bind(myDog); 
myDog.meow();

// The zoo can use the same method to handle different types of animals.
// When adding a new animal, simply create a new class extending Animal without modifying Zoo.
zoo.letAnimalMakeSound(myDog); // Outputs "Dog barks"
zoo.letAnimalMakeSound(myCat); // Outputs "Cat meows"

```
As you can see, JavaScript cannot achieve polymorphism in the same way as Java. However, it is much more flexible—object declarations do not require types, and methods can be added and bound dynamically.

## Example in Python
```py
# Define a general Animal class  
class Animal:  
    def make_sound(self):  
        print("Animal makes a sound")  
  
# Define a Dog class that inherits from Animal
class Dog(Animal):  
    name = "Dog"
    def make_sound(self):  
        print("Dog barks")  
  
# Define a Cat class that inherits from Animal
class Cat(Animal):  
    name = "Cat"
    def make_sound(self):  
        print("Cat meows")  
  
    # Cat-specific method  
    def meow(self):  
        print(self.name + " is meowing...")  
  
# Define a Bird class, which is a subclass of Animal  
class Bird(Animal):  
    def make_sound(self):  
        print("Bird chirps")  

# Define a management class
class Zoo:  
    # Python, like JavaScript, is a dynamic language that uses duck typing,
    # so there is no need to explicitly declare interfaces.
    def let_animal_make_sound(self, animal):  
        animal.make_sound()  
  
# Test code
if __name__ == "__main__":
    zoo = Zoo()

    # Directly create instances; in Python, there is no need for upcasting.
    my_dog = Dog()
    my_cat = Cat()
    my_bird = Bird()

    # Directly call the specific method
    my_cat.meow()

    # In Python, you can directly assign a method to an object; 'self' remains unchanged.
    my_dog.meow = my_cat.meow
    my_dog.meow()

    # The zoo can use the same method to handle different types of animals
    zoo.let_animal_make_sound(my_dog)  # Outputs "Dog barks"
    zoo.let_animal_make_sound(my_cat)  # Outputs "Cat meows"
    zoo.let_animal_make_sound(my_bird)  # Outputs "Bird chirps"
```
Python is a dynamic language that uses the self parameter to refer to the instance and does not require the new keyword to instantiate objects. Python does not have a strict interface concept, so there is no need to explicitly declare an object's interface. It achieves polymorphism through inheritance and method overriding, but does not support the traditional concepts of declaring a subclass via a superclass reference or method overloading.

Therefore, Python’s approach to polymorphism is similar to that of JavaScript—it is based on the dynamic nature of the language, making it flexible and dynamic, with diverse object behaviors achieved through inheritance and overriding.

## Detailed Example of Polymorphism in Java

```java
/*
 * PolymorphismSimple.java
 * Demonstrates polymorphism in Java (method overloading and overriding).
 *
 * Class A is the superclass; class B extends A; and classes C and D extend B.
 * By observing the declared types and actual types of different objects, we can see that:
 *   - At compile-time: The overloaded method is chosen based on the static type of the reference and the method parameter types.
 *   - At runtime: The executed method is determined by the actual type of the object, specifically the overridden method.
 */

// Superclass A
class A {
    // Overloaded method: parameter of type D
    public String show(D obj) {
        return "A and D";
    }
    
    // Overloaded method: parameter of type A
    public String show(A obj) {
        return "A and A";
    }
    
    // Optional overloaded method: parameter of type B (commented out by default, can be used for experimental comparison)
//    public String show(B obj) {
//        return "A and B";
//    }
}

// Subclass B extends A
class B extends A {
    // New overloaded method: parameter of type B
    public String show(B obj) {
        return "B and B";
    }
    
    // Overriding method from the superclass: parameter of type A
    @Override
    public String show(A obj) {
        return "B and A";
    }
}

// Subclass C extends B (no new methods; inherits methods from B and A)
class C extends B {
}

// Subclass D extends B (no new methods; inherits methods from B and A)
class D extends B {
}

// Main class: used to test polymorphism and method resolution
public class PolymorphismSimple {
    public static void main(String[] args) {
        // Create different objects:
        A a = new A();       // 'a' has static type A and actual type A
        A ab = new B();      // 'ab' has static type A, but actual type B (upcasting)
        B b = new B();       // 'b' has static type B and actual type B
        C c = new C();       // 'c' is of type C
        D d = new D();       // 'd' is of type D
        
        /*
         * Test 1: a.show(b)
         *  - 'b' has a static type of B, but class A does not define a method that accepts a B type parameter,
         *    so at compile-time, it matches A.show(A) (since B is a subclass of A).
         *  - The method executed is A's show(A), outputting "A and A".
         */
        System.out.println("1) " + a.show(b));
        
        /*
         * Test 2: a.show(c)
         *  - 'c' has a static type of C, and C extends B, which in turn extends A.
         *    Class A does not have an overloaded method matching C or B type, so only show(A) can match (since C is a subclass of A).
         *  - Output: "A and A".
         */
        System.out.println("2) " + a.show(c));
        
        /*
         * Test 3: a.show(d)
         *  - 'd' has a static type of D, and class A has a specifically defined method show(D), so A.show(D) is directly called.
         *  - Output: "A and D".
         */
        System.out.println("3) " + a.show(d));
        
        /*
         * Test 4: ab.show(b) -- Key case: Analysis of upcasting call.
         *  - 'ab' has a static type of A, but its actual type is B (upcasting).
         *  - At compile-time, the method is looked up based on 'ab's static type A: class A does not have show(B), so no match is found.
         *    The parameter B is upcast to type A, matching show(A) (since B is a subclass of A).
         *  - At runtime, 'ab's actual type is B, and B has overridden show(A), so B.show(A) is called.
         *  - Output: "B and A".
         */
        System.out.println("4) " + ab.show(b));
        
        /*
         * Test 5: ab.show(c)
         *  - 'c' has a static type of C, and class A does not have an overloaded method matching C or B type,
         *    so it matches show(A).
         *  - 'ab's actual type is B, and B has overridden show(A), so B.show(A) is called.
         *  - Output: "B and A".
         */
        System.out.println("5) " + ab.show(c));
        
        /*
         * Test 6: ab.show(d)
         *  - 'd' has a static type of D, and class A has a specifically defined method show(D),
         *    so A.show(D) is directly matched at compile-time.
         *  - Since B does not override show(D), A.show(D) is called, outputting "A and D".
         */
        System.out.println("6) " + ab.show(d));
        
        /*
         * Test 7: b.show(b)
         *  - 'b' has a static type of B, and class B defines an overloaded method show(B),
         *    so B.show(B) is directly called, outputting "B and B".
         */
        System.out.println("7) " + b.show(b));
        
        /*
         * Test 8: b.show(c)
         *  - 'c' has a static type of C, and since C is a subclass of B, it matches B.show(B).
         *  - B.show(B) is called, outputting "B and B".
         */
        System.out.println("8) " + b.show(c));
        
        /*
         * Test 9: b.show(d)
         *  - 'd' has a static type of D, but class B does not define a method that accepts a D type parameter.
         *    Since B extends A and A defines show(D), A.show(D) is called.
         *  - Output: "A and D".
         */
        System.out.println("9) " + b.show(d));
        
        /*
         * Test 10: ab.show(a)
         *  - 'a' has a static type of A, matching A.show(A).
         *    However, 'ab's actual type is B, and B has overridden show(A), so B.show(A) is called.
         *  - Output: "B and A".
         */
        System.out.println("10) " + ab.show(a));
    }
}
```

# Summary
Polymorphism includes both compile-time (static binding, typically via method overloading) and runtime polymorphism (dynamic binding, via method overriding). From Java’s perspective, strict polymorphism requires three conditions: inheritance, method overriding, and using a superclass reference to refer to a subclass object. Java fully satisfies these requirements, achieving strict polymorphism.

Go, Python, and JavaScript do not fully conform to the strict definition of polymorphism but still possess polymorphic characteristics. They allow the actual method to be determined dynamically at runtime, making the code more flexible, maintainable, and extensible.

## Complete Examples for Each Language

[https://github.com/microwind/design-pattern/tree/main/programming-paradigm/oop/polymorphism](https://github.com/microwind/design-patterns/tree/main/programming-paradigm/object-oriented-programming/polymorphism)

[PolymorphismExample.java](./PolymorphismExample.java) 
[PolymorphismExample.go](./PolymorphismExample.go) 
[polymorphism_example.c](./polymorphism_example.c) 
[PolymorphismExample.cpp](./PolymorphismExample.cpp) 
[PolymorphismExample.js](./PolymorphismExample.js) 
[PolymorphismExample.py](./PolymorphismExample.py) 
[PolymorphismExample.ts](./PolymorphismExample.ts) 

## Simple Examples

[PolymorphismSimple.java](./PolymorphismSimple.java) 
[PolymorphismSimple.go](./PolymorphismSimple.go) 
[polymorphism_simple.c](./polymorphism_simple.c) 
[PolymorphismSimple.cpp](./PolymorphismSimple.cpp) 
[PolymorphismSimple.js](./PolymorphismSimple.js) 
[PolymorphismSimple.py](./PolymorphismSimple.py) 
[PolymorphismSimple.ts](./PolymorphismSimple.ts) 
