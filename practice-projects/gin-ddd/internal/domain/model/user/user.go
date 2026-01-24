package user

import (
	"errors"
	"time"
)

// User 用户实体（聚合根）
type User struct {
	ID        int64      `json:"id"`
	Name  string     `json:"name"`
	Email     string     `json:"email"`
	Phone     string     `json:"phone"`
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
func NewUser(name, email, phone, password string) (*User, error) {
	if name == "" {
		return nil, errors.New("用户名不能为空")
	}
	if email == "" {
		return nil, errors.New("邮箱不能为空")
	}
	if phone == "" {
		return nil, errors.New("手机号不能为空")
	}

	now := time.Now()
	return &User{
		Name:  name,
		Email:     email,
		Phone:     phone,
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

func (u *User) UpdatePhone(phone string) error {
	if phone == "" {
		return errors.New("手机号不能为空")
	}
	u.Phone = phone
	u.UpdatedAt = time.Now()
	return nil
}

// IsActive 判断用户是否激活
func (u *User) IsActive() bool {
	return u.Status == UserStatusActive
}
