package user

import (
	"context"
	"gin-ddd/internal/domain/model/user"
)

// UserRepository 用户仓储接口
type UserRepository interface {
	// Create 创建用户
	Create(ctx context.Context, user *user.User) error

	// Update 更新用户
	Update(ctx context.Context, user *user.User) error

	// Delete 删除用户
	Delete(ctx context.Context, id int64) error

	// FindByID 根据ID查询用户
	FindByID(ctx context.Context, id int64) (*user.User, error)

	// FindByName 根据用户名查询用户
	FindByName(ctx context.Context, name string) (*user.User, error)

	// FindByEmail 根据邮箱查询用户
	FindByEmail(ctx context.Context, email string) (*user.User, error)

	// FindAll 查询所有用户
	FindAll(ctx context.Context) ([]*user.User, error)

	// FindByStatus 根据状态查询用户
	FindByStatus(ctx context.Context, status user.UserStatus) ([]*user.User, error)
}
