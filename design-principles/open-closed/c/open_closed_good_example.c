#include <stdio.h>
#include <stdlib.h>

/*
 * 开闭原则
 * 1. 对扩展开放（Open for Extension）：尽量通过可扩展的方式来支持功能的增加，而不需要修改原有代码。
 * 2. 对修改关闭（Closed for Modification）：代码一旦经过测试，那就尽量少修改原有逻辑。
 */

/*
 * 车辆抽象类或者是接口，用于具体对象继承或实现，便于统一约束和扩展
 */
typedef struct
{
  int (*create)();
  const char *(*getName)();
} AbstractVehicle;

/*
 * 汽车类继承自抽象机动车类
 */
typedef struct
{
  AbstractVehicle base;
  const char *name;
  int typeId;
  int weight;
} Car;

int Car_create()
{
  Car *car = (Car *)malloc(sizeof(Car));
  if (car == NULL)
  {
    return 0;
  }
  car->name = "car2";
  car->typeId = 1;
  car->weight = 2500;
  printf("car has been produced: %d %s %d\n", car->typeId, car->name, car->weight);
  return 1;
}

const char *Car_getName()
{
  return "car2";
}

/*
 * 巴士类继承自抽象机动车类
 */
typedef struct
{
  AbstractVehicle base;
  const char *name;
  int typeId;
  int weight;
} Bus;

int Bus_create()
{
  Bus *bus = (Bus *)malloc(sizeof(Bus));
  if (bus == NULL)
  {
    return 0;
  }
  bus->name = "bus2";
  bus->typeId = 2;
  bus->weight = 15000;
  printf("bus has been produced: %d %s %d\n", bus->typeId, bus->name, bus->weight);
  return 1;
}

const char *Bus_getName()
{
  return "bus2";
}

/*
 * 车辆制造工厂，关联抽象汽车类，调用具体车辆的制造方法
 * 可以参照设计模式中的抽象工厂和工厂方法来获取具体工厂
 * 总之要将具体对象的方法与工厂调用逻辑区分开
 */
AbstractVehicle createVehicle(AbstractVehicle vehicle)
{
  // 从工厂里调用具体对象的方法，避免通过if else 进行判断获取某个对象。
  // 当增加其他具体对象时不用修改这里的逻辑，
  // 而是通过扩展新增对象来实现。因此对修改关闭，对扩展开放。
  vehicle.create();
  // 可以返回具体对象或者调用基础方法
  return vehicle;
}

int main()
{
  // 测试符合开闭原则的例子
  AbstractVehicle car2 = {&Car_create, &Car_getName};
  AbstractVehicle bus2 = {&Bus_create, &Bus_getName};

  createVehicle(car2);
  printf("%s\n", car2.getName());

  createVehicle(bus2);
  printf("%s\n", bus2.getName());

  return 0;
}

/**
jarry@jarrys-MBP c % gcc open_closed_good_example.c 
jarry@jarrys-MBP c % ./a.out 
car has been produced: 1 car2 2500
car2
bus has been produced: 2 bus2 15000
bus2
*/
