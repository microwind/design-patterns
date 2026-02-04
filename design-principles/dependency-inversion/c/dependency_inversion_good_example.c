/**
 * 依赖倒置。
 * 1. 高层次模块不依赖于低层的实现，而是依赖于低层的抽象。
 * 2. 接口和抽象类不应该依赖于具体实现类，而是具体实现类依赖接口或抽象类。
 */
#include <stdio.h>

// 发送者的接口或抽象类，供实际发送者对象继承和实现，C语言中以struct替代
typedef struct
{
    void (*send)(void *self, const char *content); // 基础发送方法
} Sender;

// MessageSender 对象
typedef struct
{
    Sender sender;    // 依赖 Sender 接口
    const char *name; // 发送者名称
} MessageSender;

// MessageSender 发送方法
void MessageSender_send(void *self, const char *content)
{
    MessageSender *message_sender = (MessageSender *)self;
    printf("%s 发送了：%s\n", message_sender->name, content);
}

// MailSender 对象
typedef struct
{
    Sender sender;    // 依赖 Sender 接口
    const char *name; // 发送者名称
} MailSender;

// MailSender 发送方法
void MailSender_send(void *self, const char *content)
{
    MailSender *mail_sender = (MailSender *)self;
    printf("%s 发送了：%s\n", mail_sender->name, content);
}

// PushSender 对象
typedef struct
{
    Sender sender;    // 依赖 Sender 接口
    const char *name; // 发送者名称
} PushSender;

// PushSender 发送方法
void PushSender_send(void *self, const char *content)
{
    PushSender *push_sender = (PushSender *)self;
    printf("%s 发送了：%s\n", push_sender->name, content);
}

// 消息通知业务应用类
typedef struct
{
    // 这里业务类依赖了Sender抽象接口，而不是具体对象
    // 这样即使增加再多Sender也不用额外修改
    Sender *sender;
} Notification;

// Notification 发送方法
void Notification_send(Notification *notification, const char *content)
{
    printf("通过通知发送内容。\n");
    notification->sender->send(notification->sender, content); // 调用发送方法
}

// Notification 设置发送者方法
void Notification_setSender(Notification *notification, Sender *sender)
{
    notification->sender = sender; // 设置发送者
}

int main()
{
    // 创建 Notification 对象
    Notification notification;
    // 初始化为 MailSender
    MailSender mail_sender = {{&MailSender_send}, "MailSender"};
    notification.sender = (Sender *)&mail_sender;

    // 测试发送内容
    const char *content = "你好，最近好吗？";
    Notification_send(&notification, content);

    // 更改为 MessageSender
    MessageSender message_sender = {{&MessageSender_send}, "MessageSender"};
    Notification_setSender(&notification, (Sender *)&message_sender);
    Notification_send(&notification, content);

    // 更改为 PushSender
    PushSender push_sender = {{&PushSender_send}, "PushSender"};
    Notification_setSender(&notification, (Sender *)&push_sender);
    Notification_send(&notification, content);

    return 0;
}

/**
jarry@jarrys-MBP c % gcc dependency_inversion_good_example.c 
jarry@jarrys-MBP c % ./a.out 
通过通知发送内容。
MailSender 发送了：你好，最近好吗？
通过通知发送内容。
MessageSender 发送了：你好，最近好吗？
通过通知发送内容。
PushSender 发送了：你好，最近好吗？
*/
