package notification

import "context"

// MailService 邮件服务接口
type MailService interface {
	// SendOrderConfirmationMail 发送订单确认邮件
	SendOrderConfirmationMail(ctx context.Context, userEmail string, userName string, orderData map[string]interface{}) error

	// Close 关闭邮件服务
	Close() error
}
