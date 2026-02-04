package src;


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