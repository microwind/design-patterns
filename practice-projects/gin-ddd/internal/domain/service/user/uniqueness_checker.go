// Package user 定义用户领域内的领域服务接口。
//
// UserUniquenessChecker 是"跨聚合根的全局唯一性校验"这一规则的端口,
// 由 infrastructure 层借助 UserRepository 实现。聚合根 User 在
// Register / UpdateEmail 等行为方法中依赖它,避免规则散落在 Application Service。
package user

import "context"

// UserUniquenessChecker 用户唯一性领域服务。
//
// 接口位于 domain/service 内,实现位于 infrastructure 层,符合
// 依赖倒置(domain 不依赖具体仓储实现)。
type UserUniquenessChecker interface {
	// ExistsByName 检查指定用户名是否已存在。
	ExistsByName(ctx context.Context, name string) (bool, error)

	// ExistsByEmail 检查指定邮箱是否已存在。
	ExistsByEmail(ctx context.Context, email string) (bool, error)
}
