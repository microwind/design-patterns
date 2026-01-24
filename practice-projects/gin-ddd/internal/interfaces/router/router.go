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

	// 配置受信任的代理
	engine.SetTrustedProxies([]string{"127.0.0.1", "::1"})

	// 使用中间件
	engine.Use(middleware.Logger())
	engine.Use(middleware.Recovery())
	engine.Use(middleware.CORS())

	// 健康检查
	engine.GET("/health", func(c *gin.Context) {
		c.JSON(200, gin.H{
			"status":  "ok",
			"message": "Gin DDD service is running",
		})
	})

	// API 路由组
	api := engine.Group("/api")
	{
		// 设置用户路由
		SetupUserRoutes(api, r.userHandler, r.orderHandler)

		// 设置订单路由
		SetupOrderRoutes(api, r.orderHandler)
	}

	return engine
}
