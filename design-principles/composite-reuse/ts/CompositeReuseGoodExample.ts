/**
 * 通过组合来替代继承进行组合/聚合复用，将有关联的类聚合到业务类中。
 * 组合：相对紧密，表示组成部件与物体的关系。
 * 聚合：相对松散，表示个体与整体/模块的关系。
 */

/** 对比Java和JS，ts做以下改造。
1. TypeScript中使用了class关键字来定义类。
2. 所有的属性和方法都需要指定类型（除非它们是any类型或隐式具有类型）。
3. TypeScript使用constructor来定义构造函数。
4. TypeScript使用?来标识可选参数。
5. 在TypeScript中，使用string、number等原始数据类型，而不是像Java中的String、Integer等包装类。
6. TypeScript使用模板字符串（反引号包围的字符串）来嵌入变量和表达式。
7. 在TypeScript中，通常不需要像Java那样显式地调用super()来调用父类的构造函数（除非父类构造函数有参数）。
8. 本例子中，由于Employee类的构造函数需要参数，所以我们在Engineer和Manager的构造函数中调用了super()。
*/

// Person.ts
class Person {
  name: string
  age: number
  gender?: number // 假设gender是可选的，并且不确定其确切类型，所以用number代替

  constructor(name: string, age: number) {
    this.name = name
    this.age = age
  }

  getName(): string {
    return this.name
  }

  getAge(): number {
    return this.age
  }
}

// Employee.ts
abstract class Employee {
  // 聚合人物类
  person: Person
  id: number
  title: string

  constructor(person: Person, id: number, title: string) {
    this.person = person
    this.id = id
    this.title = title
  }

  work(): boolean {
    return true
  }
}

// Engineer.ts
class Engineer extends Employee {
  constructor(id: number, title: string, person: Person) {
    super(person, id, title)
  }

  work(): boolean {
    console.log(
      `Engineer is working. id = ${this.id}, title = ${this.title}, name = ${this.person.name}, age = ${this.person.age}`
    )
    return true
  }
}

// Manager.ts
class Manager extends Employee {
  constructor(id: number, title: string, person: Person) {
    super(person, id, title)
  }

  work(): boolean {
    console.log(
      `Manager is working. id = ${this.id}, title = ${this.title}, name = ${this.person.name}, age = ${this.person.age}`
    )
    return true
  }
}

/* 测试验证 */
const engineer = new Engineer(1001, 'senior engineer', new Person('Tom', 25))
const manager = new Manager(2002, 'advanced director', new Person('Jerry', 45))
engineer.work()
manager.work()

/**
jarry@jarrys-MBP ts % ts-node CompositeReuseGoodExample.ts 
Engineer is working. id = 1001, title = senior engineer, name = Tom, age = 25
Manager is working. id = 2002, title = advanced director, name = Jerry, age = 45
*/
