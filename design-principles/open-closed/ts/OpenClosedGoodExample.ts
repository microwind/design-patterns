/**
 * 开闭原则
 * 1. 对扩展开放（Open for Extension）：尽量通过可扩展的方式来支持功能的增加，而不需要修改原有代码。
 * 2. 对修改关闭（Closed for Modification）：代码一旦经过测试，那就尽量少修改原有逻辑。
 */
/**
 * 车辆抽象类或者是接口，用于具体对象继承或实现，便于统一约束和扩展
 * ts下抽象类里抽象属性不需要设定初始值
 */
abstract class AbstractVehicle {
  abstract name: string
  abstract type: number
  abstract weight: number
  abstract create(): boolean
  abstract getName(): string
}

/**
 * 汽车类继承自抽象机动车类
 */
class Car extends AbstractVehicle {
  public name: string = 'car'
  public type: number = 1
  public weight: number = 2500

  constructor(name?: string) {
    super()
    this.name = name || 'car'
  }

  create(): boolean {
    console.log(
      `car has been produced: ${this.type} ${this.name} ${this.weight}`
    )
    return true
  }

  getName(): string {
    return this.name
  }
}

/**
 * 巴士类继承自抽象机动车类
 */
class Bus extends AbstractVehicle {
  public name: string = 'bus'
  public type: number = 2
  public weight: number = 15000

  constructor(name?: string) {
    super()
    this.name = name || 'bus'
  }

  create(): boolean {
    console.log(
      `bus has been produced: ${this.type} ${this.name} ${this.weight}`
    )
    return true
  }

  getName(): string {
    return this.name
  }
}

/**
 * 车辆制造工厂，关联抽象汽车类，调用具体车辆的制造方法
 * 可以参照设计模式中的抽象工厂和工厂方法来获取具体工厂
 * 总之要将具体对象的方法与工厂调用逻辑区分开
 */
class VehicleFactory {
  static createVehicle(vehicle: AbstractVehicle): AbstractVehicle {
    // 从工厂里调用具体对象的方法，避免通过if else 进行判断获取某个对象。
    // 当增加其他具体对象时不用修改这里的逻辑，
    // 而是通过扩展新增对象来实现。因此对修改关闭，对扩展开放。
    vehicle.create()
    // 可以返回具体对象或者调用基础方法
    return vehicle
  }
}

/**
 * 测试符合开闭原则的例子
 */
const car2 = VehicleFactory.createVehicle(new Car('car2'))
console.log(car2.getName())

const bus2 = VehicleFactory.createVehicle(new Bus('bus') as AbstractVehicle)
console.log(car2.getName())

/**
jarry@jarrys-MBP ts % ts-node OpenClosedGoodExample.ts 
car has been produced: 1 car2 2500
car2
bus has been produced: 2 bus 15000
car2
*/
