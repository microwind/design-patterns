// 接口层（Interfaces）：订单 Routes 设置
package routes

import (
  "go-web-order/internal/interfaces/handlers"
  "go-web-order/internal/middleware"
)

// SetupOrderRoutes 设置订单相关的路由
func SetupOrderRoutes(router *Router, orderHandler *handlers.OrderHandler) {
  router.Post("/orders", middleware.LoggingMiddleware(orderHandler.CreateOrder))
  router.Get("/orders", middleware.LoggingMiddleware(orderHandler.GetAllOrders))
  router.Get("/orders/:id", middleware.LoggingMiddleware(orderHandler.GetOrder))
  router.Put("/orders/:id", middleware.LoggingMiddleware(orderHandler.UpdateOrder))
  router.Delete("/orders/:id", middleware.LoggingMiddleware(orderHandler.DeleteOrder))
}
