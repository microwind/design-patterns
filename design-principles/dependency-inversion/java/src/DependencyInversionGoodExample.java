package src;

/**
 * 这个例子符合依赖倒置原则。
 * 1. 高层模块Notification依赖低层模块的接口Sender，而不是具体实现。
 * 2. 具体实现类MessageSender、MailSender、PushSender依赖接口Sender。
 * 3. 通过构造函数注入和setter方法实现依赖注入，便于替换具体实现。
 */
public class DependencyInversionGoodExample {
    public DependencyInversionGoodExample() {
        return;
    }

    /**
     * 发送者的接口或抽象类，供实际发送者对象实现
     */
    public interface Sender {
        public void send(String content);
    }

    /**
     * MessageSender实现了Sender接口
     */
    public class MessageSender implements Sender {

        public String name = "message-sender";
        @Override
        public void send(String content) {
            // do Something
            System.out.println(this.name + " has sent: " + content);
        }
    }

    /**
     * MailSender实现了Sender接口
     */
    public class MailSender implements Sender {

        public String name = "mail-sender";
        @Override
        public void send(String content) {
            // do Something
            System.out.println(this.name + " has sent: " + content);
        }
    }

    /**
     * PushSender实现了Sender接口
     */
    public class PushSender implements Sender {

        public String name = "push-sender";
        @Override
        public void send(String content) {
            // do Something
            System.out.println(this.name + " has sent: " + content);
        }
    }

    /**
     * 消息通知业务应用类，依赖低层模块的接口，而不是实现
     */
    public class Notification {
        // 这里业务类依赖了发送者抽象接口
        // 这样即使增加再多Sender也不用修改
        public Sender sender;

        public Notification(Sender sender) {
            this.sender = sender;
        }

        // 依赖抽象发送方法，根据类型执行某个具体对象的方法
        public void send(String content) {
            System.out.println("send content by Notification.");
            this.sender.send(content);
        }

        public void setSender(Sender sender) {
            this.sender = sender;
        }
    }

    public static void main(String[] args) {
        System.out.println("Testing DependencyInversionGoodExample...");
        DependencyInversionGoodExample example = new DependencyInversionGoodExample();
        
        // Test with MessageSender
        Notification notification = example.new Notification(example.new MessageSender());
        notification.send("Hello, this is a test message.");
        
        // Test with MailSender
        notification.setSender(example.new MailSender());
        notification.send("Hello, this is a test email.");
        
        // Test with PushSender
        notification.setSender(example.new PushSender());
        notification.send("Hello, this is a test push notification.");
    }
}
