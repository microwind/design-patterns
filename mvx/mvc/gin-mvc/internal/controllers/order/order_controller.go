package controllers

import (
  "fmt"
  "net/http"

  models "gin-order/internal/models/order"
  services "gin-order/internal/services/order"
  "gin-order/pkg/request"
  "gin-order/pkg/response"

  "github.com/gin-gonic/gin"
)

// OrderController 订单控制器
type OrderController struct {
  orderService services.OrderService
}

// NewOrderController 创建订单控制器实例
func NewOrderController(service services.OrderService) *OrderController {
  return &OrderController{orderService: service}
}

// RegisterRoutes 注册路由（改为实例方法）
func (oc *OrderController) RegisterRoutes(r *gin.Engine) {
  group := r.Group("/api/orders")
  {
    group.POST("", oc.CreateOrder)
    group.GET("/:orderNo", oc.GetByOrderNo)
    group.GET("/page/:orderNo", oc.GetPageByOrderNo)
    group.GET("", oc.GetAllOrders)
    group.GET("/user/:userId", oc.GetOrdersByUserID)
    group.PUT("/:orderNo", oc.UpdateOrder)
    group.PATCH("/:orderNo/status", oc.UpdateOrderStatus)
    group.DELETE("/:orderNo", oc.DeleteOrder)
  }
}

// CreateOrder 创建订单
func (oc *OrderController) CreateOrder(c *gin.Context) {
  var newOrder models.Order
  if err := c.ShouldBindJSON(&newOrder); err != nil {
    response.ErrorResponse(c, http.StatusBadRequest, err.Error())
    return
  }
  fmt.Println("newOrder:", newOrder)

  createdOrder, err := oc.orderService.CreateOrder(newOrder)
  if err != nil {
    response.ErrorResponse(c, http.StatusInternalServerError, err.Error())
    return
  }
  response.SuccessResponse(c, http.StatusCreated, createdOrder)
}

// GetOrderByID 获取单个订单
func (oc *OrderController) GetByOrderNo(c *gin.Context) {
  orderNo := c.Param("orderNo")
  order, err := oc.orderService.GetByOrderNo(orderNo)
  if err != nil {
    response.ErrorResponse(c, http.StatusNotFound, "订单未找到")
    return
  }
  response.SuccessResponse(c, http.StatusOK, order)
}

// GetPageByOrderNo 订单详情页渲染
func (oc *OrderController) GetPageByOrderNo(c *gin.Context) {
  orderNo := c.Param("orderNo")
  order, err := oc.orderService.GetByOrderNo(orderNo)

  if err != nil {
    // 错误处理改为渲染错误页（需新增模板）
    c.HTML(http.StatusNotFound, "error.tmpl", gin.H{
      "errorCode":    "404",
      "errorMessage": "订单未找到",
    })
    return
  }

  // 成功时渲染订单模板
  c.HTML(http.StatusOK, "order.tmpl", gin.H{
    "OrderID":   order.OrderID,
    "UserID":    order.UserID,
    "OrderName": order.OrderName,
    "Amount":    order.Amount,
  })
}

// GetAllOrders 获取所有订单（支持分页）
func (oc *OrderController) GetAllOrders(c *gin.Context) {
  // 获取分页参数
  pageRequest := request.GetPaginationParams(c)

  // 调用服务层获取分页后的订单数据
  orders, total, err := oc.orderService.GetAllOrders(pageRequest.Page, pageRequest.PageSize)
  if err != nil {
    response.ErrorResponse(c, http.StatusInternalServerError, err.Error())
    return
  }

  // 返回分页数据
  response.SuccessResponse(c, http.StatusOK, gin.H{
    "data":     orders,
    "total":    total,
    "page":     pageRequest.Page,
    "pageSize": pageRequest.PageSize,
  })
}

// GetOrdersByUserID 获取用户的订单（支持分页）
func (oc *OrderController) GetOrdersByUserID(c *gin.Context) {
  userId := c.Param("userId")

  // 获取分页参数
  pageRequest := request.GetPaginationParams(c)

  // 调用服务层获取分页后的订单数据
  orders, total, err := oc.orderService.GetOrdersByUserID(userId, pageRequest.Page, pageRequest.PageSize)
  if err != nil {
    response.ErrorResponse(c, http.StatusInternalServerError, err.Error())
    return
  }

  // 返回分页数据
  response.SuccessResponse(c, http.StatusOK, gin.H{
    "data":     orders,
    "total":    total,
    "page":     pageRequest.Page,
    "pageSize": pageRequest.PageSize,
  })
}

// UpdateOrder 更新订单
func (oc *OrderController) UpdateOrder(c *gin.Context) {
  orderNo := c.Param("orderNo")
  if orderNo == "" || len(orderNo) < 5 {
    response.ErrorResponse(c, http.StatusBadRequest, "Invalid orderNo")
    return
  }
  var updatedOrder models.Order
  if err := c.ShouldBindJSON(&updatedOrder); err != nil {
    response.ErrorResponse(c, http.StatusBadRequest, err.Error())
    return
  }
  updatedOrder.OrderNo = orderNo

  order, err := oc.orderService.UpdateOrder(updatedOrder)
  if err != nil {
    response.ErrorResponse(c, http.StatusInternalServerError, err.Error())
    return
  }
  response.SuccessResponse(c, http.StatusOK, order)
}

// UpdateOrderStatus 更新订单状态
func (oc *OrderController) UpdateOrderStatus(c *gin.Context) {
  orderNo := c.Param("orderNo")
  var statusUpdate struct {
    Status string `json:"status"`
  }
  if err := c.ShouldBindJSON(&statusUpdate); err != nil {
    response.ErrorResponse(c, http.StatusBadRequest, err.Error())
    return
  }

  updatedOrder, err := oc.orderService.UpdateOrderStatus(orderNo, statusUpdate.Status)
  if err != nil {
    response.ErrorResponse(c, http.StatusInternalServerError, err.Error())
    return
  }
  response.SuccessResponse(c, http.StatusOK, updatedOrder)
}

// DeleteOrder 删除订单
func (oc *OrderController) DeleteOrder(c *gin.Context) {
  orderNo := c.Param("orderNo")
  if err := oc.orderService.DeleteOrder(orderNo); err != nil {
    response.ErrorResponse(c, http.StatusInternalServerError, err.Error())
    return
  }
  response.SuccessResponse(c, http.StatusOK, gin.H{"message": "Order deleted"})
}
