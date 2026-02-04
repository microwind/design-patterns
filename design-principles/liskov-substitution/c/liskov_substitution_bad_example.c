#include <stdio.h>
/**
 * 里氏代换原则 - 反例
 * 这个例子违反了里氏代换原则，为了方便理解把相关类放在一起。
 * 1. 子类覆盖了父类的方法，改变了父类方法的意图。
 * 2. 因为子类改变了父类行为，如果用子类替换其父类可能会引起问题。
 * 对比：正例不覆盖父类的非抽象方法，通过新增方法扩展行为。
 */

typedef struct Shape Shape;

struct Shape {
  void (*draw)(Shape *self);
  double (*area)(Shape *self);
};

// 违规点：子类行为改变父类预期
typedef struct {
  Shape base;
  double side;
} Square;

double square_area(Shape *self) {
  Square *square = (Square *)self;
  return square->side * square->side;
}

// 该额外校验导致 draw 行为改变
int square_check_area(Shape *self) {
  return square_area(self) <= 100;
}

// 改变父类 draw 语义，导致替换父类后行为不一致
void square_draw(Shape *self) {
  // 新增条件导致行为差异
  if (square_check_area(self)) {
    printf("Drawing Square. area:%.0f\n", square_area(self));
  } else {
    printf("Don't draw square\n");
  }
}

typedef struct {
  Shape base;
  double width;
  double height;
} Rectangle;

double rectangle_area(Shape *self) {
  Rectangle *rect = (Rectangle *)self;
  return rect->width * rect->height;
}

void rectangle_draw(Shape *self) {
  printf("Drawing Shape. area:%.0f\n", rectangle_area(self));
}

int main() {
  Square square1 = {{square_draw, square_area}, 6};
  Square square2 = {{square_draw, square_area}, 12};
  Rectangle rect1 = {{rectangle_draw, rectangle_area}, 8, 5};
  Rectangle rect2 = {{rectangle_draw, rectangle_area}, 9, 6};

  square1.base.draw((Shape *)&square1);
  square2.base.draw((Shape *)&square2);
  rect1.base.draw((Shape *)&rect1);
  rect2.base.draw((Shape *)&rect2);
  return 0;
}
/**
 * 里氏代换原则 - 反例
 * 这个例子违反了里氏代换原则，为了方便理解把相关类放在一起。
 * 1. 子类覆盖了父类的方法，改变了父类方法的意图。
 * 2. 因为子类改变了父类行为，如果用子类替换其父类可能会引起问题。
 * 对比：正例不覆盖父类的非抽象方法，通过新增方法扩展行为。
 */
