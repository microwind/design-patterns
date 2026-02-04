package test;

import src.*;

public class Test {

  public static void start() {

    /**
     * 测试不符合里氏替换原则的例子
     */
    LiskovSubstitutionBadExample liskovSubstitutionBadExample = new LiskovSubstitutionBadExample();
    LiskovSubstitutionBadExample.Square square1 = liskovSubstitutionBadExample.new Square(6);
    LiskovSubstitutionBadExample.Shape square2 = liskovSubstitutionBadExample.new Square(12);
    LiskovSubstitutionBadExample.Rectangle rectangle1 = liskovSubstitutionBadExample.new Rectangle(8, 5);
    LiskovSubstitutionBadExample.Shape rectangle2 = liskovSubstitutionBadExample.new Rectangle(9, 6);
    square1.draw();
    // 会出现不符合父类预期的行为
    square2.draw();

    rectangle1.draw();
    rectangle2.draw();

    // *********************** 分割线 ******************************************/
    System.out.println("***********************");

    /**
     * 测试符合里氏替换原则的例子
     * 删除与父类行为不一致的方法，或者通过重载得到新方法
     * 只实现父类抽象方法，不覆盖父类方法
     */
    Square square3 = new Square(6);
    Shape square4 = new Square(12);
    Rectangle rectangle3 = new Rectangle(8, 5);
    Shape rectangle4 = new Rectangle(9, 6);
    square3.draw();
    square3.drawSquare();
    // 不会出现不符合父类预期的行为
    square4.draw();

    rectangle3.draw();
    rectangle4.draw();
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
 * Drawing Square. area:36.0
 * Don't draw square
 * Drawing Shape. area:40.0
 * Drawing Shape. area:54.0
 ***********************
 * Drawing Shape. area:36.0
 * Haha Drawing Square. area:36.0
 * Drawing Shape. area:144.0
 * Drawing Shape. area:40.0
 * Drawing Shape. area:54.0
 */