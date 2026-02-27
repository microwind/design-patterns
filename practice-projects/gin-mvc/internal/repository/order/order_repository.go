package order

import (
	"context"

	model "gin-mvc/internal/models/order"
)

type Repository interface {
	Create(ctx context.Context, o *model.Order) error
	Update(ctx context.Context, o *model.Order) error
	Delete(ctx context.Context, id int64) error
	FindByID(ctx context.Context, id int64) (*model.Order, error)
	FindByOrderNo(ctx context.Context, orderNo string) (*model.Order, error)
	FindByUserID(ctx context.Context, userID int64) ([]*model.Order, error)
	FindAll(ctx context.Context) ([]*model.Order, error)
	FindByStatus(ctx context.Context, status model.Status) ([]*model.Order, error)
}
