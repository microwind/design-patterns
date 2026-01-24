package user

import "time"

// UserResponse 用户响应
type UserResponse struct {
	ID        int64     `json:"id"`
	Name  string    `json:"name"`
	Email     string    `json:"email"`
	Status    string    `json:"status"`
	CreatedAt time.Time `json:"created_at"`
	UpdatedAt time.Time `json:"updated_at"`
}
