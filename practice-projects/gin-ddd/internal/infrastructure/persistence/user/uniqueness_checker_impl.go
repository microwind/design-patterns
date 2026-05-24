package user

import (
	"context"

	userDomain "gin-ddd/internal/domain/repository/user"
	userService "gin-ddd/internal/domain/service/user"
)

// UniquenessCheckerImpl 是 UserUniquenessChecker 的基础设施实现。
//
// 内部复用 UserRepository 已有的 FindByName / FindByEmail 查询,
// 避免在 application 层散落唯一性检查逻辑。
type UniquenessCheckerImpl struct {
	userRepo userDomain.UserRepository
}

// NewUniquenessChecker 构造一个 UserUniquenessChecker 实现。
func NewUniquenessChecker(userRepo userDomain.UserRepository) userService.UserUniquenessChecker {
	return &UniquenessCheckerImpl{userRepo: userRepo}
}

// ExistsByName 检查用户名是否已存在。
func (c *UniquenessCheckerImpl) ExistsByName(ctx context.Context, name string) (bool, error) {
	u, err := c.userRepo.FindByName(ctx, name)
	if err != nil {
		return false, err
	}
	return u != nil, nil
}

// ExistsByEmail 检查邮箱是否已存在。
func (c *UniquenessCheckerImpl) ExistsByEmail(ctx context.Context, email string) (bool, error) {
	u, err := c.userRepo.FindByEmail(ctx, email)
	if err != nil {
		return false, err
	}
	return u != nil, nil
}
