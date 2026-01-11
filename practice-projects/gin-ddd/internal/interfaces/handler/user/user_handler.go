package user

import (
	"gin-ddd/internal/application/service/user"
	"gin-ddd/internal/infrastructure/common"
	"gin-ddd/internal/interfaces/vo/user"
	"strconv"

	"github.com/gin-gonic/gin"
)

// UserHandler 用户处理器
type UserHandler struct {
	userService *user.UserService
}

// NewUserHandler 创建用户处理器
func NewUserHandler(userService *user.UserService) *UserHandler {
	return &UserHandler{
		userService: userService,
	}
}

// CreateUser 创建用户
// @Summary 创建用户
// @Tags 用户管理
// @Accept json
// @Produce json
// @Param body body user.CreateUserRequest true "用户信息"
// @Success 200 {object} common.Response
// @Router /api/users [post]
func (h *UserHandler) CreateUser(c *gin.Context) {
	var req user.CreateUserRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		common.BadRequest(c, err.Error())
		return
	}

	userDTO, err := h.userService.CreateUser(c.Request.Context(), req.Username, req.Email, req.Password)
	if err != nil {
		common.Error(c, 1001, err.Error())
		return
	}

	common.SuccessWithMessage(c, "用户创建成功", userDTO)
}

// GetUser 获取用户
// @Summary 获取用户
// @Tags 用户管理
// @Produce json
// @Param id path int true "用户ID"
// @Success 200 {object} common.Response
// @Router /api/users/{id} [get]
func (h *UserHandler) GetUser(c *gin.Context) {
	id, err := strconv.ParseInt(c.Param("id"), 10, 64)
	if err != nil {
		common.BadRequest(c, "无效的用户ID")
		return
	}

	userDTO, err := h.userService.GetUserByID(c.Request.Context(), id)
	if err != nil {
		common.Error(c, 1001, err.Error())
		return
	}

	common.Success(c, userDTO)
}

// GetAllUsers 获取所有用户
// @Summary 获取所有用户
// @Tags 用户管理
// @Produce json
// @Success 200 {object} common.Response
// @Router /api/users [get]
func (h *UserHandler) GetAllUsers(c *gin.Context) {
	users, err := h.userService.GetAllUsers(c.Request.Context())
	if err != nil {
		common.InternalServerError(c, err.Error())
		return
	}

	common.Success(c, users)
}

// UpdateEmail 更新邮箱
// @Summary 更新用户邮箱
// @Tags 用户管理
// @Accept json
// @Produce json
// @Param id path int true "用户ID"
// @Param body body user.UpdateUserRequest true "邮箱信息"
// @Success 200 {object} common.Response
// @Router /api/users/{id}/email [put]
func (h *UserHandler) UpdateEmail(c *gin.Context) {
	id, err := strconv.ParseInt(c.Param("id"), 10, 64)
	if err != nil {
		common.BadRequest(c, "无效的用户ID")
		return
	}

	var req user.UpdateUserRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		common.BadRequest(c, err.Error())
		return
	}

	if err := h.userService.UpdateEmail(c.Request.Context(), id, req.Email); err != nil {
		common.Error(c, 1001, err.Error())
		return
	}

	common.SuccessWithMessage(c, "邮箱更新成功", nil)
}

// UpdatePassword 更新密码
// @Summary 更新用户密码
// @Tags 用户管理
// @Accept json
// @Produce json
// @Param id path int true "用户ID"
// @Param body body user.UpdatePasswordRequest true "密码信息"
// @Success 200 {object} common.Response
// @Router /api/users/{id}/password [put]
func (h *UserHandler) UpdatePassword(c *gin.Context) {
	id, err := strconv.ParseInt(c.Param("id"), 10, 64)
	if err != nil {
		common.BadRequest(c, "无效的用户ID")
		return
	}

	var req user.UpdatePasswordRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		common.BadRequest(c, err.Error())
		return
	}

	if err := h.userService.UpdatePassword(c.Request.Context(), id, req.NewPassword); err != nil {
		common.Error(c, 1001, err.Error())
		return
	}

	common.SuccessWithMessage(c, "密码更新成功", nil)
}

// ActivateUser 激活用户
// @Summary 激活用户
// @Tags 用户管理
// @Produce json
// @Param id path int true "用户ID"
// @Success 200 {object} common.Response
// @Router /api/users/{id}/activate [put]
func (h *UserHandler) ActivateUser(c *gin.Context) {
	id, err := strconv.ParseInt(c.Param("id"), 10, 64)
	if err != nil {
		common.BadRequest(c, "无效的用户ID")
		return
	}

	if err := h.userService.ActivateUser(c.Request.Context(), id); err != nil {
		common.Error(c, 1001, err.Error())
		return
	}

	common.SuccessWithMessage(c, "用户激活成功", nil)
}

// DeactivateUser 停用用户
// @Summary 停用用户
// @Tags 用户管理
// @Produce json
// @Param id path int true "用户ID"
// @Success 200 {object} common.Response
// @Router /api/users/{id}/deactivate [put]
func (h *UserHandler) DeactivateUser(c *gin.Context) {
	id, err := strconv.ParseInt(c.Param("id"), 10, 64)
	if err != nil {
		common.BadRequest(c, "无效的用户ID")
		return
	}

	if err := h.userService.DeactivateUser(c.Request.Context(), id); err != nil {
		common.Error(c, 1001, err.Error())
		return
	}

	common.SuccessWithMessage(c, "用户停用成功", nil)
}

// BlockUser 封禁用户
// @Summary 封禁用户
// @Tags 用户管理
// @Produce json
// @Param id path int true "用户ID"
// @Success 200 {object} common.Response
// @Router /api/users/{id}/block [put]
func (h *UserHandler) BlockUser(c *gin.Context) {
	id, err := strconv.ParseInt(c.Param("id"), 10, 64)
	if err != nil {
		common.BadRequest(c, "无效的用户ID")
		return
	}

	if err := h.userService.BlockUser(c.Request.Context(), id); err != nil {
		common.Error(c, 1001, err.Error())
		return
	}

	common.SuccessWithMessage(c, "用户封禁成功", nil)
}

// DeleteUser 删除用户
// @Summary 删除用户
// @Tags 用户管理
// @Produce json
// @Param id path int true "用户ID"
// @Success 200 {object} common.Response
// @Router /api/users/{id} [delete]
func (h *UserHandler) DeleteUser(c *gin.Context) {
	id, err := strconv.ParseInt(c.Param("id"), 10, 64)
	if err != nil {
		common.BadRequest(c, "无效的用户ID")
		return
	}

	if err := h.userService.DeleteUser(c.Request.Context(), id); err != nil {
		common.Error(c, 1001, err.Error())
		return
	}

	common.SuccessWithMessage(c, "用户删除成功", nil)
}
