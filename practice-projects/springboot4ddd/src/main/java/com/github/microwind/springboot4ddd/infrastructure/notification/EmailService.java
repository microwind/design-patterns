package com.github.microwind.springboot4ddd.infrastructure.notification;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * 邮件发送服务
 * 提供简单易用的邮件发送功能
 *
 * @author jarry
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * 发送简单文本邮件
     *
     * @param to      收件人邮箱
     * @param subject 邮件主题
     * @param content 邮件内容
     */
    public void sendSimpleEmail(String to, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, false);

            mailSender.send(message);
            log.info("邮件发送成功 - 收件人: {}, 主题: {}", to, subject);
        } catch (MessagingException e) {
            log.error("邮件发送失败 - 收件人: {}, 主题: {}, 错误: {}", to, subject, e.getMessage(), e);
            throw new RuntimeException("邮件发送失败", e);
        }
    }

    /**
     * 发送HTML格式邮件
     *
     * @param to      收件人邮箱
     * @param subject 邮件主题
     * @param htmlContent HTML格式的邮件内容
     */
    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("HTML邮件发送成功 - 收件人: {}, 主题: {}", to, subject);
        } catch (MessagingException e) {
            log.error("HTML邮件发送失败 - 收件人: {}, 主题: {}, 错误: {}", to, subject, e.getMessage(), e);
            throw new RuntimeException("HTML邮件发送失败", e);
        }
    }
}
