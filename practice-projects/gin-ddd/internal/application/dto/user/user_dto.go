package user

import (
	"gin-ddd/internal/domain/model/user"
	"time"
)

// UserDTO 用户数据传输对象。
type UserDTO struct {
	ID          int64     `json:"id"`
	Name        string    `json:"name"`
	Email       string    `json:"email"`
	Phone       *string   `json:"phone"`
	Address     *string   `json:"address,omitempty"`
	CreatedTime time.Time `json:"created_time"`
	UpdatedTime time.Time `json:"updated_time"`
}

// ToDTO 将用户实体转换为 DTO。
func ToDTO(u *user.User) *UserDTO {
	if u == nil {
		return nil
	}
	return &UserDTO{
		ID:          u.ID,
		Name:        u.Name,
		Email:       u.Email,
		Phone:       u.Phone,
		Address:     u.Address,
		CreatedTime: u.CreatedTime,
		UpdatedTime: u.UpdatedTime,
	}
}

// ToDTOs 批量转换。
func ToDTOs(users []*user.User) []*UserDTO {
	dtos := make([]*UserDTO, 0, len(users))
	for _, u := range users {
		dtos = append(dtos, ToDTO(u))
	}
	return dtos
}
