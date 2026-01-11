package user

import (
	"errors"
	"time"
)

// User 用户实体（聚合根）
type User struct {
	ID        int64      `json:"id"`
	Username  string     `json:"username"`
	Email     string     `json:"email"`
	Password  string     `json:"-"` // 密码不在 JSON 中输出
	Status    UserStatus `json:"status"`
	CreatedAt time.Time  `json:"created_at"`
	UpdatedAt time.Time  `json:"updated_at"`
}

// UserStatus 用户状态
type UserStatus string

const (
	UserStatusActive   UserStatus = "ACTIVE"
	UserStatusInactive UserStatus = "INACTIVE"
	UserStatusBlocked  UserStatus = "BLOCKED"
)

// NewUser 创建新用户
func NewUser(username, email, password string) (*User, error) {
	if username == "" {
		return nil, errors.New("用户名不能为空")
	}
	if email == "" {
		return nil, errors.New("邮箱不能为空")
	}
	if password == "" {
		return nil, errors.New("密码不能为空")
	}

	now := time.Now()
	return &User{
		Username:  username,
		Email:     email,
		Password:  password,
		Status:    UserStatusActive,
		CreatedAt: now,
		UpdatedAt: now,
	}, nil
}

// Activate 激活用户
func (u *User) Activate() error {
	if u.Status == UserStatusBlocked {
		return errors.New("被封禁的用户无法激活")
	}
	u.Status = UserStatusActive
	u.UpdatedAt = time.Now()
	return nil
}

// Deactivate 停用用户
func (u *User) Deactivate() {
	u.Status = UserStatusInactive
	u.UpdatedAt = time.Now()
}

// Block 封禁用户
func (u *User) Block() {
	u.Status = UserStatusBlocked
	u.UpdatedAt = time.Now()
}

// UpdateEmail 更新邮箱
func (u *User) UpdateEmail(email string) error {
	if email == "" {
		return errors.New("邮箱不能为空")
	}
	u.Email = email
	u.UpdatedAt = time.Now()
	return nil
}

// UpdatePassword 更新密码
func (u *User) UpdatePassword(newPassword string) error {
	if newPassword == "" {
		return errors.New("密码不能为空")
	}
	u.Password = newPassword
	u.UpdatedAt = time.Now()
	return nil
}

// IsActive 判断用户是否激活
func (u *User) IsActive() bool {
	return u.Status == UserStatusActive
}
