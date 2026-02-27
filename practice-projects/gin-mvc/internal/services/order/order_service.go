package order

import (
	"context"
	"errors"
	"fmt"
	"time"

	"gin-mvc/internal/models/event"
	model "gin-mvc/internal/models/order"
	orderrepo "gin-mvc/internal/repository/order"
	userrepo "gin-mvc/internal/repository/user"
	"gin-mvc/pkg/logger"
)

type Service struct {
	orderRepo      orderrepo.Repository
	userRepo       userrepo.Repository
	eventPublisher event.Publisher
	orderTopic     string
}

func New(orderRepo orderrepo.Repository, userRepo userrepo.Repository, publisher event.Publisher, orderTopic string) *Service {
	return &Service{orderRepo: orderRepo, userRepo: userRepo, eventPublisher: publisher, orderTopic: orderTopic}
}

func (s *Service) CreateOrder(ctx context.Context, userID int64, totalAmount float64) (*model.Order, error) {
	if userID <= 0 {
		return nil, errors.New("invalid user_id")
	}
	if totalAmount <= 0 {
		return nil, errors.New("invalid total_amount")
	}

	o, err := model.New(s.generateOrderNo(), userID, totalAmount)
	if err != nil {
		return nil, err
	}
	if err := s.orderRepo.Create(ctx, o); err != nil {
		return nil, err
	}

	email, name := s.userInfo(ctx, o.UserID)
	s.publishOrderEvent(ctx, event.NewOrderCreated(o.ID, o.OrderNo, o.UserID, email, name, o.TotalAmount), s.orderTopic)
	return o, nil
}

func (s *Service) GetOrderByID(ctx context.Context, id int64) (*model.Order, error) {
	o, err := s.orderRepo.FindByID(ctx, id)
	if err != nil {
		return nil, err
	}
	if o == nil {
		return nil, errors.New("order not found")
	}
	return o, nil
}

func (s *Service) GetAllOrders(ctx context.Context) ([]*model.Order, error) {
	return s.orderRepo.FindAll(ctx)
}

func (s *Service) GetUserOrders(ctx context.Context, userID int64) ([]*model.Order, error) {
	return s.orderRepo.FindByUserID(ctx, userID)
}

func (s *Service) PayOrder(ctx context.Context, id int64) error {
	o, err := s.mustGetOrder(ctx, id)
	if err != nil {
		return err
	}
	if err := o.Pay(); err != nil {
		return err
	}
	if err := s.orderRepo.Update(ctx, o); err != nil {
		return err
	}
	email, name := s.userInfo(ctx, o.UserID)
	s.publishOrderEvent(ctx, event.NewOrderPaid(o.ID, o.OrderNo, o.UserID, email, name, o.TotalAmount), s.orderTopic)
	return nil
}

func (s *Service) ShipOrder(ctx context.Context, id int64) error {
	o, err := s.mustGetOrder(ctx, id)
	if err != nil {
		return err
	}
	if err := o.Ship(); err != nil {
		return err
	}
	return s.orderRepo.Update(ctx, o)
}

func (s *Service) DeliverOrder(ctx context.Context, id int64) error {
	o, err := s.mustGetOrder(ctx, id)
	if err != nil {
		return err
	}
	if err := o.Deliver(); err != nil {
		return err
	}
	return s.orderRepo.Update(ctx, o)
}

func (s *Service) CancelOrder(ctx context.Context, id int64) error {
	o, err := s.mustGetOrder(ctx, id)
	if err != nil {
		return err
	}
	if err := o.Cancel(); err != nil {
		return err
	}
	if err := s.orderRepo.Update(ctx, o); err != nil {
		return err
	}
	email, name := s.userInfo(ctx, o.UserID)
	s.publishOrderEvent(ctx, event.NewOrderCancelled(o.ID, o.OrderNo, o.UserID, email, name), s.orderTopic)
	return nil
}

func (s *Service) RefundOrder(ctx context.Context, id int64) error {
	o, err := s.mustGetOrder(ctx, id)
	if err != nil {
		return err
	}
	if err := o.Refund(); err != nil {
		return err
	}
	return s.orderRepo.Update(ctx, o)
}

func (s *Service) mustGetOrder(ctx context.Context, id int64) (*model.Order, error) {
	o, err := s.orderRepo.FindByID(ctx, id)
	if err != nil {
		return nil, err
	}
	if o == nil {
		return nil, errors.New("order not found")
	}
	return o, nil
}

func (s *Service) generateOrderNo() string {
	return fmt.Sprintf("ORD%d", time.Now().UnixNano())
}

func (s *Service) userInfo(ctx context.Context, userID int64) (email, name string) {
	if s.userRepo == nil {
		return "", ""
	}
	u, err := s.userRepo.FindByID(ctx, userID)
	if err != nil || u == nil {
		return "", ""
	}
	return u.Email, u.Name
}

func (s *Service) publishOrderEvent(ctx context.Context, evt *event.OrderEvent, topic string) {
	if s.eventPublisher == nil || topic == "" {
		return
	}
	if err := s.eventPublisher.Publish(ctx, topic, evt); err != nil {
		logger.Ctx(ctx).Error("publish order event failed", "event", evt.EventType(), "err", err)
	}
}
