package src;


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