"""
组合复用原则 - 反例
这个例子违反了组合复用原则，因为采用了继承而不是组合。
1. 人的身份会有多重角色，不适合用继承表达；继承更适合物种层级。
2. 采用继承不够灵活，修改很麻烦，例如一个人既是经理也是雇员。
对比：正例通过组合/聚合复用角色，让对象关系更灵活可扩展。
"""

class Person:
    def __init__(self, name, age):
        self.name = name
        self.age = age

    def get_name(self):
        return self.name

    def get_age(self):
        return self.age


# 违规点：用继承表达角色，导致角色组合困难
class Employee(Person):
    def __init__(self, name, age, emp_id, title):
        super().__init__(name, age)
        self.id = emp_id
        self.title = title

    def work(self):
        return True


# 角色变化会迫使继承层级不断扩展
class Engineer(Employee):
    def work(self):
        print(
            f"Engineer is working. id = {self.id}, title = {self.title} name = {self.get_name()}, age = {self.get_age()}"
        )
        return True


class Manager(Employee):
    def work(self):
        print(
            f"Manager is working. id = {self.id}, title = {self.title} name = {self.get_name()}, age = {self.get_age()}"
        )
        return True


Engineer("Tom", 25, 1001, "senior engineer").work()
Manager("Jerry", 45, 2002, "advanced director").work()

"""
jarry@jarrys-MBP python % python3 composite_reuse_bad_example.py 
Engineer is working. id = 1001, title = senior engineer name = Tom, age = 25
Manager is working. id = 2002, title = advanced director name = Jerry, age = 45
"""
