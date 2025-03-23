// controllers/order_controller.go
package controllers

import (
  "fmt"
  "net/http"
  "strconv"

  models "gin-order/internal/models/order"
  services "gin-order/internal/services/order"

  "github.com/gin-gonic/gin"
)

// OrderController 订单控制器
type OrderController struct {
  orderService *services.OrderService
}

// NewOrderController 创建订单控制器实例
func NewOrderController(service *services.OrderService) *OrderController {
  return &OrderController{orderService: service}
}

// RegisterRoutes 注册路由（改为实例方法）
func (oc *OrderController) RegisterRoutes(r *gin.Engine) {
  group := r.Group("/api/orders")
  {
    group.POST("", oc.CreateOrder)
    group.GET("/:id", oc.GetOrderByID)
    group.GET("", oc.GetAllOrders)
    group.GET("/user/:userId", oc.GetOrdersByUserID)
    group.PUT("/:id", oc.UpdateOrder)
    group.PATCH("/:id/status", oc.UpdateOrderStatus)
    group.DELETE("/:id", oc.DeleteOrder)
  }
}

// CreateOrder 创建订单
func (oc *OrderController) CreateOrder(c *gin.Context) {
  var newOrder models.Order
  if err := c.ShouldBindJSON(&newOrder); err != nil {
    c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
    return
  }
  fmt.Println("newOrder:", newOrder)

  createdOrder, err := oc.orderService.CreateOrder(newOrder)
  if err != nil {
    c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
    return
  }
  c.JSON(http.StatusCreated, createdOrder)
}

// GetOrderByID 获取单个订单
func (oc *OrderController) GetOrderByID(c *gin.Context) {
  id := c.Param("id")
  order, err := oc.orderService.GetByOrderNo(id)
  if err != nil {
    c.JSON(http.StatusNotFound, gin.H{"error": "Order not found"})
    return
  }
  c.JSON(http.StatusOK, order)
}

// GetAllOrders 获取所有订单
func (oc *OrderController) GetAllOrders(c *gin.Context) {
  orders := oc.orderService.GetAllOrders()
  c.JSON(http.StatusOK, orders)
}

// GetOrdersByUserID 获取用户订单
func (oc *OrderController) GetOrdersByUserID(c *gin.Context) {
  userId := c.Param("userId")
  orders := oc.orderService.GetOrdersByUserID(userId)
  c.JSON(http.StatusOK, orders)
}

// UpdateOrder 更新订单
func (oc *OrderController) UpdateOrder(c *gin.Context) {
  id := c.Param("id")
  var updatedOrder models.Order
  if err := c.ShouldBindJSON(&updatedOrder); err != nil {
    c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
    return
  }
  orderID, err := strconv.ParseInt(id, 10, 64)
  if err != nil {
    c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid order ID format"})
    return
  }
  updatedOrder.OrderID = orderID

  order, err := oc.orderService.UpdateOrder(updatedOrder)
  if err != nil {
    c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
    return
  }
  c.JSON(http.StatusOK, order)
}

// UpdateOrderStatus 更新订单状态
func (oc *OrderController) UpdateOrderStatus(c *gin.Context) {
  id := c.Param("id")
  var statusUpdate struct {
    Status string `json:"status"`
  }
  if err := c.ShouldBindJSON(&statusUpdate); err != nil {
    c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
    return
  }

  updatedOrder, err := oc.orderService.UpdateOrderStatus(id, statusUpdate.Status)
  if err != nil {
    c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
    return
  }
  c.JSON(http.StatusOK, updatedOrder)
}

// DeleteOrder 删除订单
func (oc *OrderController) DeleteOrder(c *gin.Context) {
  id := c.Param("id")
  if err := oc.orderService.DeleteOrder(id); err != nil {
    c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
    return
  }
  c.JSON(http.StatusOK, gin.H{"message": "Order deleted"})
}
