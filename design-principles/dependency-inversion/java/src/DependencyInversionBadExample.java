package src;

/**
 * 这个例子违反了依赖反转原则，为了方面理解，把全部类放在了一起。
 * 1. 高层业务应用直接依赖了低层逻辑对象，过度耦合。
 * 2. 依赖的是实现对象而不是接口或抽象类，不便于扩展。
 */
public class DependencyInversionBadExample {
    public DependencyInversionBadExample() {
        return;
    }

    // 上层消息业务应用
    public class Notification {

        // 依赖低层模块具体的发送对象
        public MessageSender messageSender = new MessageSender();
        public MailSender mailSender = new MailSender();
        public PushSender pushSender = new PushSender();

        // 依赖发送短信方法
        public void sendMessage(String content) {
            System.out.println("send message by Notification.");
            messageSender.send(content);
        }

        // 依赖发送邮件方法
        public void sendEmail(String content) {
            System.out.println("send email by Notification.");
            mailSender.send(content);
        }

        // 依赖发送push方法
        public void sendPush(String content) {
            System.out.println("send push by Notification.");
            pushSender.send(content);
        }
    }

    public class MessageSender {
        public String name = "message-sender";

        public void send(String content) {
            // do Something
            System.out.println(this.name + " has sent: " + content);
        }
    }

    public class MailSender {
        public String name = "mail-sender";

        public void send(String content) {
            // do Something
            System.out.println(this.name + " has sent: " + content);
        }
    }

    public class PushSender {
        public String name = "push-sender";

        public void send(String content) {
            // do Something
            System.out.println(this.name + " has sent: " + content);
        }
    }
    
    public static void main(String[] args) {
        System.out.println("Testing DependencyInversionBadExample...");
        DependencyInversionBadExample example = new DependencyInversionBadExample();
        Notification notification = example.new Notification();
        
        String content = "Hello, this is a test message.";
        notification.sendMessage(content);
        notification.sendEmail(content);
        notification.sendPush(content);
    }
}