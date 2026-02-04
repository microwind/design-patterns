package test;

import src.*;

public class Test {

  public static void start() {

    /**
     * 测试不符合依赖反转的例子
     */
    DependencyInversionBadExample dependencyInversionBadExample = new DependencyInversionBadExample();
    DependencyInversionBadExample.Notification notificationCounter = dependencyInversionBadExample.new Notification();
    String content = "hello, how are you!";
    notificationCounter.sendEmail(content);
    notificationCounter.sendMessage(content);
    notificationCounter.sendPush(content);

    // *********************** 分割线 ******************************************/
    System.out.println("***********************");

    /**
     * 测试符合依赖反转的例子
     * 上层业务对象依赖具体实现的接口，便于扩展。
     * 底层模块依赖上层接口而实现具体逻辑。
     */
    Notification notification = new Notification(new MailSender());
    String content2 = "hello, how are you!";
    notification.send(content2);
    // 更换为MessageSender
    notification.setSender(new MessageSender());
    notification.send(content2);
    // 更换为PushSender
    notification.setSender(new PushSender());
    notification.send(content2);
  }

  public static void main(String[] args) {
    System.out.println("test start:");
    start();
  }

}

/**
 * 测试
 * jarry@jarrys-MacBook-Pro java % javac -encoding utf-8 test/Test.java
 * jarry@jarrys-MacBook-Pro java % java test/Test
 * test start:
 * send email by Notification.
 * mail-sender has sent: hello, how are you!
 * send message by Notification.
 * message-sender has sent: hello, how are you!
 * send push by Notification.
 * push-sender has sent: hello, how are you!
 ***********************
 * send content by Notification.
 * mail-sender has sent: hello, how are you!
 * send content by Notification.
 * message-sender has sent: hello, how are you!
 * send content by Notification.
 * push-sender has sent: hello, how are you!
 */