/**
 * 依赖反转原则 - 反例
 * 这个例子违反了依赖反转原则，为了方便理解把相关类放在一起。
 * 1. 高层业务应用直接依赖了低层逻辑对象，过度耦合。
 * 2. 依赖的是实现对象而不是接口或抽象类，不便于扩展。
 * 对比：正例让高层依赖抽象接口，具体实现可替换、可扩展。
 */
class MessageSender {
  constructor() {
    this.name = 'message-sender';
  }

  send(content) {
    console.log(`${this.name} has sent: ${content}`);
  }
}

class MailSender {
  constructor() {
    this.name = 'mail-sender';
  }

  send(content) {
    console.log(`${this.name} has sent: ${content}`);
  }
}

class PushSender {
  constructor() {
    this.name = 'push-sender';
  }

  send(content) {
    console.log(`${this.name} has sent: ${content}`);
  }
}

// 违规点：高层模块直接依赖具体发送器实现
class Notification {
  // 直接 new 具体实现，导致强耦合且难以替换
  constructor() {
    this.messageSender = new MessageSender();
    this.mailSender = new MailSender();
    this.pushSender = new PushSender();
  }

  // 高层方法直接调用具体实现
  sendMessage(content) {
    console.log('send message by Notification.');
    this.messageSender.send(content);
  }

  sendEmail(content) {
    console.log('send email by Notification.');
    this.mailSender.send(content);
  }

  sendPush(content) {
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
jarry@jarrys-MBP js % node DependencyInversionBadExample.js 
send message by Notification.
message-sender has sent: hello, how are you!
send email by Notification.
mail-sender has sent: hello, how are you!
send push by Notification.
push-sender has sent: hello, how are you!
*/
