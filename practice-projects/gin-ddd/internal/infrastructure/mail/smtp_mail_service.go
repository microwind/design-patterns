package mail

import (
	"bytes"
	"context"
	"crypto/tls"
	"fmt"
	"net/smtp"

	"gin-ddd/internal/domain/notification"
	"gin-ddd/pkg/utils"

	"github.com/jordan-wright/email"
)

// SMTPMailService SMTP邮件服务实现
type SMTPMailService struct {
	host     string
	port     int
	username string
	password string
	from     string
	fromName string
}

// NewSMTPMailService 创建SMTP邮件服务
func NewSMTPMailService(host string, port int, username, password, fromEmail, fromName string) notification.MailService {
	return &SMTPMailService{
		host:     host,
		port:     port,
		username: username,
		password: password,
		from:     fromEmail,
		fromName: fromName,
	}
}

// SendOrderConfirmationMail 发送订单确认邮件
func (s *SMTPMailService) SendOrderConfirmationMail(ctx context.Context, userEmail string, userName string, orderData map[string]interface{}) error {
	fmt.Printf("[MailService] 开始发送订单确认邮件\n")
	fmt.Printf("[MailService] 收件人: %s <%s>\n", userName, userEmail)

	if !isValidEmail(userEmail) {
		fmt.Printf("[MailService] 邮箱验证失败: %s\n", userEmail)
		utils.GetLogger().Info("无效的邮箱地址: %s", userEmail)
		return fmt.Errorf("无效的邮箱地址: %s", userEmail)
	}
	fmt.Printf("[MailService] 邮箱格式验证成功\n")

	// 准备模板数据
	fmt.Printf("[MailService] 准备邮件模板数据...\n")
	templateData := OrderConfirmationMailData{
		UserName:    userName,
		OrderNo:     fmt.Sprintf("%v", orderData["order_no"]),
		OrderID:     toInt64(orderData["order_id"]),
		TotalAmount: toFloat64(orderData["total_amount"]),
		Status:      fmt.Sprintf("%v", orderData["status"]),
	}
	fmt.Printf("[MailService] 模板数据: orderNo=%s, amount=%.2f, status=%s\n",
		templateData.OrderNo, templateData.TotalAmount, templateData.Status)

	// 生成邮件HTML内容
	fmt.Printf("[MailService] 生成邮件HTML内容...\n")
	tmpl := GetOrderConfirmationTemplate()
	var buf bytes.Buffer
	if err := tmpl.Execute(&buf, templateData); err != nil {
		fmt.Printf("[MailService] 生成HTML失败: %v\n", err)
		utils.GetLogger().Error("生成邮件模板失败: %v", err)
		return fmt.Errorf("生成邮件模板失败: %w", err)
	}
	fmt.Printf("[MailService] HTML生成成功, 邮件内容大小: %d bytes\n", buf.Len())

	// 创建邮件
	fmt.Printf("[MailService] 创建邮件对象...\n")
	e := email.NewEmail()
	e.From = fmt.Sprintf("%s <%s>", s.fromName, s.from)
	e.To = []string{userEmail}
	e.Subject = fmt.Sprintf("订单确认 - 订单号: %s", templateData.OrderNo)
	e.HTML = buf.Bytes()
	fmt.Printf("[MailService] 邮件对象创建成功: From=%s, To=%s, Subject=%s\n",
		e.From, userEmail, e.Subject)

	// 发送邮件（QQ 邮箱要求 TLS/SSL）
	fmt.Printf("[MailService] 连接SMTP服务器: %s:%d\n", s.host, s.port)
	addr := fmt.Sprintf("%s:%d", s.host, s.port)
	auth := smtp.PlainAuth("", s.username, s.password, s.host)
	tlsConfig := &tls.Config{
		ServerName: s.host,
		MinVersion: tls.VersionTLS12,
	}

	fmt.Printf("[MailService] 发送邮件...\n")
	var err error
	if s.port == 465 {
		err = e.SendWithTLS(addr, auth, tlsConfig)
	} else {
		// 默认使用 STARTTLS（适用于 587 等端口）
		err = e.SendWithStartTLS(addr, auth, tlsConfig)
	}
	if err != nil {
		fmt.Printf("[MailService] 邮件发送失败: %v\n", err)
		utils.GetLogger().Error("发送订单确认邮件失败 (收件人: %s): %v", userEmail, err)
		return fmt.Errorf("发送邮件失败: %w", err)
	}

	fmt.Printf("[MailService] 邮件发送成功\n")
	utils.GetLogger().Info("订单确认邮件发送成功 (收件人: %s, 订单号: %s)", userEmail, templateData.OrderNo)
	return nil
}

// Close 关闭邮件服务
func (s *SMTPMailService) Close() error {
	// SMTP 无需关闭连接，每次发送时建立新连接
	return nil
}

// isValidEmail 验证邮箱格式
func isValidEmail(email string) bool {
	if len(email) == 0 {
		return false
	}
	// 简单的邮箱验证
	return bytes.ContainsAny([]byte(email), "@") && bytes.ContainsAny([]byte(email), ".")
}

// toInt64 转换为int64
func toInt64(v interface{}) int64 {
	switch val := v.(type) {
	case int64:
		return val
	case int:
		return int64(val)
	case float64:
		return int64(val)
	default:
		return 0
	}
}

// toFloat64 转换为float64
func toFloat64(v interface{}) float64 {
	switch val := v.(type) {
	case float64:
		return val
	case int:
		return float64(val)
	case int64:
		return float64(val)
	default:
		return 0.0
	}
}
