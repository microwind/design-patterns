package user

import (
	"database/sql"
	"errors"
	"time"
)

type User struct {
	ID          int64          `json:"id"`
	Name        string         `json:"name"`
	Email       string         `json:"email"`
	Phone       sql.NullString `json:"phone"`
	CreatedTime time.Time      `json:"created_time"`
	UpdatedTime time.Time      `json:"updated_time"`
}

func New(name, email, phone string) (*User, error) {
	if name == "" {
		return nil, errors.New("name is required")
	}
	if email == "" {
		return nil, errors.New("email is required")
	}
	now := time.Now()
	u := &User{
		Name:        name,
		Email:       email,
		CreatedTime: now,
		UpdatedTime: now,
	}
	if phone != "" {
		u.Phone = sql.NullString{String: phone, Valid: true}
	}
	return u, nil
}

func (u *User) UpdateEmail(email string) error {
	if email == "" {
		return errors.New("email is required")
	}
	u.Email = email
	u.UpdatedTime = time.Now()
	return nil
}

func (u *User) UpdatePhone(phone string) {
	if phone == "" {
		u.Phone = sql.NullString{Valid: false}
	} else {
		u.Phone = sql.NullString{String: phone, Valid: true}
	}
	u.UpdatedTime = time.Now()
}
