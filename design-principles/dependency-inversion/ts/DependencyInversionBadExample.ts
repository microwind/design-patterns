/**
 * 依赖反转原则 - 反例
 * 这个例子违反了依赖反转原则，为了方便理解把相关类放在一起。
 * 1. 高层业务应用直接依赖了低层逻辑对象，过度耦合。
 * 2. 依赖的是实现对象而不是接口或抽象类，不便于扩展。
 * 对比：正例让高层依赖抽象接口，具体实现可替换、可扩展。
 */
class MessageSender {
  name = 'message-sender';
  send(content: string): void {
    console.log(`${this.name} has sent: ${content}`);
  }
}

class MailSender {
  name = 'mail-sender';
  send(content: string): void {
    console.log(`${this.name} has sent: ${content}`);
  }
}

class PushSender {
  name = 'push-sender';
  send(content: string): void {
    console.log(`${this.name} has sent: ${content}`);
  }
}

// 违规点：高层模块直接依赖具体发送器实现
class Notification {
  // 直接依赖具体实现，无法替换
  private messageSender = new MessageSender();
  private mailSender = new MailSender();
  private pushSender = new PushSender();

  // 高层方法直接调用具体实现
  sendMessage(content: string): void {
    console.log('send message by Notification.');
    this.messageSender.send(content);
  }

  sendEmail(content: string): void {
    console.log('send email by Notification.');
    this.mailSender.send(content);
  }

  sendPush(content: string): void {
    console.log('send push by Notification.');
    this.pushSender.send(content);
  }
}

const notification = new Notification();
const content = 'hello, how are you!';
notification.sendMessage(content);
notification.sendEmail(content);
notification.sendPush(content);

/**
jarry@jarrys-MBP ts % ts-node DependencyInversionBadExample.ts 
send message by Notification.
message-sender has sent: hello, how are you!
send email by Notification.
mail-sender has sent: hello, how are you!
send push by Notification.
push-sender has sent: hello, how are you!
*/
