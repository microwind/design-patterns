package com.github.microwind.springboot4ddd.application.scheduled;

import com.github.microwind.springboot4ddd.domain.model.order.Order;
import com.github.microwind.springboot4ddd.domain.repository.order.OrderRepository;
import com.github.microwind.springboot4ddd.infrastructure.notification.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 订单扫描定时任务
 * 定期扫描超时未支付的订单，发送邮件通知管理员
 *
 * @author jarry
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderScanScheduledTask {

    private final OrderRepository orderRepository;
    private final EmailService emailService;

    private static final String ADMIN_EMAIL = "12262529@qq.com";
    private static final int TIMEOUT_HOURS = 2;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 定时扫描超时未支付订单
     * 每小时执行一次（可根据需要调整）
     * cron表达式: 0 0 * * * ? 表示每小时整点执行
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void scanUnpaidOrders() {
        log.info("开始扫描超时未支付订单...");

        try {
            // 计算超时时间点：当前时间减去2小时
            LocalDateTime timeoutThreshold = LocalDateTime.now().minusHours(TIMEOUT_HOURS);

            // 查询超时未支付的订单
            List<Order> unpaidOrders = orderRepository.findByStatusAndCreatedAtBefore(
                Order.OrderStatus.PENDING.name(),
                timeoutThreshold
            );

            if (unpaidOrders.isEmpty()) {
                log.info("没有发现超时未支付订单");
                return;
            }

            log.info("发现 {} 个超时未支付订单", unpaidOrders.size());

            // 构建邮件内容
            String emailContent = buildEmailContent(unpaidOrders, timeoutThreshold);

            // 发送邮件给管理员
            emailService.sendHtmlEmail(
                ADMIN_EMAIL,
                String.format("订单超时提醒 - 发现%d个超过%d小时未支付订单", unpaidOrders.size(), TIMEOUT_HOURS),
                emailContent
            );

            log.info("超时订单提醒邮件已发送至管理员: {}", ADMIN_EMAIL);

        } catch (Exception e) {
            log.error("扫描超时订单失败", e);
        }
    }

    /**
     * 构建邮件内容
     */
    private String buildEmailContent(List<Order> orders, LocalDateTime threshold) {
        StringBuilder content = new StringBuilder();
        content.append("<html><body>");
        content.append("<h2>订单超时提醒</h2>");
        content.append("<p>以下订单创建超过").append(TIMEOUT_HOURS).append("小时仍未支付：</p>");
        content.append("<table border='1' cellpadding='8' cellspacing='0' style='border-collapse: collapse;'>");
        content.append("<tr style='background-color: #f2f2f2;'>");
        content.append("<th>订单ID</th>");
        content.append("<th>订单号</th>");
        content.append("<th>用户ID</th>");
        content.append("<th>订单金额</th>");
        content.append("<th>创建时间</th>");
        content.append("<th>超时时长</th>");
        content.append("</tr>");

        for (Order order : orders) {
            long hours = java.time.Duration.between(order.getCreatedAt(), LocalDateTime.now()).toHours();
            content.append("<tr>");
            content.append("<td>").append(order.getId()).append("</td>");
            content.append("<td>").append(order.getOrderNo()).append("</td>");
            content.append("<td>").append(order.getUserId()).append("</td>");
            content.append("<td>").append(order.getTotalAmount()).append("</td>");
            content.append("<td>").append(order.getCreatedAt().format(DATE_FORMATTER)).append("</td>");
            content.append("<td>").append(hours).append("小时</td>");
            content.append("</tr>");
        }

        content.append("</table>");
        content.append("<p style='margin-top: 20px;'>扫描时间: ").append(LocalDateTime.now().format(DATE_FORMATTER)).append("</p>");
        content.append("<p>超时阈值: 创建时间早于 ").append(threshold.format(DATE_FORMATTER)).append("</p>");
        content.append("</body></html>");

        return content.toString();
    }
}
