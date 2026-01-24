package user

import (
	"database/sql"
	"errors"
	"time"
)

// User 用户实体（聚合根）
type User struct {
	ID          int64         `json:"id"`
	Name        string        `json:"name"`
	Email       string        `json:"email"`
	Phone       sql.NullString `json:"phone"`
	CreatedTime time.Time    `json:"created_time"`
	UpdatedTime time.Time    `json:"updated_time"`
}

// NewUser 创建新用户
func NewUser(name, email, phone, password string) (*User, error) {
	if name == "" {
		return nil, errors.New("用户名不能为空")
	}
	if email == "" {
		return nil, errors.New("邮箱不能为空")
	}
	// phone 允许为空，但如果不为空则存入
	var phoneVal sql.NullString
	if phone != "" {
		phoneVal = sql.NullString{String: phone, Valid: true}
	}

	now := time.Now()
	return &User{
		Name:        name,
		Email:       email,
		Phone:       phoneVal,
		CreatedTime: now,
		UpdatedTime: now,
	}, nil
}

// UpdateEmail 更新邮箱
func (u *User) UpdateEmail(email string) error {
	if email == "" {
		return errors.New("邮箱不能为空")
	}
	u.Email = email
	u.UpdatedTime = time.Now()
	return nil
}

func (u *User) UpdatePhone(phone string) error {
	// phone 允许为空，但如果不为空则存入
	if phone != "" {
		u.Phone = sql.NullString{String: phone, Valid: true}
	} else {
		u.Phone = sql.NullString{Valid: false}
	}
	u.UpdatedTime = time.Now()
	return nil
}
