package router

import (
	"gin-ddd/internal/interfaces/handler/order"
	"gin-ddd/internal/interfaces/handler/user"

	"github.com/gin-gonic/gin"
)

// SetupUserRoutes 配置用户相关路由
func SetupUserRoutes(api *gin.RouterGroup, userHandler *user.UserHandler, orderHandler *order.OrderHandler) {
	users := api.Group("/users")
	{
		users.POST("", userHandler.CreateUser)
		users.GET("", userHandler.GetAllUsers)
		users.GET("/:id", userHandler.GetUser)
		users.PUT("/:id/email", userHandler.UpdateEmail)
		users.PUT("/:id/phone", userHandler.UpdatePhone)
		users.PUT("/:id/activate", userHandler.ActivateUser)
		users.PUT("/:id/deactivate", userHandler.DeactivateUser)
		users.PUT("/:id/block", userHandler.BlockUser)
		users.DELETE("/:id", userHandler.DeleteUser)

		// 用户的订单
		users.GET("/:id/orders", orderHandler.GetUserOrders)
	}
}
