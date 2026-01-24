package router

import (
	"gin-ddd/internal/interfaces/handler/order"

	"github.com/gin-gonic/gin"
)

// SetupOrderRoutes 配置订单相关路由
func SetupOrderRoutes(api *gin.RouterGroup, orderHandler *order.OrderHandler) {
	orders := api.Group("/orders")
	{
		orders.POST("", orderHandler.CreateOrder)
		orders.GET("", orderHandler.GetAllOrders)
		orders.GET("/:id", orderHandler.GetOrder)
		orders.PUT("/:id/pay", orderHandler.PayOrder)
		orders.PUT("/:id/ship", orderHandler.ShipOrder)
		orders.PUT("/:id/deliver", orderHandler.DeliverOrder)
		orders.PUT("/:id/cancel", orderHandler.CancelOrder)
		orders.PUT("/:id/refund", orderHandler.RefundOrder)
	}
}