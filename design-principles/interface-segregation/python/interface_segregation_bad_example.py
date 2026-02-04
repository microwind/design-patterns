"""
接口隔离原则 - 反例
这个例子违反了接口隔离原则，为了方便理解把相关类放在一起。
1. 接口过于庞大，比较臃肿，职责不单一。
2. 实现对象依赖了并不需要的接口方法，在实现时浪费。
对比：正例把接口拆分为多个小接口，类只实现自己需要的方法。
"""

# 违规点：接口过大，控制类被迫实现不需要的方法
class AllDeviceController:
    def turn_on_tv(self):
        print("Turn on TV")

    def turn_off_tv(self):
        print("Turn off TV")

    def adjust_tv_volume(self, volume):
        print("Adjust TV volume to", volume)

    def change_tv_channel(self, channel):
        print("Change TV channel to", channel)

    # 对只控制电视的实现来说，这些灯光方法是冗余的
    def turn_on_light(self):
        print("Turn on Light")

    def turn_off_light(self):
        print("Turn off Light")

    def change_light_color(self, color):
        print("Change Light color to", color)


controller = AllDeviceController()
controller.turn_on_tv()
controller.adjust_tv_volume(10)
controller.change_tv_channel(2)
controller.turn_off_tv()
controller.turn_on_light()
controller.change_light_color("Red")
controller.turn_off_light()

"""
jarry@jarrys-MBP python % python3 interface_segregation_bad_example.py 
Turn on TV
Adjust TV volume to 10
Change TV channel to 2
Turn off TV
Turn on Light
Change Light color to Red
Turn off Light
"""
