package order

import (
	"context"
	"strconv"

	ordersvc "gin-mvc/internal/services/order"
	"gin-mvc/pkg/response"

	"github.com/gin-gonic/gin"
)

type Controller struct {
	service *ordersvc.Service
}

func New(service *ordersvc.Service) *Controller {
	return &Controller{service: service}
}

type createOrderRequest struct {
	UserID      int64   `json:"user_id" binding:"required,gt=0"`
	TotalAmount float64 `json:"total_amount" binding:"required,gt=0"`
}

func (ctl *Controller) RegisterRoutes(api *gin.RouterGroup) {
	orders := api.Group("/orders")
	orders.POST("", ctl.CreateOrder)
	orders.GET("", ctl.GetAllOrders)
	orders.GET("/:id", ctl.GetOrder)
	orders.PUT("/:id/pay", ctl.PayOrder)
	orders.PUT("/:id/ship", ctl.ShipOrder)
	orders.PUT("/:id/deliver", ctl.DeliverOrder)
	orders.PUT("/:id/cancel", ctl.CancelOrder)
	orders.PUT("/:id/refund", ctl.RefundOrder)
}

func (ctl *Controller) CreateOrder(c *gin.Context) {
	var req createOrderRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		response.BadRequest(c, err.Error())
		return
	}
	o, err := ctl.service.CreateOrder(c.Request.Context(), req.UserID, req.TotalAmount)
	if err != nil {
		response.Error(c, 2001, err.Error())
		return
	}
	response.SuccessWithMessage(c, "订单创建成功", o)
}

func (ctl *Controller) GetOrder(c *gin.Context) {
	id, err := strconv.ParseInt(c.Param("id"), 10, 64)
	if err != nil {
		response.BadRequest(c, "invalid order id")
		return
	}
	o, err := ctl.service.GetOrderByID(c.Request.Context(), id)
	if err != nil {
		response.Error(c, 2001, err.Error())
		return
	}
	response.Success(c, o)
}

func (ctl *Controller) GetAllOrders(c *gin.Context) {
	orders, err := ctl.service.GetAllOrders(c.Request.Context())
	if err != nil {
		response.InternalServerError(c, err.Error())
		return
	}
	response.Success(c, orders)
}

func (ctl *Controller) GetUserOrders(c *gin.Context) {
	id, err := strconv.ParseInt(c.Param("id"), 10, 64)
	if err != nil {
		response.BadRequest(c, "invalid user id")
		return
	}
	orders, err := ctl.service.GetUserOrders(c.Request.Context(), id)
	if err != nil {
		response.InternalServerError(c, err.Error())
		return
	}
	response.Success(c, orders)
}

func (ctl *Controller) PayOrder(c *gin.Context) {
	ctl.updateStatusAction(c, ctl.service.PayOrder, "订单支付成功")
}

func (ctl *Controller) ShipOrder(c *gin.Context) {
	ctl.updateStatusAction(c, ctl.service.ShipOrder, "订单发货成功")
}

func (ctl *Controller) DeliverOrder(c *gin.Context) {
	ctl.updateStatusAction(c, ctl.service.DeliverOrder, "订单送达确认成功")
}

func (ctl *Controller) CancelOrder(c *gin.Context) {
	ctl.updateStatusAction(c, ctl.service.CancelOrder, "订单取消成功")
}

func (ctl *Controller) RefundOrder(c *gin.Context) {
	ctl.updateStatusAction(c, ctl.service.RefundOrder, "订单退款成功")
}

func (ctl *Controller) updateStatusAction(c *gin.Context, fn func(ctx context.Context, id int64) error, successMsg string) {
	id, err := strconv.ParseInt(c.Param("id"), 10, 64)
	if err != nil {
		response.BadRequest(c, "invalid order id")
		return
	}
	if err := fn(c.Request.Context(), id); err != nil {
		response.Error(c, 2002, err.Error())
		return
	}
	response.SuccessWithMessage(c, successMsg, nil)
}
