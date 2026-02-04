# -*- coding: utf-8 -*-
"""
 * 接口隔离原则：
 * 1. 接口建立在最小化抽象上，接口功能单一明确，按照单一职责来建立接口，而不要将接口设计得过于复杂。
 * 2. 客户端不依赖它不需要的接口方法，只依赖它必须的接口方法，也就是最小依赖原则，以此保证代码结构清晰与纯洁性。
"""

"""
电器设备的基础控制接口，约定基本的一些控制方法【可选】
"""


class DeviceController:
    def turnOn(self):
        pass

    def turnOff(self):
        pass


"""
Light控制接口，包括TV需要的控制方法，供Light具体对象实现
每个具体控制对象有其自身的接口，不跟其他对象混在一起
"""


class LightController(DeviceController):
    def changeLightColor(self, color):
        pass


"""
TV控制接口，包括TV需要的控制方法，供TV具体对象实现
每个具体控制对象有其自身的接口，不跟其他对象混在一起
"""


class TVController(DeviceController):
    def adjustTVVolume(self, volume):
        pass

    def changeTVChannel(self, channel):
        pass


"""
Light实现类，实现Light控制接口的全部方法
"""


class Light(LightController):
    def turnOn(self):
        # 执行灯光打开操作
        print("Turn on Light")

    def turnOff(self):
        # 执行灯光关闭操作
        print("Turn off Light")

    def changeLightColor(self, color):
        # 执行灯光颜色切换
        print("Change Light color to", color)


"""
TV实现类，实现TV控制接口的全部方法
"""


class TV(TVController):
    def turnOn(self):
        print("Turn on TV")
        # 执行电视打开操作

    def turnOff(self):
        print("Turn off TV")
        # 执行电视关闭操作

    def adjustTVVolume(self, volume):
        # 执行调节音量操作
        print("Adjust TV volume to", volume)

    def changeTVChannel(self, channel):
        # 执行频道切换操作
        print("Change TV channel to", channel)

# 测试验证


# 创建TV对象
tv = TV()
tv.turnOn()
tv.adjustTVVolume(10)  # 这里的方法仅对于TV来说
tv.changeTVChannel(2)  # 这里的方法仅对于TV来说
tv.turnOff()

# 控制灯光
light = Light()
light.turnOn()
light.changeLightColor("Red")  # 这里的方法仅对于Light来说
light.turnOff()

"""
jarry@jarrys-MBP python % python3 interface_segregation_good_example.py 
Turn on TV
Adjust TV volume to 10
Change TV channel to 2
Turn off TV
Turn on Light
Change Light color to Red
Turn off Light
"""
