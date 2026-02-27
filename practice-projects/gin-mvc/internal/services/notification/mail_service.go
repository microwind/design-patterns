package notification

import "context"

type MailService interface {
	SendOrderConfirmation(ctx context.Context, userEmail, userName string, orderData map[string]interface{}) error
	Close() error
}
