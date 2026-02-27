package event

import (
	"context"

	modelevent "gin-mvc/internal/models/event"
	"gin-mvc/internal/services/notification"
	"gin-mvc/pkg/logger"
)

func HandleOrderEvent(ctx context.Context, evt modelevent.DomainEvent, mailService notification.MailService) error {
	logger.Ctx(ctx).Info("consume event", "event", evt.EventType())

	if evt.EventType() != modelevent.OrderCreatedEvent {
		return nil
	}
	if mailService == nil {
		return nil
	}

	orderEvt, ok := evt.(*modelevent.OrderEvent)
	if !ok {
		logger.Ctx(ctx).Warn("unexpected event type for order.created")
		return nil
	}

	payload := map[string]interface{}{
		"order_id":     orderEvt.OrderID,
		"order_no":     orderEvt.OrderNo,
		"total_amount": orderEvt.TotalAmount,
		"status":       orderEvt.Status,
	}
	if err := mailService.SendOrderConfirmation(ctx, orderEvt.UserEmail, orderEvt.UserName, payload); err != nil {
		logger.Ctx(ctx).Error("send order confirmation mail failed", "err", err)
	}
	return nil
}
