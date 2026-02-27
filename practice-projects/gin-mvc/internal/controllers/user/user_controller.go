package user

import (
	"net/mail"
	"strconv"
	"strings"

	usersvc "gin-mvc/internal/services/user"
	"gin-mvc/pkg/response"

	"github.com/gin-gonic/gin"
)

type Controller struct {
	service *usersvc.Service
}

func New(service *usersvc.Service) *Controller {
	return &Controller{service: service}
}

type createUserRequest struct {
	Name  string `json:"name" binding:"required,min=2,max=50"`
	Email string `json:"email" binding:"required,email"`
	Phone string `json:"phone" binding:"omitempty,min=6,max=20"`
}

type updateEmailRequest struct {
	Email string `json:"email" binding:"required,email"`
}

type updatePhoneRequest struct {
	NewPhone string `json:"new_phone" binding:"required,min=6,max=20"`
}

func (ctl *Controller) RegisterRoutes(api *gin.RouterGroup, orderController interface{ GetUserOrders(*gin.Context) }) {
	users := api.Group("/users")
	users.POST("", ctl.CreateUser)
	users.GET("", ctl.GetAllUsers)
	users.GET("/:id", ctl.GetUser)
	users.PUT("/:id/email", ctl.UpdateEmail)
	users.PUT("/:id/phone", ctl.UpdatePhone)
	users.DELETE("/:id", ctl.DeleteUser)
	users.GET("/:id/orders", orderController.GetUserOrders)
}

func (ctl *Controller) CreateUser(c *gin.Context) {
	var req createUserRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		response.BadRequest(c, err.Error())
		return
	}
	if _, err := mail.ParseAddress(req.Email); err != nil {
		response.BadRequest(c, "invalid email")
		return
	}
	u, err := ctl.service.CreateUser(c.Request.Context(), strings.TrimSpace(req.Name), strings.TrimSpace(req.Email), strings.TrimSpace(req.Phone))
	if err != nil {
		response.Error(c, 1001, err.Error())
		return
	}
	response.SuccessWithMessage(c, "用户创建成功", u)
}

func (ctl *Controller) GetUser(c *gin.Context) {
	id, err := strconv.ParseInt(c.Param("id"), 10, 64)
	if err != nil {
		response.BadRequest(c, "invalid user id")
		return
	}
	u, err := ctl.service.GetUserByID(c.Request.Context(), id)
	if err != nil {
		response.Error(c, 1001, err.Error())
		return
	}
	response.Success(c, u)
}

func (ctl *Controller) GetAllUsers(c *gin.Context) {
	users, err := ctl.service.GetAllUsers(c.Request.Context())
	if err != nil {
		response.InternalServerError(c, err.Error())
		return
	}
	response.Success(c, users)
}

func (ctl *Controller) UpdateEmail(c *gin.Context) {
	id, err := strconv.ParseInt(c.Param("id"), 10, 64)
	if err != nil {
		response.BadRequest(c, "invalid user id")
		return
	}
	var req updateEmailRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		response.BadRequest(c, err.Error())
		return
	}
	if err := ctl.service.UpdateEmail(c.Request.Context(), id, req.Email); err != nil {
		response.Error(c, 1001, err.Error())
		return
	}
	response.SuccessWithMessage(c, "邮箱更新成功", nil)
}

func (ctl *Controller) UpdatePhone(c *gin.Context) {
	id, err := strconv.ParseInt(c.Param("id"), 10, 64)
	if err != nil {
		response.BadRequest(c, "invalid user id")
		return
	}
	var req updatePhoneRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		response.BadRequest(c, err.Error())
		return
	}
	if err := ctl.service.UpdatePhone(c.Request.Context(), id, req.NewPhone); err != nil {
		response.Error(c, 1001, err.Error())
		return
	}
	response.SuccessWithMessage(c, "手机更新成功", nil)
}

func (ctl *Controller) DeleteUser(c *gin.Context) {
	id, err := strconv.ParseInt(c.Param("id"), 10, 64)
	if err != nil {
		response.BadRequest(c, "invalid user id")
		return
	}
	if err := ctl.service.DeleteUser(c.Request.Context(), id); err != nil {
		response.Error(c, 1001, err.Error())
		return
	}
	response.SuccessWithMessage(c, "用户删除成功", nil)
}
