package order

import (
	"gin-ddd/internal/application/service/order"
	"gin-ddd/internal/infrastructure/common"
	orderVO "gin-ddd/internal/interfaces/vo/order"
	"gin-ddd/pkg/utils"
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
	var req orderVO.CreateOrderRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		common.BadRequest(c, err.Error())
		return
	}

	utils.GetLogger().Info("CreateOrder request: user_id=%d total_amount=%.2f", req.UserID, req.TotalAmount)

	orderDTO, err := h.orderService.CreateOrder(c.Request.Context(), req.UserID, req.TotalAmount)
	if err != nil {
		utils.GetLogger().Error("CreateOrder failed: %v", err)
		common.Error(c, 2001, err.Error())
		return
	}

	utils.GetLogger().Info("CreateOrder success: order_id=%d, user_id=%d", orderDTO.OrderID, orderDTO.UserID)
	common.SuccessWithMessage(c, "订单创建成功", orderVO.FromOrderDTO(orderDTO))
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

	utils.GetLogger().Info("GetOrder request: id=%d", id)

	orderDTO, err := h.orderService.GetOrderByID(c.Request.Context(), id)
	if err != nil {
		utils.GetLogger().Error("GetOrder failed: %v", err)
		common.Error(c, 2001, err.Error())
		return
	}

	utils.GetLogger().Info("GetOrder success: id=%d, status=%s", id, orderDTO.Status)
	common.Success(c, orderVO.FromOrderDTO(orderDTO))
}

// GetAllOrders 获取所有订单
// @Summary 获取所有订单
// @Tags 订单管理
// @Produce json
// @Success 200 {object} common.Response
// @Router /api/orders [get]
func (h *OrderHandler) GetAllOrders(c *gin.Context) {
	utils.GetLogger().Info("GetAllOrders request")
	orders, err := h.orderService.GetAllOrders(c.Request.Context())
	if err != nil {
		utils.GetLogger().Error("GetAllOrders failed: %v", err)
		common.InternalServerError(c, err.Error())
		return
	}

	utils.GetLogger().Info("GetAllOrders success: total_count=%d", len(orders))
	common.Success(c, orderVO.FromOrderDTOs(orders))
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

	utils.GetLogger().Info("GetUserOrders request: user_id=%d", userID)

	orders, err := h.orderService.GetUserOrders(c.Request.Context(), userID)
	if err != nil {
		utils.GetLogger().Error("GetUserOrders failed: %v", err)
		common.InternalServerError(c, err.Error())
		return
	}

	utils.GetLogger().Info("GetUserOrders success: user_id=%d, order_count=%d", userID, len(orders))
	common.Success(c, orderVO.FromOrderDTOs(orders))
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

	utils.GetLogger().Info("PayOrder request: id=%d", id)

	if err := h.orderService.PayOrder(c.Request.Context(), id); err != nil {
		utils.GetLogger().Error("PayOrder failed: %v", err)
		common.Error(c, 2002, err.Error())
		return
	}

	utils.GetLogger().Info("PayOrder success: id=%d", id)
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

	utils.GetLogger().Info("ShipOrder request: id=%d", id)

	if err := h.orderService.ShipOrder(c.Request.Context(), id); err != nil {
		utils.GetLogger().Error("ShipOrder failed: %v", err)
		common.Error(c, 2002, err.Error())
		return
	}

	utils.GetLogger().Info("ShipOrder success: id=%d", id)
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

	utils.GetLogger().Info("DeliverOrder request: id=%d", id)

	if err := h.orderService.DeliverOrder(c.Request.Context(), id); err != nil {
		utils.GetLogger().Error("DeliverOrder failed: %v", err)
		common.Error(c, 2002, err.Error())
		return
	}

	utils.GetLogger().Info("DeliverOrder success: id=%d", id)
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

	utils.GetLogger().Info("CancelOrder request: id=%d", id)

	if err := h.orderService.CancelOrder(c.Request.Context(), id); err != nil {
		utils.GetLogger().Error("CancelOrder failed: %v", err)
		common.Error(c, 2003, err.Error())
		return
	}

	utils.GetLogger().Info("CancelOrder success: id=%d", id)
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

	utils.GetLogger().Info("RefundOrder request: id=%d", id)

	if err := h.orderService.RefundOrder(c.Request.Context(), id); err != nil {
		utils.GetLogger().Error("RefundOrder failed: %v", err)
		common.Error(c, 2002, err.Error())
		return
	}

	utils.GetLogger().Info("RefundOrder success: id=%d", id)
	common.SuccessWithMessage(c, "订单退款成功", nil)
}
