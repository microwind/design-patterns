package user

// CreateUserRequest 创建用户请求
type CreateUserRequest struct {
	Name  string `json:"name" binding:"required"`
	Email string `json:"email" binding:"required,email"`
	Phone string `json:"phone" binding:"required"`
}

// UpdateUserRequest 更新用户请求
type UpdateUserRequest struct {
	Email string `json:"email" binding:"omitempty,email"`
}

// UpdatePhoneRequest 更新密码请求
type UpdatePhoneRequest struct {
	NewPhone string `json:"new_Phone" binding:"required,min=6"`
}
