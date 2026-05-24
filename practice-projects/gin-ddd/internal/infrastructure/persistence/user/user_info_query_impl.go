// Package user 提供 UserInfoQueryClient 防腐层接口的基础设施实现。
//
// Order 上下文需要展示订单关联的用户简介,但不应直接依赖 User 仓储。
// 本包通过实现 domain/client/user.UserInfoQueryClient,把跨上下文的
// 用户简介查询封装为 UserBriefInfo 视图。
package user

import (
	"context"

	clientPort "gin-ddd/internal/domain/client/user"
	userDomain "gin-ddd/internal/domain/repository/user"
)

// UserInfoQueryClientImpl 防腐层实现,内部走 UserRepository,
// 上层仅依赖 UserInfoQueryClient 接口,与 User 聚合根解耦。
type UserInfoQueryClientImpl struct {
	userRepo userDomain.UserRepository
}

// NewUserInfoQueryClient 构造一个 UserInfoQueryClient 实现。
func NewUserInfoQueryClient(userRepo userDomain.UserRepository) clientPort.UserInfoQueryClient {
	return &UserInfoQueryClientImpl{userRepo: userRepo}
}

// FindBriefByID 查询单个用户简介。
func (c *UserInfoQueryClientImpl) FindBriefByID(ctx context.Context, userID int64) (*clientPort.UserBriefInfo, error) {
	u, err := c.userRepo.FindByID(ctx, userID)
	if err != nil {
		return nil, err
	}
	if u == nil {
		return nil, nil
	}

	phone := ""
	if u.Phone != nil {
		phone = *u.Phone
	}
	return &clientPort.UserBriefInfo{
		ID:    u.ID,
		Name:  u.Name,
		Email: u.Email,
		Phone: phone,
	}, nil
}

// FindBriefsByIDs 批量查询,通过单条单条调用实现;
// 实际生产可改为 SQL IN 一次拉取,以彻底消除 N+1。
func (c *UserInfoQueryClientImpl) FindBriefsByIDs(ctx context.Context, userIDs []int64) (map[int64]*clientPort.UserBriefInfo, error) {
	out := make(map[int64]*clientPort.UserBriefInfo, len(userIDs))
	for _, id := range userIDs {
		brief, err := c.FindBriefByID(ctx, id)
		if err != nil {
			return nil, err
		}
		if brief != nil {
			out[id] = brief
		}
	}
	return out, nil
}
