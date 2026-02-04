"""
开闭原则 - 反例
这个例子违反了开闭原则，为了方便理解把相关类放在一起。
1. 车辆制造类将具体创建方法放在了一起，不利于扩展。
2. 一旦有新的车辆类型加入就需要不断修改车辆建造类。
对比：正例依赖抽象与多态，新增车型只需扩展新类，无需修改工厂。
"""

class Vehicle:
    def __init__(self, name, type_id, weight):
        self.name = name
        self.type = type_id
        self.weight = weight

    def get_name(self):
        return self.name


class Car(Vehicle):
    def __init__(self, name="car"):
        super().__init__(name, 1, 2500)


class Bus(Vehicle):
    def __init__(self, name="bus"):
        super().__init__(name, 2, 15000)


# 违规点：工厂需要认识所有具体类型
class VehicleFactory:
    # 新增车型必须修改该方法
    def create_vehicle(self, vehicle: Vehicle) -> Vehicle:
        # 分支判断导致扩展时必须改这里
        if vehicle.type == 1:
            return self.create_car(vehicle)
        if vehicle.type == 2:
            return self.create_bus(vehicle)
        return vehicle

    def create_car(self, vehicle: Vehicle) -> Vehicle:
        print(f"car has been produced: {vehicle.type} {vehicle.name} {vehicle.weight}")
        return vehicle

    def create_bus(self, vehicle: Vehicle) -> Vehicle:
        print(f"bus has been produced: {vehicle.type} {vehicle.name} {vehicle.weight}")
        return vehicle


factory = VehicleFactory()
factory.create_vehicle(Car("car1"))
factory.create_vehicle(Bus("bus1"))

"""
jarry@jarrys-MBP python % python3 open_closed_bad_example.py 
car has been produced: 1 car1 2500
bus has been produced: 2 bus1 15000
"""
