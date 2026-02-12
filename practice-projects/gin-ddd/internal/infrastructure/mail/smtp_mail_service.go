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
	utils.GetLogger().Info("[MailService] 开始发送订单确认邮件")
	utils.GetLogger().Info("[MailService] 收件人: %s <%s>", userName, userEmail)

	if !isValidEmail(userEmail) {
		utils.GetLogger().Info("[MailService] 邮箱验证失败: %s", userEmail)
		return fmt.Errorf("无效的邮箱地址: %s", userEmail)
	}
	utils.GetLogger().Info("[MailService] 邮箱格式验证成功")

	// 准备模板数据
	utils.GetLogger().Info("[MailService] 准备邮件模板数据...")
	templateData := OrderConfirmationMailData{
		UserName:    userName,
		OrderNo:     fmt.Sprintf("%v", orderData["order_no"]),
		OrderID:     toInt64(orderData["order_id"]),
		TotalAmount: toFloat64(orderData["total_amount"]),
		Status:      fmt.Sprintf("%v", orderData["status"]),
	}
	utils.GetLogger().Info("[MailService] 模板数据: orderNo=%s, amount=%.2f, status=%s", templateData.OrderNo, templateData.TotalAmount, templateData.Status)

	// 生成邮件HTML内容
	utils.GetLogger().Info("[MailService] 生成邮件HTML内容...")
	tmpl := GetOrderConfirmationTemplate()
	var buf bytes.Buffer
	if err := tmpl.Execute(&buf, templateData); err != nil {
		utils.GetLogger().Error("[MailService] 生成HTML失败: %v", err)
		return fmt.Errorf("生成邮件模板失败: %w", err)
	}
	utils.GetLogger().Info("[MailService] HTML生成成功, 邮件内容大小: %d bytes", buf.Len())

	// 创建邮件
	utils.GetLogger().Info("[MailService] 创建邮件对象...")
	e := email.NewEmail()
	e.From = fmt.Sprintf("%s <%s>", s.fromName, s.from)
	e.To = []string{userEmail}
	e.Subject = fmt.Sprintf("订单确认 - 订单号: %s", templateData.OrderNo)
	e.HTML = buf.Bytes()
	utils.GetLogger().Info("[MailService] 邮件对象创建成功: From=%s, To=%s, Subject=%s", e.From, userEmail, e.Subject)

	// 发送邮件（QQ 邮箱要求 TLS/SSL）
	utils.GetLogger().Info("[MailService] 连接SMTP服务器: %s:%d", s.host, s.port)
	addr := fmt.Sprintf("%s:%d", s.host, s.port)
	auth := smtp.PlainAuth("", s.username, s.password, s.host)
	tlsConfig := &tls.Config{
		ServerName: s.host,
		MinVersion: tls.VersionTLS12,
	}

	utils.GetLogger().Info("[MailService] 发送邮件...")
	var err error
	if s.port == 465 {
		err = e.SendWithTLS(addr, auth, tlsConfig)
	} else {
		// 默认使用 STARTTLS（适用于 587 等端口）
		err = e.SendWithStartTLS(addr, auth, tlsConfig)
	}
	if err != nil {
		utils.GetLogger().Error("[MailService] 邮件发送失败: %v", err)
		return fmt.Errorf("发送邮件失败: %w", err)
	}

	utils.GetLogger().Info("[MailService] 邮件发送成功")
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
