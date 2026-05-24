// Package user 定义订单上下文跨界查询用户信息的防腐层接口。
//
// 订单上下文需要展示订单关联的用户简介(姓名、邮箱、电话),但不应直接
// 依赖 User 聚合根或 UserRepository——那会让两个 Bounded Context 紧耦合,
// 任何 User 模型变更都会波及 Order 上下文。
//
// UserInfoQueryClient 提供精简的 UserBriefInfo 视图,实现位于 infrastructure
// 层,内部可以走 UserRepository,也可以走 RPC / HTTP,对 Order 上下文透明。
package user

import "context"

// UserBriefInfo 跨上下文用户简介,仅含订单视图需要的字段,与 User 聚合根解耦。
type UserBriefInfo struct {
	ID    int64
	Name  string
	Email string
	Phone string
}

// UserInfoQueryClient 用户信息查询端口(防腐层)。
type UserInfoQueryClient interface {
	// FindBriefByID 查询单个用户简介,不存在时返回 (nil, nil)。
	FindBriefByID(ctx context.Context, userID int64) (*UserBriefInfo, error)

	// FindBriefsByIDs 批量查询,消除 N+1。返回 map 中缺失的用户 ID 表示未找到。
	FindBriefsByIDs(ctx context.Context, userIDs []int64) (map[int64]*UserBriefInfo, error)
}
