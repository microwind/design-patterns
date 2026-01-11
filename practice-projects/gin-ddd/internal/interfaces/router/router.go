package router

import (
	"gin-ddd/internal/infrastructure/middleware"
	orderHandler "gin-ddd/internal/interfaces/handler/order"
	userHandler "gin-ddd/internal/interfaces/handler/user"

	"github.com/gin-gonic/gin"
)

// Router 路由配置
type Router struct {
	userHandler  *userHandler.UserHandler
	orderHandler *orderHandler.OrderHandler
}

// NewRouter 创建路由
func NewRouter(userHandler *userHandler.UserHandler, orderHandler *orderHandler.OrderHandler) *Router {
	return &Router{
		userHandler:  userHandler,
		orderHandler: orderHandler,
	}
}

// Setup 配置路由
func (r *Router) Setup(mode string) *gin.Engine {
	// 设置 Gin 模式
	gin.SetMode(mode)

	engine := gin.New()

	// 使用中间件
	engine.Use(middleware.Logger())
	engine.Use(middleware.Recovery())
	engine.Use(middleware.CORS())

	// 健康检查
	engine.GET("/health", func(c *gin.Context) {
		c.JSON(200, gin.H{
			"status": "ok",
			"message": "Gin DDD service is running",
		})
	})

	// API 路由组
	api := engine.Group("/api")
	{
		// 用户路由
		users := api.Group("/users")
		{
			users.POST("", r.userHandler.CreateUser)
			users.GET("", r.userHandler.GetAllUsers)
			users.GET("/:id", r.userHandler.GetUser)
			users.PUT("/:id/email", r.userHandler.UpdateEmail)
			users.PUT("/:id/password", r.userHandler.UpdatePassword)
			users.PUT("/:id/activate", r.userHandler.ActivateUser)
			users.PUT("/:id/deactivate", r.userHandler.DeactivateUser)
			users.PUT("/:id/block", r.userHandler.BlockUser)
			users.DELETE("/:id", r.userHandler.DeleteUser)

			// 用户的订单
			users.GET("/:user_id/orders", r.orderHandler.GetUserOrders)
		}

		// 订单路由
		orders := api.Group("/orders")
		{
			orders.POST("", r.orderHandler.CreateOrder)
			orders.GET("", r.orderHandler.GetAllOrders)
			orders.GET("/:id", r.orderHandler.GetOrder)
			orders.PUT("/:id/pay", r.orderHandler.PayOrder)
			orders.PUT("/:id/ship", r.orderHandler.ShipOrder)
			orders.PUT("/:id/deliver", r.orderHandler.DeliverOrder)
			orders.PUT("/:id/cancel", r.orderHandler.CancelOrder)
			orders.PUT("/:id/refund", r.orderHandler.RefundOrder)
		}
	}

	return engine
}
