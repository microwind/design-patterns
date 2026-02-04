/**
 * 依赖倒置。
 * 1. 高层次模块不依赖于低层的实现，而是依赖于低层的抽象。
 * 2. 接口和抽象类不应该依赖于具体实现类，而是具体实现类依赖接口或抽象类。
 */

/**
 * 消息通知业务应用类，依赖低层模块的接口，而不是实现
 */
class Notification {
  private sender: Sender

  constructor(sender: Sender) {
    this.sender = sender
  }

  // 这里业务类依赖了发送者抽象接口，而不是具体对象
  // 这样即使增加再多Sender也不用额外修改
  send(content: string) {
    console.log('send content by Notification.')
    this.sender.send(content)
  }

  setSender(sender: Sender) {
    this.sender = sender
  }
}

/**
 * 发送者的接口或抽象类，供实际发送者对象实现
 */
interface Sender {
  send(content: string): void
}

/**
 * MessageSender实现了Sender接口
 */
class MessageSender implements Sender {
  name: string = 'message-sender'

  send(content: string) {
    // do Something
    console.log(`${this.name} has sent: ${content}`)
  }
}

/**
 * MailSender实现了Sender接口
 */
class MailSender implements Sender {
  name: string = 'mail-sender'

  send(content: string) {
    // do Something
    console.log(`${this.name} has sent: ${content}`)
  }
}

/**
 * PushSender实现了Sender接口
 */
class PushSender implements Sender {
  name: string = 'push-sender'

  send(content: string) {
    // do Something
    console.log(`${this.name} has sent: ${content}`)
  }
}

/** 测试验证 */
const notification = new Notification(new MailSender())
const content = 'hello, how are you!'
notification.send(content)
// 更换为MessageSender
notification.setSender(new MessageSender())
notification.send(content)
// 更换为PushSender
notification.setSender(new PushSender())
notification.send(content)

/**
jarry@jarrys-MBP ts % ts-node DependencyInversionGoodExample.ts 
send content by Notification.
mail-sender has sent: hello, how are you!
send content by Notification.
message-sender has sent: hello, how are you!
send content by Notification.
push-sender has sent: hello, how are you!
*/
