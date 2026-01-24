package user

import (
	"gin-ddd/internal/application/service/user"
	"gin-ddd/internal/infrastructure/common"
	userVO "gin-ddd/internal/interfaces/vo/user"
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
	var req userVO.CreateUserRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		common.BadRequest(c, err.Error())
		return
	}

	userDTO, err := h.userService.CreateUser(c.Request.Context(), req.Name, req.Email, req.Phone, req.Phone)
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

	var req userVO.UpdateUserRequest
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

// UpdatePhone 更新手机
// @Summary 更新用户手机
// @Tags 用户管理
// @Accept json
// @Produce json
// @Param id path int true "用户ID"
// @Param body body user.UpdatePhoneRequest true "手机信息"
// @Success 200 {object} common.Response
// @Router /api/users/{id}/Phone [put]
func (h *UserHandler) UpdatePhone(c *gin.Context) {
	id, err := strconv.ParseInt(c.Param("id"), 10, 64)
	if err != nil {
		common.BadRequest(c, "无效的用户ID")
		return
	}

	var req userVO.UpdatePhoneRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		common.BadRequest(c, err.Error())
		return
	}

	if err := h.userService.UpdatePhone(c.Request.Context(), id, req.NewPhone); err != nil {
		common.Error(c, 1001, err.Error())
		return
	}

	common.SuccessWithMessage(c, "手机更新成功", nil)
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
