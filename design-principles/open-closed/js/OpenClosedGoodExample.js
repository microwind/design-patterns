/**
 * 开闭原则
 * 1. 对扩展开放（Open for Extension）：尽量通过可扩展的方式来支持功能的增加，而不需要修改原有代码。
 * 2. 对修改关闭（Closed for Modification）：代码一旦经过测试，那就尽量少修改原有逻辑。
 */


/**
 * 车辆抽象类或者是接口，用于具体对象继承或实现，便于统一约束和扩展
 */
class AbstractVehicle {
  constructor() {
      this.name = '';
      this.type = 0;
      this.weight = 0;
  }

  create() {
      throw new Error('Method create() must be implemented');
  }

  getName() {
      throw new Error('Method getName() must be implemented');
  }
}

/**
* 汽车类继承自抽象机动车类
*/
class Car extends AbstractVehicle {
  constructor(name) {
      super();
      this.name = name || 'car';
      this.type = 1;
      this.weight = 2500;
  }

  create() {
      console.log(`car has been produced: ${this.type} ${this.name} ${this.weight}`);
      return true;
  }

  getName() {
      return this.name;
  }
}

/**
* 巴士类继承自抽象机动车类
*/
class Bus extends AbstractVehicle {
  constructor(name) {
      super();
      this.name = name || 'bus';
      this.type = 2;
      this.weight = 15000;
  }

  create() {
      console.log(`bus has been produced: ${this.type} ${this.name} ${this.weight}`);
      return true;
  }

  getName() {
      return this.name;
  }
}

/**
* 车辆制造工厂，关联抽象汽车类，调用具体车辆的制造方法
* 可以参照设计模式中的抽象工厂和工厂方法来获取具体工厂
* 总之要将具体对象的方法与工厂调用逻辑区分开
*/
class VehicleFactory {
  static createVehicle(vehicle) {
      // 从工厂里调用具体对象的方法，避免通过if else 进行判断获取某个对象。
      // 当增加其他具体对象时不用修改这里的逻辑，
      // 而是通过扩展新增对象来实现。因此对修改关闭，对扩展开放。
      vehicle.create();
      // 可以返回具体对象或者调用基础方法
      return vehicle;
  }
}

/**
 * 测试符合开闭原则的例子
 */
const car2 = VehicleFactory.createVehicle(new Car("car2"));
console.log(car2.getName());

const bus2 = VehicleFactory.createVehicle(new Bus("bus2"));
console.log(bus2.getName());

/**
jarry@jarrys-MBP js % node OpenClosedGoodExample.js 
car has been produced: 1 car2 2500
car2
bus has been produced: 2 bus2 15000
bus2
*/
