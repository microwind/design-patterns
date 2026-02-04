/**
 * 里氏代换原则
 * 所有引用基类的地方必须能透明地使用其子类的对象，简单说即一个地方使用了父类，那么也可以使用子类。
 */
/**
 * 抽象图形父类，抽象方法供重写，非抽象方法尽量不去覆盖
 */
class Shape {
  draw() {
    console.log(`Drawing Shape. area:${this.area()}`);
  }

  area() {
    throw new Error('Abstract method area() must be implemented.');
  }
}

/**
 * Square对象类，继承父类Shape
 */
class Square extends Shape {
  constructor(side) {
    super();
    this.side = side;
  }

  // 不重写父类的draw方法
  // 另外起名，或通过重载得到新方法
  // drawSquare() {
  drawSquare() {
    if (this.checkArea()) {
      console.log(`Haha Drawing Square. area:${this.area()}`);
    } else {
      console.log("Don't draw square");
    }
  }

  checkArea() {
    return this.area() <= 100;
  }

  area() {
    return this.side * this.side;
  }
}

/**
 * Rectangle类，继承父类
 */
class Rectangle extends Shape {
  constructor(width, height) {
    super();
    this.width = width;
    this.height = height;
  }

  // 这里没有覆盖父类的draw()方法

  area() {
    return this.width * this.height;
  }
}

/**
 * 测试符合迪米特原则的例子
 * 删除与父类行为不一致的方法，或者通过重载得到新方法
 * 只实现父类抽象方法，不覆盖父类方法
 */
const square3 = new Square(6);
const square4 = new Square(12);
const rectangle3 = new Rectangle(8, 5);
const rectangle4 = new Rectangle(9, 6);
square3.draw();
square3.drawSquare();
// 不会出现不符合父类预期的行为
square4.draw();
rectangle3.draw();
rectangle4.draw();

/**
jarry@jarrys-MBP js % node LiskovSubstitutionGoodExample.js 
Drawing Shape. area:36
Haha Drawing Square. area:36
Drawing Shape. area:144
Drawing Shape. area:40
Drawing Shape. area:54
*/
