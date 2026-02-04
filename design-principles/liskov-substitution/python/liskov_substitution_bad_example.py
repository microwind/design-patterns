"""
里氏代换原则 - 反例
这个例子违反了里氏代换原则，为了方便理解把相关类放在一起。
1. 子类覆盖了父类的方法，改变了父类方法的意图。
2. 因为子类改变了父类行为，如果用子类替换其父类可能会引起问题。
对比：正例不覆盖父类的非抽象方法，通过新增方法扩展行为。
"""
from abc import ABC, abstractmethod


class Shape(ABC):
    # 改变父类 draw 语义，导致替换父类后行为不一致
    def draw(self):
        print("Drawing Shape. area:", self.area())

    @abstractmethod
    def area(self):
        pass


# 违规点：子类重写父类行为，破坏替换性
class Square(Shape):
    def __init__(self, side):
        self.side = side

    # 违反：重写父类 draw()，引入额外条件
    def draw(self):
        # 新增条件导致行为差异
        if self.check_area():
            print("Drawing Square. area:", self.area())
        else:
            print("Don't draw square")

    # 该额外校验导致 draw 行为改变
    def check_area(self):
        return self.area() <= 100

    def area(self):
        return self.side * self.side


class Rectangle(Shape):
    def __init__(self, width, height):
        self.width = width
        self.height = height

    def area(self):
        return self.width * self.height


square1 = Square(6)
square2 = Square(12)
rectangle1 = Rectangle(8, 5)
rectangle2 = Rectangle(9, 6)

square1.draw()
square2.draw()
rectangle1.draw()
rectangle2.draw()

"""
jarry@jarrys-MBP python % python3 liskov_substitution_bad_example.py 
Drawing Square. area: 36
Don't draw square
Drawing Shape. area: 40
Drawing Shape. area: 54
"""
