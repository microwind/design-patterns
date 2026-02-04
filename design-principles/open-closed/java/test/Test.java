package test;

import src.*;

public class Test {

  public static void start() {

    /**
     * 测试不符合开闭原则的例子
     */
    OpenClosedBadExample vehicle_counter = new OpenClosedBadExample();
    OpenClosedBadExample.VehicleFactory vehicleFactory = vehicle_counter.new VehicleFactory();
    OpenClosedBadExample.Vehicle car1 = vehicleFactory.createVehicle(vehicle_counter.new Car("car1"));
    System.out.println(car1.getName());
    OpenClosedBadExample.Vehicle bus1 = vehicleFactory.createVehicle(vehicle_counter.new Bus("bus1"));
    System.out.println(bus1.getName());

    // *********************** 分割线 ******************************************/
    System.out.println("***********************");

    /**
     * 测试符合开闭原则的例子
     */
    AbstractVehicle car2 = VehicleFactory.createVehicle(new Car("car2"));
    System.out.println(car2.getName());

    Bus bus2 = (Bus) VehicleFactory.createVehicle(new Bus("bus2"));
    System.out.println(bus2.getName());
  }

  public static void main(String[] args) {
    System.out.println("test start:");
    start();
  }

}

/**
 * 测试验证
 * jarry@jarrys-MacBook-Pro java % javac -encoding utf-8 test/Test.java
 * jarry@jarrys-MacBook-Pro java % java test/Test
 * test start:
 * car has been produced: 1 car1 2500
 * car1
 * bus has been produced: 2 bus1 15000
 * bus1
 ***********************
 * car has been produced: 1 car2 2500
 * car2
 * bus has been produced: 2 bus2 15000
 * bus2
 */