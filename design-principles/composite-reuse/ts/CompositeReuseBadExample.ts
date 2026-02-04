/**
 * 组合复用原则 - 反例
 * 这个例子违反了组合复用原则，因为采用了继承而不是组合。
 * 1. 人的身份会有多重角色，不适合用继承表达；继承更适合物种层级。
 * 2. 采用继承不够灵活，修改很麻烦，例如一个人既是经理也是雇员。
 * 对比：正例通过组合/聚合复用角色，让对象关系更灵活可扩展。
 */
class Person {
  constructor(private name: string, private age: number) {}

  getName(): string {
    return this.name;
  }

  getAge(): number {
    return this.age;
  }
}

// 违规点：用继承表达角色，导致角色组合困难
class Employee extends Person {
  constructor(name: string, age: number, public id: number, public title: string) {
    super(name, age);
  }

  work(): boolean {
    return true;
  }
}

// 角色变化会迫使继承层级不断扩展
class Engineer extends Employee {
  work(): boolean {
    console.log(
      `Engineer is working. id = ${this.id}, title = ${this.title} name = ${this.getName()}, age = ${this.getAge()}`
    );
    return true;
  }
}

class Manager extends Employee {
  work(): boolean {
    console.log(
      `Manager is working. id = ${this.id}, title = ${this.title} name = ${this.getName()}, age = ${this.getAge()}`
    );
    return true;
  }
}

new Engineer('Tom', 25, 1001, 'senior engineer').work();
new Manager('Jerry', 45, 2002, 'advanced director').work();

/**
jarry@jarrys-MBP ts % ts-node CompositeReuseBadExample.ts 
Engineer is working. id = 1001, title = senior engineer name = Tom, age = 25
Manager is working. id = 2002, title = advanced director name = Jerry, age = 45
*/
