/**
 * 里氏代换原则
 * 所有引用基类的地方必须能透明地使用其子类的对象，简单说即一个地方使用了父类，那么也可以使用子类。
 */
#include <stdio.h>
#include <stdbool.h>

// 抽象图形父类, 以struct替代，抽象方法供重写，非抽象方法尽量不去覆盖
typedef struct
{
  void (*draw)(void *self);
  double (*area)(void *self);
} Shape;

// Square对象类，继承父类Shape
typedef struct
{
  Shape shape;
  double side;
} Square;

// Square draw_square 方法
void Square_draw_square(Square *self)
{
  if (self->shape.area(self) <= 100)
  {
    printf("Haha Drawing Square. area: %.2f\n", self->shape.area(self));
  }
  else
  {
    printf("Don't draw square\n");
  }
}

// Square check_area 方法
bool Square_check_area(Square *self)
{
  return self->shape.area(self) <= 100;
}

// Square area 方法
double Square_area(void *self)
{
  Square *square = (Square *)self;
  return square->side * square->side;
}

// Square draw 方法
void Square_draw(void *self)
{
  Square *square = (Square *)self;
  printf("Drawing Shape. area: %.2f\n", square->shape.area(self));
}

// Rectangle 对象
typedef struct
{
  Shape shape;
  double width;
  double height;
} Rectangle;

// Rectangle area 方法
double Rectangle_area(void *self)
{
  Rectangle *rectangle = (Rectangle *)self;
  return rectangle->width * rectangle->height;
}

// Rectangle draw 方法
void Rectangle_draw(void *self)
{
  Rectangle *rectangle = (Rectangle *)self;
  printf("Drawing Shape. area: %.2f\n", rectangle->shape.area(self));
}

int main()
{
  // Square 对象
  Square square3 = {{&Square_draw, &Square_area}, 6};
  Square square4 = {{&Square_draw, &Square_area}, 12};

  // Rectangle 对象
  Rectangle rectangle3 = {{&Rectangle_draw, &Rectangle_area}, 8, 5};
  Rectangle rectangle4 = {{&Rectangle_draw, &Rectangle_area}, 9, 6};

  // Draw Square 对象
  square3.shape.draw(&square3);
  Square_draw_square(&square3);

  square4.shape.draw(&square4);
  // Square_draw_square(&square4);

  // Draw Rectangle 对象
  rectangle3.shape.draw(&rectangle3);
  rectangle4.shape.draw(&rectangle4);

  return 0;
}

/**
jarry@jarrys-MBP c % gcc liskov_substitution_good_example.c 
jarry@jarrys-MBP c % ./a.out 
Drawing Shape. area: 36.00
Haha Drawing Square. area: 36.00
Drawing Shape. area: 144.00
Drawing Shape. area: 40.00
Drawing Shape. area: 54.00
*/
