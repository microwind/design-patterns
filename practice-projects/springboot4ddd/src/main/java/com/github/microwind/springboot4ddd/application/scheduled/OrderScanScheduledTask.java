package com.github.microwind.springboot4ddd.application.scheduled;

import com.github.microwind.springboot4ddd.application.port.EmailSender;
import com.github.microwind.springboot4ddd.domain.model.order.Order;
import com.github.microwind.springboot4ddd.domain.repository.order.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 订单扫描定时任务
 *
 * <p>定期扫描超时未支付的订单，发送邮件通知管理员。
 * 邮箱与阈值通过配置项注入；HTML 模板放在 {@code resources/templates/email/}。
 *
 * @author jarry
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderScanScheduledTask {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String TEMPLATE_PATH = "templates/email/order-timeout.html";

    private final OrderRepository orderRepository;
    private final EmailSender emailSender;

    @Value("${notification.admin-email:admin@example.com}")
    private String adminEmail;

    @Value("${notification.order-timeout-hours:2}")
    private int timeoutHours;

    @Scheduled(cron = "${notification.order-scan-cron:0 0 * * * ?}")
    public void scanUnpaidOrders() {
        log.info("开始扫描超时未支付订单...");
        try {
            LocalDateTime threshold = LocalDateTime.now().minusHours(timeoutHours);
            List<Order> unpaidOrders = orderRepository.findExpiredPendingOrders(threshold);

            if (unpaidOrders.isEmpty()) {
                log.info("没有发现超时未支付订单");
                return;
            }
            log.info("发现 {} 个超时未支付订单", unpaidOrders.size());

            String emailContent = renderEmail(unpaidOrders, threshold);
            String subject = String.format("订单超时提醒 - 发现%d个超过%d小时未支付订单",
                    unpaidOrders.size(), timeoutHours);
            emailSender.sendHtmlEmail(adminEmail, subject, emailContent);
            log.info("超时订单提醒邮件已发送至管理员: {}", adminEmail);
        } catch (Exception e) {
            log.error("扫描超时订单失败", e);
        }
    }

    private String renderEmail(List<Order> orders, LocalDateTime threshold) throws IOException {
        String template = loadTemplate();
        StringBuilder rows = new StringBuilder();
        LocalDateTime now = LocalDateTime.now();
        for (Order order : orders) {
            long hours = Duration.between(order.getCreatedAt(), now).toHours();
            rows.append("<tr>")
                    .append("<td>").append(order.getId()).append("</td>")
                    .append("<td>").append(order.getOrderNo()).append("</td>")
                    .append("<td>").append(order.getUserId()).append("</td>")
                    .append("<td>").append(order.getTotalAmount()).append("</td>")
                    .append("<td>").append(order.getCreatedAt().format(DATE_FORMATTER)).append("</td>")
                    .append("<td>").append(hours).append("小时</td>")
                    .append("</tr>");
        }
        return template
                .replace("{{timeoutHours}}", String.valueOf(timeoutHours))
                .replace("{{rows}}", rows.toString())
                .replace("{{scanTime}}", now.format(DATE_FORMATTER))
                .replace("{{thresholdTime}}", threshold.format(DATE_FORMATTER));
    }

    private String loadTemplate() throws IOException {
        ClassPathResource resource = new ClassPathResource(TEMPLATE_PATH);
        try (var in = resource.getInputStream()) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
