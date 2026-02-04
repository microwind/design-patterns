#include <stdio.h>
/**
 * 开闭原则 - 反例
 * 这个例子违反了开闭原则，为了方便理解把相关类放在一起。
 * 1. 车辆制造类将具体创建方法放在了一起，不利于扩展。
 * 2. 一旦有新的车辆类型加入就需要不断修改车辆建造类。
 * 对比：正例依赖抽象与多态，新增车型只需扩展新类，无需修改工厂。
 */

typedef struct {
  const char *name;
  int typeId;
  int weight;
} Vehicle;

Vehicle create_car(Vehicle vehicle) {
  printf("car has been produced: %d %s %d\n", vehicle.typeId, vehicle.name, vehicle.weight);
  return vehicle;
}

Vehicle create_bus(Vehicle vehicle) {
  printf("bus has been produced: %d %s %d\n", vehicle.typeId, vehicle.name, vehicle.weight);
  return vehicle;
}

// 违规点：新增车型必须修改该函数
Vehicle create_vehicle(Vehicle vehicle) {
  // 分支判断导致对修改开放
  switch (vehicle.typeId) {
  case 1:
    return create_car(vehicle);
  case 2:
    return create_bus(vehicle);
  default:
    return vehicle;
  }
}

int main() {
  Vehicle car = {"car1", 1, 2500};
  Vehicle bus = {"bus1", 2, 15000};

  create_vehicle(car);
  create_vehicle(bus);
  return 0;
}
/**
 * 开闭原则 - 反例
 * 这个例子违反了开闭原则，为了方便理解把相关类放在一起。
 * 1. 车辆制造类将具体创建方法放在了一起，不利于扩展。
 * 2. 一旦有新的车辆类型加入就需要不断修改车辆建造类。
 * 对比：正例依赖抽象与多态，新增车型只需扩展新类，无需修改工厂。
 */
