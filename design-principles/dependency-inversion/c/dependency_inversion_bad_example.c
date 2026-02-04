#include <stdio.h>
/**
 * 依赖反转原则 - 反例
 * 这个例子违反了依赖反转原则，为了方便理解把相关类放在一起。
 * 1. 高层业务应用直接依赖了低层逻辑对象，过度耦合。
 * 2. 依赖的是实现对象而不是接口或抽象类，不便于扩展。
 * 对比：正例让高层依赖抽象接口，具体实现可替换、可扩展。
 */

typedef struct {
  const char *name;
} MessageSender;

typedef struct {
  const char *name;
} MailSender;

typedef struct {
  const char *name;
} PushSender;

void message_send(MessageSender *sender, const char *content) {
  printf("%s has sent: %s\n", sender->name, content);
}

void mail_send(MailSender *sender, const char *content) {
  printf("%s has sent: %s\n", sender->name, content);
}

void push_send(PushSender *sender, const char *content) {
  printf("%s has sent: %s\n", sender->name, content);
}

// 违规点：高层模块持有具体发送器
typedef struct {
  MessageSender messageSender;
  MailSender mailSender;
  PushSender pushSender;
} Notification;

// 高层方法直接依赖具体实现
void send_message(Notification *n, const char *content) {
  printf("send message by Notification.\n");
  message_send(&n->messageSender, content);
}

void send_email(Notification *n, const char *content) {
  printf("send email by Notification.\n");
  mail_send(&n->mailSender, content);
}

void send_push(Notification *n, const char *content) {
  printf("send push by Notification.\n");
  push_send(&n->pushSender, content);
}

int main() {
  Notification n = {
    {"message-sender"},
    {"mail-sender"},
    {"push-sender"},
  };
  const char *content = "hello, how are you!";
  send_message(&n, content);
  send_email(&n, content);
  send_push(&n, content);
  return 0;
}
/**
 * 依赖倒置原则 - 反例
 * 这个例子违反了依赖倒置原则，为了方便理解把相关类放在一起。
 * 1. 高层业务应用直接依赖了低层逻辑对象，过度耦合。
 * 2. 依赖的是实现对象而不是接口或抽象类，不便于扩展。
 * 对比：正例让高层依赖抽象接口，具体实现可替换、可扩展。
 */
