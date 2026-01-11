package user

// CreateUserRequest 创建用户请求
type CreateUserRequest struct {
	Username string `json:"username" binding:"required"`
	Email    string `json:"email" binding:"required,email"`
	Password string `json:"password" binding:"required,min=6"`
}

// UpdateUserRequest 更新用户请求
type UpdateUserRequest struct {
	Email string `json:"email" binding:"omitempty,email"`
}

// UpdatePasswordRequest 更新密码请求
type UpdatePasswordRequest struct {
	NewPassword string `json:"new_password" binding:"required,min=6"`
}
