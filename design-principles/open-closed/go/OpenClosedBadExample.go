/**
 * 开闭原则 - 反例
 * 这个例子违反了开闭原则，为了方便理解把相关类放在一起。
 * 1. 车辆制造类将具体创建方法放在了一起，不利于扩展。
 * 2. 一旦有新的车辆类型加入就需要不断修改车辆建造类。
 * 对比：正例依赖抽象与多态，新增车型只需扩展新类，无需修改工厂。
 */
package main

import "fmt"

type Vehicle struct {
  name   string
  typeId int
  weight int
}

type Car struct {
  Vehicle
}

type Bus struct {
  Vehicle
}

func NewCar(name string) Car {
  if name == "" {
    name = "car"
  }
  return Car{Vehicle{name: name, typeId: 1, weight: 2500}}
}

func NewBus(name string) Bus {
  if name == "" {
    name = "bus"
  }
  return Bus{Vehicle{name: name, typeId: 2, weight: 15000}}
}

// 违规点：工厂了解所有具体类型
type VehicleFactory struct{}

// 新增车型必须修改此方法
func (f VehicleFactory) CreateVehicle(vehicle Vehicle) Vehicle {
  // 分支判断导致对修改开放
  switch vehicle.typeId {
  case 1:
    return f.createCar(vehicle)
  case 2:
    return f.createBus(vehicle)
  default:
    return vehicle
  }
}

func (f VehicleFactory) createCar(vehicle Vehicle) Vehicle {
  fmt.Printf("car has been produced: %d %s %d\n", vehicle.typeId, vehicle.name, vehicle.weight)
  return vehicle
}

func (f VehicleFactory) createBus(vehicle Vehicle) Vehicle {
  fmt.Printf("bus has been produced: %d %s %d\n", vehicle.typeId, vehicle.name, vehicle.weight)
  return vehicle
}

func main() {
  factory := VehicleFactory{}
  car := NewCar("car1")
  bus := NewBus("bus1")

  factory.CreateVehicle(car.Vehicle)
  factory.CreateVehicle(bus.Vehicle)
}

/**
jarry@jarrys-MBP go % go run OpenClosedBadExample.go 
car has been produced: 1 car1 2500
bus has been produced: 2 bus1 15000
*/
