package user

import (
	"context"

	"gin-mvc/internal/models/user"
)

type Repository interface {
	Create(ctx context.Context, u *user.User) error
	Update(ctx context.Context, u *user.User) error
	Delete(ctx context.Context, id int64) error
	FindByID(ctx context.Context, id int64) (*user.User, error)
	FindByName(ctx context.Context, name string) (*user.User, error)
	FindByEmail(ctx context.Context, email string) (*user.User, error)
	FindAll(ctx context.Context) ([]*user.User, error)
}
