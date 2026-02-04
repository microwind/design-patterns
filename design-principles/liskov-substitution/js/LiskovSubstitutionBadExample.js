/**
 * 里氏代换原则 - 反例
 * 这个例子违反了里氏代换原则，为了方便理解把相关类放在一起。
 * 1. 子类覆盖了父类的方法，改变了父类方法的意图。
 * 2. 因为子类改变了父类行为，如果用子类替换其父类可能会引起问题。
 * 对比：正例不覆盖父类的非抽象方法，通过新增方法扩展行为。
 */
class Shape {
  // 改变父类 draw 语义，导致替换父类后行为不一致
  draw() {
    console.log(`Drawing Shape. area:${this.area()}`);
  }

  area() {
    throw new Error('Abstract method area() must be implemented.');
  }
}

// 违规点：子类重写父类行为，破坏替换性
class Square extends Shape {
  constructor(side) {
    super();
    this.side = side;
  }

  // 违反：重写父类 draw()，引入额外的条件逻辑
  draw() {
    // 新增条件导致行为差异
    if (this.checkArea()) {
      console.log(`Drawing Square. area:${this.area()}`);
    } else {
      console.log("Don't draw square");
    }
  }

  // 该额外校验导致 draw 行为改变
  checkArea() {
    return this.area() <= 100;
  }

  area() {
    return this.side * this.side;
  }
}

class Rectangle extends Shape {
  constructor(width, height) {
    super();
    this.width = width;
    this.height = height;
  }

  area() {
    return this.width * this.height;
  }
}

// 测试：当把 Square 当作 Shape 使用时，draw 的行为发生变化
const square1 = new Square(6);
const square2 = new Square(12);
const rectangle1 = new Rectangle(8, 5);
const rectangle2 = new Rectangle(9, 6);

square1.draw();
square2.draw();
rectangle1.draw();
rectangle2.draw();

/**
jarry@jarrys-MBP js % node LiskovSubstitutionBadExample.js 
Drawing Square. area:36
Don't draw square
Drawing Shape. area:40
Drawing Shape. area:54
*/
