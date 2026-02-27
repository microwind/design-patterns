package order

import (
	"errors"
	"time"
)

type Status string

const (
	StatusPending   Status = "PENDING"
	StatusPaid      Status = "PAID"
	StatusShipped   Status = "SHIPPED"
	StatusDelivered Status = "DELIVERED"
	StatusCancelled Status = "CANCELLED"
	StatusRefunded  Status = "REFUNDED"
)

type Order struct {
	ID          int64     `json:"id"`
	OrderNo     string    `json:"order_no"`
	UserID      int64     `json:"user_id"`
	TotalAmount float64   `json:"total_amount"`
	Status      Status    `json:"status"`
	CreatedAt   time.Time `json:"created_at"`
	UpdatedAt   time.Time `json:"updated_at"`
}

func New(orderNo string, userID int64, totalAmount float64) (*Order, error) {
	if orderNo == "" {
		return nil, errors.New("order_no is required")
	}
	if userID <= 0 {
		return nil, errors.New("user_id is invalid")
	}
	if totalAmount <= 0 {
		return nil, errors.New("total_amount is invalid")
	}
	now := time.Now()
	return &Order{
		OrderNo:     orderNo,
		UserID:      userID,
		TotalAmount: totalAmount,
		Status:      StatusPending,
		CreatedAt:   now,
		UpdatedAt:   now,
	}, nil
}

func (o *Order) Pay() error {
	if o.Status != StatusPending {
		return errors.New("only pending order can be paid")
	}
	o.Status = StatusPaid
	o.UpdatedAt = time.Now()
	return nil
}

func (o *Order) Ship() error {
	if o.Status != StatusPaid {
		return errors.New("only paid order can be shipped")
	}
	o.Status = StatusShipped
	o.UpdatedAt = time.Now()
	return nil
}

func (o *Order) Deliver() error {
	if o.Status != StatusShipped {
		return errors.New("only shipped order can be delivered")
	}
	o.Status = StatusDelivered
	o.UpdatedAt = time.Now()
	return nil
}

func (o *Order) Cancel() error {
	if o.Status != StatusPending {
		return errors.New("only pending order can be cancelled")
	}
	o.Status = StatusCancelled
	o.UpdatedAt = time.Now()
	return nil
}

func (o *Order) Refund() error {
	if o.Status != StatusPaid && o.Status != StatusShipped {
		return errors.New("only paid or shipped order can be refunded")
	}
	o.Status = StatusRefunded
	o.UpdatedAt = time.Now()
	return nil
}
