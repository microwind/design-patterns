package user

import (
	"gin-ddd/internal/application/service/user"
	"gin-ddd/internal/infrastructure/common"
	userVO "gin-ddd/internal/interfaces/vo/user"
	"gin-ddd/pkg/utils"
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
		utils.GetLogger().Error("创建用户时绑定JSON失败: %v", err)
		common.BadRequest(c, err.Error())
		return
	}

	utils.GetLogger().Info("开始创建用户: Name=%s, Email=%s, Phone=%s", req.Name, req.Email, req.Phone)
	userDTO, err := h.userService.CreateUser(c.Request.Context(), req.Name, req.Email, req.Phone, req.Address)
	if err != nil {
		utils.GetLogger().Error("创建用户失败: %v, 请求信息: Name=%s, Email=%s", err, req.Name, req.Email)
		common.Error(c, 1001, err.Error())
		return
	}

	utils.GetLogger().Info("用户创建成功: UserID=%d, Email=%s", userDTO.ID, userDTO.Email)
	common.SuccessWithMessage(c, "用户创建成功", userVO.FromUserDTO(userDTO))
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
		utils.GetLogger().Error("解析用户ID失败: %v, 参数: %s", err, c.Param("id"))
		common.BadRequest(c, "无效的用户ID")
		return
	}

	utils.GetLogger().Info("开始查询用户: UserID=%d", id)
	userDTO, err := h.userService.GetUserByID(c.Request.Context(), id)
	if err != nil {
		utils.GetLogger().Error("查询用户失败: %v, UserID=%d", err, id)
		common.Error(c, 1001, err.Error())
		return
	}

	utils.GetLogger().Info("查询用户成功: UserID=%d, Email=%s", userDTO.ID, userDTO.Email)
	common.Success(c, userVO.FromUserDTO(userDTO))
}

// GetAllUsers 获取所有用户
// @Summary 获取所有用户
// @Tags 用户管理
// @Produce json
// @Success 200 {object} common.Response
// @Router /api/users [get]
func (h *UserHandler) GetAllUsers(c *gin.Context) {
	utils.GetLogger().Info("开始查询所有用户")
	users, err := h.userService.GetAllUsers(c.Request.Context())
	if err != nil {
		utils.GetLogger().Error("查询所有用户失败: %v", err)
		common.InternalServerError(c, err.Error())
		return
	}

	utils.GetLogger().Info("查询所有用户成功: 共%d条记录", len(users))
	common.Success(c, userVO.FromUserDTOs(users))
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
		utils.GetLogger().Error("解析用户ID失败: %v, 参数: %s", err, c.Param("id"))
		common.BadRequest(c, "无效的用户ID")
		return
	}

	var req userVO.UpdateEmailRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		utils.GetLogger().Error("更新邮箱时绑定JSON失败: %v", err)
		common.BadRequest(c, err.Error())
		return
	}

	utils.GetLogger().Info("开始更新用户邮箱: UserID=%d, NewEmail=%s", id, req.Email)
	if err := h.userService.UpdateEmail(c.Request.Context(), id, req.Email); err != nil {
		utils.GetLogger().Error("更新用户邮箱失败: %v, UserID=%d, NewEmail=%s", err, id, req.Email)
		common.Error(c, 1001, err.Error())
		return
	}

	utils.GetLogger().Info("用户邮箱更新成功: UserID=%d", id)
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
		utils.GetLogger().Error("解析用户ID失败: %v, 参数: %s", err, c.Param("id"))
		common.BadRequest(c, "无效的用户ID")
		return
	}

	var req userVO.UpdatePhoneRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		utils.GetLogger().Error("更新手机号时绑定JSON失败: %v", err)
		common.BadRequest(c, err.Error())
		return
	}

	utils.GetLogger().Info("开始更新用户手机号: UserID=%d, NewPhone=%s", id, req.NewPhone)
	if err := h.userService.UpdatePhone(c.Request.Context(), id, req.NewPhone); err != nil {
		utils.GetLogger().Error("更新用户手机号失败: %v, UserID=%d, NewPhone=%s", err, id, req.NewPhone)
		common.Error(c, 1001, err.Error())
		return
	}

	utils.GetLogger().Info("用户手机号更新成功: UserID=%d", id)
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
		utils.GetLogger().Error("解析用户ID失败: %v, 参数: %s", err, c.Param("id"))
		common.BadRequest(c, "无效的用户ID")
		return
	}

	utils.GetLogger().Info("开始删除用户: UserID=%d", id)
	if err := h.userService.DeleteUser(c.Request.Context(), id); err != nil {
		utils.GetLogger().Error("删除用户失败: %v, UserID=%d", err, id)
		common.Error(c, 1001, err.Error())
		return
	}

	utils.GetLogger().Info("用户删除成功: UserID=%d", id)
	common.SuccessWithMessage(c, "用户删除成功", nil)
}
