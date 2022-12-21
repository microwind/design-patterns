package test;

import src.*;

public class Test {

  public static void start() {

    SingleObject singleObject = SingleObject.getInstance();
    singleObject.run();

    /*********************** 分割线 ******************************************/

    SingletonLazy singletonLazy = SingletonLazy.getInstance();
    singletonLazy.run();

    /*********************** 分割线 ******************************************/

    SingletonSafe singletonSafe = SingletonSafe.getInstance();
    singletonSafe.run();

    /*********************** 分割线 ******************************************/

    SingletonInner singletonInner = SingletonInner.getInstance();
    singletonInner.run();
  }

  public static void main(String[] args) {
    System.out.println("test start:");
    start();
  }

}

/**
 * 测试
 * jarry@jarrys-MacBook-Pro java % java --version
 * java 14.0.1 2020-04-14
 * Java(TM) SE Runtime Environment (build 14.0.1+7)
 * Java HotSpot(TM) 64-Bit Server VM (build 14.0.1+7, mixed mode, sharing)
 * 
 * jarry@jarrys-MacBook-Pro java % javac test/Test.java
 * jarry@jarrys-MacBook-Pro java % java test/Test
test start:
SingleObject::run()
SingletonLazy::run()
SingletonSafe::run()
SingletonInner::run()
 */