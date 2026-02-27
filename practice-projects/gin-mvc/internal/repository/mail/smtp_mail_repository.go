package mail

import (
	"bytes"
	"context"
	"crypto/tls"
	"fmt"
	"net/smtp"

	"gin-mvc/internal/services/notification"
	"gin-mvc/pkg/logger"

	"github.com/jordan-wright/email"
)

type SMTPMailRepository struct {
	host     string
	port     int
	username string
	password string
	from     string
	fromName string
}

func NewSMTPMailRepository(host string, port int, username, password, fromEmail, fromName string) notification.MailService {
	return &SMTPMailRepository{host: host, port: port, username: username, password: password, from: fromEmail, fromName: fromName}
}

func (s *SMTPMailRepository) SendOrderConfirmation(ctx context.Context, userEmail, userName string, orderData map[string]interface{}) error {
	if userEmail == "" {
		return fmt.Errorf("empty user email")
	}

	html := buildOrderConfirmationHTML(userName, orderData)
	e := email.NewEmail()
	e.From = fmt.Sprintf("%s <%s>", s.fromName, s.from)
	e.To = []string{userEmail}
	e.Subject = fmt.Sprintf("订单确认 - 订单号: %v", orderData["order_no"])
	e.HTML = []byte(html)

	addr := fmt.Sprintf("%s:%d", s.host, s.port)
	auth := smtp.PlainAuth("", s.username, s.password, s.host)
	tlsConfig := &tls.Config{ServerName: s.host, MinVersion: tls.VersionTLS12}

	var err error
	if s.port == 465 {
		err = e.SendWithTLS(addr, auth, tlsConfig)
	} else {
		err = e.SendWithStartTLS(addr, auth, tlsConfig)
	}
	if err != nil {
		logger.Ctx(ctx).Error("send mail failed", "to", userEmail, "err", err)
		return fmt.Errorf("send mail failed: %w", err)
	}

	logger.Ctx(ctx).Info("mail sent", "to", userEmail)
	return nil
}

func (s *SMTPMailRepository) Close() error {
	return nil
}

func buildOrderConfirmationHTML(userName string, orderData map[string]interface{}) string {
	var buf bytes.Buffer
	buf.WriteString("<html><body>")
	buf.WriteString(fmt.Sprintf("<h3>您好，%s</h3>", userName))
	buf.WriteString("<p>您的订单已创建成功，详情如下：</p>")
	buf.WriteString("<ul>")
	buf.WriteString(fmt.Sprintf("<li>订单号：%v</li>", orderData["order_no"]))
	buf.WriteString(fmt.Sprintf("<li>订单ID：%v</li>", orderData["order_id"]))
	buf.WriteString(fmt.Sprintf("<li>金额：%v</li>", orderData["total_amount"]))
	buf.WriteString(fmt.Sprintf("<li>状态：%v</li>", orderData["status"]))
	buf.WriteString("</ul>")
	buf.WriteString("</body></html>")
	return buf.String()
}
