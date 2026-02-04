# -*- coding: utf-8 -*-
"""
@author: jarry
"""
'''
 * 依赖倒置。
 * 1. 高层次模块不依赖于低层的实现，而是依赖于低层的抽象。
 * 2. 接口和抽象类不应该依赖于具体实现类，而是具体实现类依赖接口或抽象类。
'''

"""
消息通知业务应用类，依赖低层模块的接口，而不是实现
"""


class Notification:
    def __init__(self, sender):
        self.sender = sender

    # 这里业务类依赖了发送者抽象接口，而不是具体对象
    # 这样即使增加再多Sender也不用额外修改
    def send(self, content):
        print("send content by Notification.")
        self.sender.send(content)

    def set_sender(self, sender):
        self.sender = sender


"""
发送者的接口或抽象类，供实际发送者对象实现
"""


class Sender:
    def send(self, content):
        pass


"""
MessageSender实现了Sender接口
"""


class MessageSender(Sender):
    def __init__(self):
        self.name = "message-sender"

    def send(self, content):
        # do Something
        print(f"{self.name} has sent: {content}")


"""
MailSender实现了Sender接口
"""


class MailSender(Sender):
    def __init__(self):
        self.name = "mail-sender"

    def send(self, content):
        # do Something
        print(f"{self.name} has sent: {content}")


"""
PushSender实现了Sender接口
"""


class PushSender(Sender):
    def __init__(self):
        self.name = "push-sender"

    def send(self, content):
        # do Something
        print(f"{self.name} has sent: {content}")


# 测试验证
notification = Notification(MailSender())
content = "hello, how are you!"
notification.send(content)
# 更换为MessageSender
notification.set_sender(MessageSender())
notification.send(content)
# 更换为PushSender
notification.set_sender(PushSender())
notification.send(content)

"""
jarry@jarrys-MBP python % python3 dependency_inversion_good_example.py 
send content by Notification.
mail-sender has sent: hello, how are you!
send content by Notification.
message-sender has sent: hello, how are you!
send content by Notification.
push-sender has sent: hello, how are you!
"""
