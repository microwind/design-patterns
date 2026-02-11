package user

import (
	userDTO "gin-ddd/internal/application/dto/user"
	"time"
)

// UserResponse 用户响应
type UserResponse struct {
	ID        int64     `json:"id"`
	Name      string    `json:"name"`
	Email     string    `json:"email"`
	Phone     *string   `json:"phone,omitempty"`
	CreatedTime time.Time `json:"created_time"`
	UpdatedTime time.Time `json:"updated_time"`
}

// FromUserDTO 将用户DTO转换为响应VO
func FromUserDTO(dto *userDTO.UserDTO) *UserResponse {
	if dto == nil {
		return nil
	}
	return &UserResponse{
		ID:          dto.ID,
		Name:        dto.Name,
		Email:       dto.Email,
		Phone:       dto.Phone,
		CreatedTime: dto.CreatedTime,
		UpdatedTime: dto.UpdatedTime,
	}
}

// FromUserDTOs 批量将用户DTO转换为响应VO
func FromUserDTOs(dtos []*userDTO.UserDTO) []*UserResponse {
	responses := make([]*UserResponse, 0, len(dtos))
	for _, dto := range dtos {
		responses = append(responses, FromUserDTO(dto))
	}
	return responses
}
