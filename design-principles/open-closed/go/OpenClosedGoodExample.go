/**
 * 开闭原则
 * 1. 对扩展开放（Open for Extension）：尽量通过可扩展的方式来支持功能的增加，而不需要修改原有代码。
 * 2. 对修改关闭（Closed for Modification）：代码一旦经过测试，那就尽量少修改原有逻辑。
 */
package main

import "fmt"

/*
 * 车辆抽象类或者是接口，用于具体对象继承或实现，便于统一约束和扩展
 */
type AbstractVehicle interface {
  create() bool
  getName() string
}

/*
 * 汽车类继承自抽象机动车类
 */
type Car struct {
  name   string
  typeId int
  weight int
}

func NewCar(name string) *Car {
  return &Car{name: name, typeId: 1, weight: 2500}
}

func (c *Car) create() bool {
  fmt.Printf("car has been produced: %d %s %d\n", c.typeId, c.name, c.weight)
  return true
}

func (c *Car) getName() string {
  return c.name
}

/*
 * 巴士类继承自抽象机动车类
 */
type Bus struct {
  name   string
  typeId int
  weight int
}

func NewBus(name string) *Bus {
  return &Bus{name: name, typeId: 2, weight: 15000}
}

func (b *Bus) create() bool {
  fmt.Printf("bus has been produced: %d %s %d\n", b.typeId, b.name, b.weight)
  return true
}

func (b *Bus) getName() string {
  return b.name
}

/*
 * 车辆制造工厂，关联抽象汽车类，调用具体车辆的制造方法
 * 可以参照设计模式中的抽象工厂和工厂方法来获取具体工厂
 * 总之要将具体对象的方法与工厂调用逻辑区分开
 */
func createVehicle(vehicle AbstractVehicle) AbstractVehicle {
  // 从工厂里调用具体对象的方法，避免通过if else 进行判断获取某个对象。
  // 当增加其他具体对象时不用修改这里的逻辑，
  // 而是通过扩展新增对象来实现。因此对修改关闭，对扩展开放。
  vehicle.create()
  // 可以返回具体对象或者调用基础方法
  return vehicle
}

func main() {
  // 测试符合开闭原则的例子
  car2 := createVehicle(NewCar("car2"))
  fmt.Println(car2.getName())

  bus2 := createVehicle(NewBus("bus2")).(*Bus)
  fmt.Println(bus2.getName())
}

/**
jarry@jarrys-MBP go % go run OpenClosedGoodExample.go 
car has been produced: 1 car2 2500
car2
bus has been produced: 2 bus2 15000
bus2
*/
