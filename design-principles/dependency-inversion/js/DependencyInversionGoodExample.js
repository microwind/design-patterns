/**
 * 依赖倒置。
 * 1. 高层次模块不依赖于低层的实现，而是依赖于低层的抽象。
 * 2. 接口和抽象类不应该依赖于具体实现类，而是具体实现类依赖接口或抽象类。
 */

/**
 * 消息通知业务应用类，依赖低层模块的接口，而不是实现
 */
class Notification {
  constructor(sender) {
    this.sender = sender;
  }

  // 依赖了发送者抽象接口，而不是具体对象
  // 这样即使增加再多Sender也不用额外修改
  send(content) {
    console.log("send content by Notification.");
    this.sender.send(content);
  }

  setSender(sender) {
    this.sender = sender;
  }
}

/**
 * 发送者的接口或抽象类，供实际发送者对象实现
 */
class Sender {
  send(content) {
    console.log("Sending content: ", content);
  }
}

/**
 * MessageSender实现了Sender接口
 */
class MessageSender extends Sender {
  constructor() {
    super();
    this.name = "message-sender";
  }

  send(content) {
    // do Something
    console.log(`${this.name} has sent: ${content}`);
  }
}

/**
 * MailSender实现了Sender接口
 */
class MailSender extends Sender {
  constructor() {
    super();
    this.name = "mail-sender";
  }

  send(content) {
    // do Something
    console.log(`${this.name} has sent: ${content}`);
  }
}

/**
 * PushSender实现了Sender接口
 */
class PushSender extends Sender {
  constructor() {
    super();
    this.name = "push-sender";
  }

  send(content) {
    // do Something
    console.log(`${this.name} has sent: ${content}`);
  }
}

/** 测试验证 */
const notification = new Notification(new MailSender());
const content = "hello, how are you!";
notification.send(content);
// 更换为MessageSender
notification.setSender(new MessageSender());
notification.send(content);
// 更换为PushSender
notification.setSender(new PushSender());
notification.send(content);

/**
jarry@jarrys-MBP js % node DependencyInversionGoodExample.js 
send content by Notification.
mail-sender has sent: hello, how are you!
send content by Notification.
message-sender has sent: hello, how are you!
send content by Notification.
push-sender has sent: hello, how are you!
*/
