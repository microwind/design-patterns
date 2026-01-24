package user

import (
	"database/sql"
	"gin-ddd/internal/domain/model/user"
	"time"
)

// UserDTO 用户数据传输对象
type UserDTO struct {
	ID          int64      `json:"id"`
	Name        string     `json:"name"`
	Email       string     `json:"email"`
	Phone       *string    `json:"phone,omitempty"`
	CreatedTime time.Time  `json:"created_time"`
	UpdatedTime time.Time  `json:"updated_time"`
}

// ToDTO 将用户实体转换为DTO
func ToDTO(u *user.User) *UserDTO {
	if u == nil {
		return nil
	}
	dto := &UserDTO{
		ID:          u.ID,
		Name:        u.Name,
		Email:       u.Email,
		CreatedTime: u.CreatedTime,
		UpdatedTime: u.UpdatedTime,
	}
	if u.Phone.Valid {
		dto.Phone = &u.Phone.String
	}
	return dto
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
	var phoneVal sql.NullString
	if dto.Phone != nil {
		phoneVal = sql.NullString{String: *dto.Phone, Valid: true}
	}
	return &user.User{
		ID:          dto.ID,
		Name:        dto.Name,
		Email:       dto.Email,
		Phone:       phoneVal,
		CreatedTime: dto.CreatedTime,
		UpdatedTime: dto.UpdatedTime,
	}
}
