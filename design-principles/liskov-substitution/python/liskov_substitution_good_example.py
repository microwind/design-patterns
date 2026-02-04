"""
 * 里氏代换原则
 * 所有引用基类的地方必须能透明地使用其子类的对象，简单说即一个地方使用了父类，那么也可以使用子类。
"""
from abc import ABC, abstractmethod


class Shape(ABC):
    def draw(self):
        print("Drawing Shape. area:", self.area())

    @abstractmethod
    def area(self):
        pass


class Square(Shape):
    def __init__(self, side):
        self.side = side

    # 不重写父类的draw方法
    # 另外起名，或通过重载得到新方法
    def draw_square(self):
        if self.check_area():
            print("Haha Drawing Square. area:", self.area())
        else:
            print("Don't draw square")

    def check_area(self):
        if self.area() > 100:
            return False
        return True

    def area(self):
        return self.side * self.side


class Rectangle(Shape):
    def __init__(self, width, height):
        self.width = width
        self.height = height

    def area(self):
        return self.width * self.height


square3 = Square(6)
square4 = Square(12)
rectangle3 = Rectangle(8, 5)
rectangle4 = Rectangle(9, 6)

square3.draw()
square3.draw_square()
square4.draw()
rectangle3.draw()
rectangle4.draw()

"""
jarry@jarrys-MBP python % python3 liskov_substitution_good_example.py 
Drawing Shape. area: 36
Haha Drawing Square. area: 36
Drawing Shape. area: 144
Drawing Shape. area: 40
Drawing Shape. area: 54
"""
