"""
依赖反转原则 - 反例
这个例子违反了依赖反转原则，为了方便理解把相关类放在一起。
1. 高层业务应用直接依赖了低层逻辑对象，过度耦合。
2. 依赖的是实现对象而不是接口或抽象类，不便于扩展。
对比：正例让高层依赖抽象接口，具体实现可替换、可扩展。
"""

class MessageSender:
    name = "message-sender"

    def send(self, content):
        print(f"{self.name} has sent: {content}")


class MailSender:
    name = "mail-sender"

    def send(self, content):
        print(f"{self.name} has sent: {content}")


class PushSender:
    name = "push-sender"

    def send(self, content):
        print(f"{self.name} has sent: {content}")


# 违规点：高层模块直接依赖具体发送器实现
class Notification:
    # 直接依赖具体实现，无法替换
    def __init__(self):
        self.message_sender = MessageSender()
        self.mail_sender = MailSender()
        self.push_sender = PushSender()

    # 高层方法直接调用具体实现
    def send_message(self, content):
        print("send message by Notification.")
        self.message_sender.send(content)

    def send_email(self, content):
        print("send email by Notification.")
        self.mail_sender.send(content)

    def send_push(self, content):
        print("send push by Notification.")
        self.push_sender.send(content)


notification = Notification()
content = "hello, how are you!"
notification.send_message(content)
notification.send_email(content)
notification.send_push(content)

"""
jarry@jarrys-MBP python % python3 dependency_inversion_bad_example.py 
send message by Notification.
message-sender has sent: hello, how are you!
send email by Notification.
mail-sender has sent: hello, how are you!
send push by Notification.
push-sender has sent: hello, how are you!
"""
