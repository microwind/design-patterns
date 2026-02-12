package user

// CreateUserRequest 创建用户请求
type CreateUserRequest struct {
	Name    string `json:"name" binding:"required"`
	Email   string `json:"email" binding:"required,email"`
	Phone   string `json:"phone" binding:"required,phone"`
	Address string `json:"address"`
}

// UpdateEmailRequest 更新用户邮箱请求
type UpdateEmailRequest struct {
	Email string `json:"email" binding:"omitempty,email"`
}

// UpdatePhoneRequest 更新手机号请求
type UpdatePhoneRequest struct {
	NewPhone string `json:"new_phone" binding:"required,phone"`
}
