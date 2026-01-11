package user

import (
	"gin-ddd/internal/domain/model/user"
	"time"
)

// UserDTO 用户数据传输对象
type UserDTO struct {
	ID        int64     `json:"id"`
	Username  string    `json:"username"`
	Email     string    `json:"email"`
	Status    string    `json:"status"`
	CreatedAt time.Time `json:"created_at"`
	UpdatedAt time.Time `json:"updated_at"`
}

// ToDTO 将用户实体转换为DTO
func ToDTO(u *user.User) *UserDTO {
	if u == nil {
		return nil
	}
	return &UserDTO{
		ID:        u.ID,
		Username:  u.Username,
		Email:     u.Email,
		Status:    string(u.Status),
		CreatedAt: u.CreatedAt,
		UpdatedAt: u.UpdatedAt,
	}
}

// ToDTOs 批量转换用户实体为DTO
func ToDTOs(users []*user.User) []*UserDTO {
	dtos := make([]*UserDTO, 0, len(users))
	for _, u := range users {
		dtos = append(dtos, ToDTO(u))
	}
	return dtos
}

// ToEntity 将DTO转换为用户实体
func ToEntity(dto *UserDTO) *user.User {
	if dto == nil {
		return nil
	}
	return &user.User{
		ID:        dto.ID,
		Username:  dto.Username,
		Email:     dto.Email,
		Status:    user.UserStatus(dto.Status),
		CreatedAt: dto.CreatedAt,
		UpdatedAt: dto.UpdatedAt,
	}
}
