package test;

import src.*;
import src.InterfaceSegregationBadExample.DeviceController;

public class Test {

  public static void start() {

    /**
     * 测试不符合接口隔离的例子
     */
    InterfaceSegregationBadExample interfaceSegregationBadExample = new InterfaceSegregationBadExample();
    InterfaceSegregationBadExample.DeviceController controller = interfaceSegregationBadExample.new AllDeviceController();

    // 控制电视
    controller.turnOnTV();
    controller.adjustTVVolume(10); // 这里的方法对于Light来说是没有意义的
    controller.changeTVChannel(2); // 这里的方法对于Light来说是没有意义的
    controller.turnOffTV();

    // 控制灯光
    controller.turnOnLight();
    controller.changeLightColor("Red"); // 这里的方法对于TV来说没有意义
    controller.turnOffLight();

    // *********************** 分割线 ******************************************/
    System.out.println("***********************");

    /**
     * 测试符合接口隔离的例子
     * 接口职责单一，方法不混在一起。
     * 每个对象有自己的接口，如果存在大量公共的接口方法，可以增加公共基础接口类。
     */

    // 控制电视
    TV tv = new TV();
    tv.turnOn();
    tv.adjustTVVolume(10); // 这里的方法仅对于TV来说
    tv.changeTVChannel(2); // 这里的方法仅对于TV来说
    tv.turnOff();

    // 控制灯光
    Light light = new Light();
    light.turnOn();
    light.changeLightColor("Red"); // 这里的方法仅对于Light来说
    light.turnOff();
  }

  public static void main(String[] args) {
    System.out.println("test start:");
    start();
  }

}

/**
 * 测试
 * jarry@jarrys-MacBook-Pro java % javac -encoding utf-8 test/Test.java
 * jarry@jarrys-MacBook-Pro java % java test/Test
 * test start:
 * Turn on TV
 * Adjust TV volume to 10
 * Change TV channel to 2
 * Turn off TV
 * Turn on Light
 * Change Light color to Red
 * Turn off Light
 ***********************
 * Turn on TV
 * Adjust TV volume to 10
 * Change TV channel to 2
 * Turn off TV
 * Turn on Light
 * Change Light color to Red
 * Turn off Light
 */