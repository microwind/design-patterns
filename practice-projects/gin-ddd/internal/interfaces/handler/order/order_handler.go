package order

import (
	"gin-ddd/internal/application/service/order"
	"gin-ddd/internal/infrastructure/common"
	"gin-ddd/internal/interfaces/vo/order"
	"strconv"

	"github.com/gin-gonic/gin"
)

// OrderHandler 订单处理器
type OrderHandler struct {
	orderService *order.OrderService
}

// NewOrderHandler 创建订单处理器
func NewOrderHandler(orderService *order.OrderService) *OrderHandler {
	return &OrderHandler{
		orderService: orderService,
	}
}

// CreateOrder 创建订单
// @Summary 创建订单
// @Tags 订单管理
// @Accept json
// @Produce json
// @Param body body order.CreateOrderRequest true "订单信息"
// @Success 200 {object} common.Response
// @Router /api/orders [post]
func (h *OrderHandler) CreateOrder(c *gin.Context) {
	var req order.CreateOrderRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		common.BadRequest(c, err.Error())
		return
	}

	orderDTO, err := h.orderService.CreateOrder(c.Request.Context(), req.UserID, req.ToOrderItems())
	if err != nil {
		common.Error(c, 2001, err.Error())
		return
	}

	common.SuccessWithMessage(c, "订单创建成功", orderDTO)
}

// GetOrder 获取订单
// @Summary 获取订单
// @Tags 订单管理
// @Produce json
// @Param id path int true "订单ID"
// @Success 200 {object} common.Response
// @Router /api/orders/{id} [get]
func (h *OrderHandler) GetOrder(c *gin.Context) {
	id, err := strconv.ParseInt(c.Param("id"), 10, 64)
	if err != nil {
		common.BadRequest(c, "无效的订单ID")
		return
	}

	orderDTO, err := h.orderService.GetOrderByID(c.Request.Context(), id)
	if err != nil {
		common.Error(c, 2001, err.Error())
		return
	}

	common.Success(c, orderDTO)
}

// GetAllOrders 获取所有订单
// @Summary 获取所有订单
// @Tags 订单管理
// @Produce json
// @Success 200 {object} common.Response
// @Router /api/orders [get]
func (h *OrderHandler) GetAllOrders(c *gin.Context) {
	orders, err := h.orderService.GetAllOrders(c.Request.Context())
	if err != nil {
		common.InternalServerError(c, err.Error())
		return
	}

	common.Success(c, orders)
}

// GetUserOrders 获取用户订单
// @Summary 获取用户订单
// @Tags 订单管理
// @Produce json
// @Param user_id path int true "用户ID"
// @Success 200 {object} common.Response
// @Router /api/users/{user_id}/orders [get]
func (h *OrderHandler) GetUserOrders(c *gin.Context) {
	userID, err := strconv.ParseInt(c.Param("user_id"), 10, 64)
	if err != nil {
		common.BadRequest(c, "无效的用户ID")
		return
	}

	orders, err := h.orderService.GetUserOrders(c.Request.Context(), userID)
	if err != nil {
		common.InternalServerError(c, err.Error())
		return
	}

	common.Success(c, orders)
}

// PayOrder 支付订单
// @Summary 支付订单
// @Tags 订单管理
// @Produce json
// @Param id path int true "订单ID"
// @Success 200 {object} common.Response
// @Router /api/orders/{id}/pay [put]
func (h *OrderHandler) PayOrder(c *gin.Context) {
	id, err := strconv.ParseInt(c.Param("id"), 10, 64)
	if err != nil {
		common.BadRequest(c, "无效的订单ID")
		return
	}

	if err := h.orderService.PayOrder(c.Request.Context(), id); err != nil {
		common.Error(c, 2002, err.Error())
		return
	}

	common.SuccessWithMessage(c, "订单支付成功", nil)
}

// ShipOrder 发货
// @Summary 订单发货
// @Tags 订单管理
// @Produce json
// @Param id path int true "订单ID"
// @Success 200 {object} common.Response
// @Router /api/orders/{id}/ship [put]
func (h *OrderHandler) ShipOrder(c *gin.Context) {
	id, err := strconv.ParseInt(c.Param("id"), 10, 64)
	if err != nil {
		common.BadRequest(c, "无效的订单ID")
		return
	}

	if err := h.orderService.ShipOrder(c.Request.Context(), id); err != nil {
		common.Error(c, 2002, err.Error())
		return
	}

	common.SuccessWithMessage(c, "订单发货成功", nil)
}

// DeliverOrder 确认送达
// @Summary 确认订单送达
// @Tags 订单管理
// @Produce json
// @Param id path int true "订单ID"
// @Success 200 {object} common.Response
// @Router /api/orders/{id}/deliver [put]
func (h *OrderHandler) DeliverOrder(c *gin.Context) {
	id, err := strconv.ParseInt(c.Param("id"), 10, 64)
	if err != nil {
		common.BadRequest(c, "无效的订单ID")
		return
	}

	if err := h.orderService.DeliverOrder(c.Request.Context(), id); err != nil {
		common.Error(c, 2002, err.Error())
		return
	}

	common.SuccessWithMessage(c, "订单送达确认成功", nil)
}

// CancelOrder 取消订单
// @Summary 取消订单
// @Tags 订单管理
// @Produce json
// @Param id path int true "订单ID"
// @Success 200 {object} common.Response
// @Router /api/orders/{id}/cancel [put]
func (h *OrderHandler) CancelOrder(c *gin.Context) {
	id, err := strconv.ParseInt(c.Param("id"), 10, 64)
	if err != nil {
		common.BadRequest(c, "无效的订单ID")
		return
	}

	if err := h.orderService.CancelOrder(c.Request.Context(), id); err != nil {
		common.Error(c, 2003, err.Error())
		return
	}

	common.SuccessWithMessage(c, "订单取消成功", nil)
}

// RefundOrder 退款
// @Summary 订单退款
// @Tags 订单管理
// @Produce json
// @Param id path int true "订单ID"
// @Success 200 {object} common.Response
// @Router /api/orders/{id}/refund [put]
func (h *OrderHandler) RefundOrder(c *gin.Context) {
	id, err := strconv.ParseInt(c.Param("id"), 10, 64)
	if err != nil {
		common.BadRequest(c, "无效的订单ID")
		return
	}

	if err := h.orderService.RefundOrder(c.Request.Context(), id); err != nil {
		common.Error(c, 2002, err.Error())
		return
	}

	common.SuccessWithMessage(c, "订单退款成功", nil)
}
