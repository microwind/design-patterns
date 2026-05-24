package com.github.microwind.springboot4ddd.application.port;

/**
 * 邮件发送端口（application 层）
 *
 * <p>application 通过该接口发送邮件，具体实现（JavaMailSender / SES / SendGrid 等）
 * 由 infrastructure 提供。
 *
 * @author jarry
 * @since 1.0.0
 */
public interface EmailSender {

    /**
     * 发送纯文本邮件。
     */
    void sendSimpleEmail(String to, String subject, String content);

    /**
     * 发送 HTML 邮件。
     */
    void sendHtmlEmail(String to, String subject, String htmlContent);
}
