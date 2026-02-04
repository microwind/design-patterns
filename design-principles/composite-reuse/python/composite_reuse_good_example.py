# -*- coding: utf-8 -*-
"""
@author: jarry
"""
'''
 * 通过组合来替代继承进行组合/聚合复用，将有关联的类聚合到业务类中。
 * 组合，相对紧密，表示为组成部件与物体的关系
 * 聚合，相对松散，表示为个体与整体/模块的关系
'''


class Person:
    def __init__(self, name, age):
        self.name = name
        self.age = age

    def get_name(self):
        return self.name

    def get_age(self):
        return self.age


class Employee:
    def __init__(self, id, title, person):
        self.id = id
        self.title = title
        self.person = person

    def work(self):
        return True


class Engineer(Employee):
    def __init__(self, id, title, person):
        super().__init__(id, title, person)

    def work(self):
        print(
            f"Engineer is working. id = {self.id}, title = {self.title}, name = {self.person.get_name()}, age = {self.person.get_age()}")
        return True


class Manager(Employee):
    def __init__(self, id, title, person):
        super().__init__(id, title, person)

    def work(self):
        print(
            f"Manager is working. id = {self.id}, title = {self.title}, name = {self.person.get_name()}, age = {self.person.get_age()}")
        return True


# 测试验证
person = Person("Tom", 25)
engineer = Engineer(1001, "senior enginee", person)
engineer.work()

manager = Manager(2002, "advanced director", Person("Jerry", 45))
manager.work()

"""
jarry@jarrys-MBP python % python3 composite_reuse_good_example.py 
Engineer is working. id = 1001, title = senior enginee, name = Tom, age = 25
Manager is working. id = 2002, title = advanced director, name = Jerry, age = 45
"""
