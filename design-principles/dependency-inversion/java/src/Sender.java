package src;


/**
 * 发送者的接口或抽象类，供实际发送者对象实现
 */
public interface Sender {
  public void send(String content);
}