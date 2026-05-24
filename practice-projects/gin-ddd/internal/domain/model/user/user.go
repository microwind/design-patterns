// Package user 定义用户聚合根。
//
// 字段保留公开符合 Go 习惯,但状态迁移必须经行为方法,
// 不应在聚合根外部直接赋值。可空字段用 *string 表达,
// 避免 domain 层耦合 database/sql。
package user

import (
	"context"
	"time"

	"gin-ddd/internal/domain/errors"
	"gin-ddd/internal/domain/event"
	userService "gin-ddd/internal/domain/service/user"
)

// User 用户聚合根。
type User struct {
	ID          int64
	Name        string
	Email       string
	Phone       *string
	Address     *string
	CreatedTime time.Time
	UpdatedTime time.Time

	// events 收集行为方法产生的领域事件,由应用层 PullEvents 后统一发布。
	events []event.DomainEvent
}

// Register 注册新用户。通过领域服务做跨聚合的唯一性校验,
// 唯一性规则下沉到这里,避免散落在 Application Service。
func Register(ctx context.Context, checker userService.UserUniquenessChecker,
	name, email, phone, address string) (*User, error) {

	if name == "" {
		return nil, errors.NewInvalidArgument("用户名不能为空")
	}
	if email == "" {
		return nil, errors.NewInvalidArgument("邮箱不能为空")
	}

	nameExists, err := checker.ExistsByName(ctx, name)
	if err != nil {
		return nil, err
	}
	if nameExists {
		return nil, errors.NewUniquenessViolation("用户名已存在: " + name)
	}

	emailExists, err := checker.ExistsByEmail(ctx, email)
	if err != nil {
		return nil, err
	}
	if emailExists {
		return nil, errors.NewUniquenessViolation("邮箱已被使用: " + email)
	}

	now := time.Now()
	u := &User{
		Name:        name,
		Email:       email,
		Phone:       optionalString(phone),
		Address:     optionalString(address),
		CreatedTime: now,
		UpdatedTime: now,
	}
	return u, nil
}

// Restore 从持久化数据重建用户,仅供 infrastructure 层的 Converter 调用。
func Restore(id int64, name, email string, phone, address *string,
	createdTime, updatedTime time.Time) *User {
	return &User{
		ID:          id,
		Name:        name,
		Email:       email,
		Phone:       phone,
		Address:     address,
		CreatedTime: createdTime,
		UpdatedTime: updatedTime,
	}
}

// MarkPersisted 仓储 save 完成、回填主键后调用一次。
// 同时记录 UserCreatedEvent,符合"事件由聚合根记录"的 DDD 约定。
func (u *User) MarkPersisted(id int64) error {
	if u.ID != 0 {
		return errors.NewInvalidState("用户 ID 已存在,不可重复初始化")
	}
	u.ID = id
	u.recordEvent(event.NewUserCreatedEvent(u.ID, u.Name, u.Email))
	return nil
}

// UpdateEmail 修改邮箱,依赖领域服务校验全局唯一性。
func (u *User) UpdateEmail(ctx context.Context, checker userService.UserUniquenessChecker, newEmail string) error {
	if newEmail == "" {
		return errors.NewInvalidArgument("邮箱不能为空")
	}
	if u.Email == newEmail {
		return nil
	}

	exists, err := checker.ExistsByEmail(ctx, newEmail)
	if err != nil {
		return err
	}
	if exists {
		return errors.NewUniquenessViolation("邮箱已被使用: " + newEmail)
	}

	u.Email = newEmail
	u.UpdatedTime = time.Now()
	return nil
}

// UpdatePhone 修改手机号。空字符串表示清空。
func (u *User) UpdatePhone(newPhone string) error {
	u.Phone = optionalString(newPhone)
	u.UpdatedTime = time.Now()
	return nil
}

// UpdateAddress 修改地址。空字符串表示清空。
func (u *User) UpdateAddress(newAddress string) error {
	u.Address = optionalString(newAddress)
	u.UpdatedTime = time.Now()
	return nil
}

// PullEvents 返回累积事件并清空。
func (u *User) PullEvents() []event.DomainEvent {
	events := u.events
	u.events = nil
	return events
}

func (u *User) recordEvent(e event.DomainEvent) {
	u.events = append(u.events, e)
}

// optionalString 将可能为空的字符串包装为 *string;空字符串返回 nil。
func optionalString(s string) *string {
	if s == "" {
		return nil
	}
	return &s
}
