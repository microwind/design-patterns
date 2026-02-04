package src;


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