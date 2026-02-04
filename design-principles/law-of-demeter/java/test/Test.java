package test;

import src.*;

public class Test {

  public static void start() {

    /**
     * 测试不符合迪米特原则的例子
     */
    LawDemeterBadExample lawDemeterBadExample = new LawDemeterBadExample();
    LawDemeterBadExample.Customer customer1 = lawDemeterBadExample.new Customer("Jimmy");
    customer1.buy(lawDemeterBadExample.new Product("Computer", 5000L));
    customer1.buy(lawDemeterBadExample.new Product("Book", 200L));

    // *********************** 分割线 ******************************************/
    System.out.println("***********************");

    /**
     * 测试符合迪米特原则的例子
     * 对象职责单一清晰，不把很多功能堆积在一起。
     * 拆出一个购物车类，作为顾客和商品之间的中介类。
     * 对象不直接依赖朋友的朋友。
     */
    Customer customer2 = new Customer("Jimmy");
    customer2.buy(new Product("Computer", 5000L));
    customer2.buy(new Product("Book", 200L));
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
 * Computer's price exceeds range：5000.0
 * Jimmy purchased Book for 200.0
 * Total price: $200.0
 ***********************
 * Jimmy 购买了 Computer 价格为 5000.0
 * Book的价格超出范围：200.0
 */