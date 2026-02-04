package src;


/**
 * 消息通知业务应用类，依赖低层模块的接口，而不是实现
 */
public class Notification {
    // 这里业务类依赖了发送者抽象接口，而不是具体对象
    // 这样即使增加再多Sender也不用额外修改
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