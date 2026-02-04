/**
 * 通过组合来替代继承进行组合/聚合复用，将有关联的类聚合到业务类中。
 * 组合：相对紧密，表示组成部件与物体的关系。
 * 聚合：相对松散，表示个体与整体/模块的关系。
 */
class Person {
  constructor(name, age) {
    this.name = name;
    this.age = age;
  }

  getName() {
    return this.name;
  }

  getAge() {
    return this.age;
  }
}

/**
 * 雇员抽象类，供其他角色继承。
 * 通过聚合人物类实现，以实现更加松散的耦合，便于扩展。
 */
class Employee {
  constructor(id, title, person) {
    this.id = id;
    this.title = title;
    this.person = person;
  }

  work() {
    return true;
  }
}

/**
 * 工程师类继承雇员类。
 */
class Engineer extends Employee {
  constructor(id, title, person) {
    super(id, title, person);
  }

  work() {
    console.log("Engineer is working. id = " + this.id +
      ", title = " + this.title + ", name = " + this.person.getName() + ", age = " + this.person.getAge());
    return true;
  }
}

/**
 * 管理者类继承雇员类。
 */
class Manager extends Employee {
  constructor(id, title, person) {
    super(id, title, person);
  }

  work() {
    console.log("Manager is working. id = " + this.id +
      ", title = " + this.title + ", name = " + this.person.getName() + ", age = " + this.person.getAge());
    return true;
  }
}

/* 测试验证 */
const engineer = new Engineer(1001, "senior engineer", new Person("Tom", 25));
engineer.work();
const manager = new Manager(2002, "advanced director", new Person("Jerry", 45));
manager.work();

/**
jarry@jarrys-MBP js % node CompositeReuseGoodExample.js 
Engineer is working. id = 1001, title = senior engineer, name = Tom, age = 25
Manager is working. id = 2002, title = advanced director, name = Jerry, age = 45
*/
