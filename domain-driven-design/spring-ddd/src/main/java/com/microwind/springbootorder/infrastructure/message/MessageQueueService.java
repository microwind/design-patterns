package com.microwind.springbootorder.infrastructure.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MessageQueueService {

    private static final Logger logger = LoggerFactory.getLogger(MessageQueueService.class);
    private static final BlockingQueue<String> queue = new LinkedBlockingQueue<>(); // 内存中的消息队列

    // 发送消息到队列
    public void sendMessage(String message) {
        try {
            queue.put(message);  // 将消息放入队列
            System.out.println("Sent: " + message);
        } catch (InterruptedException e) {
            logger.error("sendMessage error:", e);
        }
    }

    // 接收队列中的消息
    public void receiveMessages() {
        new Thread(() -> {
            try {
                while (true) {
                    String message = queue.take();  // 从队列中获取消息（阻塞直到有消息）
                    System.out.println("Received: " + message);
                }
            } catch (InterruptedException e) {
                logger.error("receiveMessages error:", e);
            }
        }).start();
    }

    // 主方法，演示如何发送和接收消息
    public static void main(String[] args) {
        MessageQueueService messageQueueService = new MessageQueueService();
        messageQueueService.receiveMessages();  // 启动接收消息的线程

        // 模拟发送消息
        messageQueueService.sendMessage("Order 1");
        messageQueueService.sendMessage("Order 2");
        messageQueueService.sendMessage("Order 3");
    }
}
