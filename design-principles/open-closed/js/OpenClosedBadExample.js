/**
 * 开闭原则 - 反例
 * 这个例子违反了开闭原则，为了方便理解把相关类放在一起。
 * 1. 车辆制造类将具体创建方法放在了一起，不利于扩展。
 * 2. 一旦有新的车辆类型加入就需要不断修改车辆建造类。
 * 对比：正例依赖抽象与多态，新增车型只需扩展新类，无需修改工厂。
 */
class Vehicle {
  constructor(name, type, weight) {
    this.name = name;
    this.type = type;
    this.weight = weight;
  }

  getName() {
    return this.name;
  }
}

class Car extends Vehicle {
  constructor(name) {
    super(name || 'car', 1, 2500);
  }
}

class Bus extends Vehicle {
  constructor(name) {
    super(name || 'bus', 2, 15000);
  }
}

// 违规点：工厂需要认识所有具体类型，新增类型需修改工厂
class VehicleFactory {
  // 新增车型必须修改该方法，违背对修改关闭
  createVehicle(vehicle) {
    // 分支判断导致扩展时必须改这里
    switch (vehicle.type) {
      case 1:
        return this.createCar(vehicle);
      case 2:
        return this.createBus(vehicle);
      default:
        return vehicle;
    }
  }

  createCar(vehicle) {
    console.log(`car has been produced: ${vehicle.type} ${vehicle.name} ${vehicle.weight}`);
    return vehicle;
  }

  createBus(vehicle) {
    console.log(`bus has been produced: ${vehicle.type} ${vehicle.name} ${vehicle.weight}`);
    return vehicle;
  }
}

const factory = new VehicleFactory();
factory.createVehicle(new Car('car1'));
factory.createVehicle(new Bus('bus1'));

/**
jarry@jarrys-MBP js % node OpenClosedBadExample.js 
car has been produced: 1 car1 2500
bus has been produced: 2 bus1 15000
*/
