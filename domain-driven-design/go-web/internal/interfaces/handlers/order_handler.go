// 接口层（Interfaces）：订单 HTTP 处理器
// src/interfaces/handlers/order_handler.go

package handlers

import (
  "encoding/json"
  "fmt"
  "go-web-order/internal/application/services"
  "go-web-order/pkg/utils"
  "log"
  "math/rand"
  "net/http"
  "strconv"
  "strings"
)

// 订单 HTTP 处理器
type OrderHandler struct {
  Service *services.OrderService
}

// 构造函数
func NewOrderHandler(service *services.OrderService) *OrderHandler {
  return &OrderHandler{Service: service}
}

// getOrderIDFromPath 从请求路径中获取订单 ID
func getOrderIDFromPath(r *http.Request) (int, error) {
  parts := strings.Split(r.URL.Path, "/")
  if len(parts) < 3 {
    return 0, fmt.Errorf("无效的请求路径: %s", r.URL.Path)
  }
  idStr := parts[2]
  id, err := strconv.Atoi(idStr)
  if err != nil || id <= 0 {
    return 0, fmt.Errorf("无效的订单 ID: %s", idStr)
  }
  return id, nil
}

// 创建订单
func (h *OrderHandler) CreateOrder(w http.ResponseWriter, r *http.Request) {
  var orderRequest struct {
    CustomerName string  `json:"customer_name"`
    TotalAmount  float64 `json:"total_amount"`
  }

  // 解析请求体中的 JSON 数据
  if err := json.NewDecoder(r.Body).Decode(&orderRequest); err != nil {
    log.Printf("解析请求体失败: %v", err)
    utils.SendError(w, http.StatusBadRequest, "无效的请求体", "application/json", nil)
    return
  }

  // 验证参数
  if orderRequest.CustomerName == "" {
    log.Println("客户名称不能为空")
    utils.SendError(w, http.StatusBadRequest, "客户名称不能为空", "application/json", nil)
    return
  }

  if orderRequest.TotalAmount <= 0 {
    log.Println("订单金额必须大于 0")
    utils.SendError(w, http.StatusBadRequest, "订单金额必须大于 0", "application/json", nil)
    return
  }

  // 创建订单
  orderID := rand.Intn(1000000)
  newOrder, err := h.Service.CreateOrder(orderID, orderRequest.CustomerName, orderRequest.TotalAmount)
  if err != nil {
    log.Printf("创建订单失败: %v", err)
    utils.SendError(w, http.StatusBadRequest, err.Error(), "application/json", nil)
    return
  }

  // 返回成功响应
  utils.SendResponse(w, http.StatusCreated, newOrder, "application/json", nil)
}

// 查询订单
func (h *OrderHandler) GetOrder(w http.ResponseWriter, r *http.Request) {
  id, err := getOrderIDFromPath(r)
  if err != nil {
    log.Printf("获取订单 ID 失败: %v", err)
    utils.SendError(w, http.StatusBadRequest, err.Error(), "application/json", nil)
    return
  }

  order, err := h.Service.GetOrder(id)
  if err != nil {
    log.Printf("查询订单失败: %v", err)
    utils.SendError(w, http.StatusNotFound, err.Error(), "application/json", nil)
    return
  }

  utils.SendResponse(w, http.StatusOK, order, "application/json", nil)
}

// 更新订单
func (h *OrderHandler) UpdateOrder(w http.ResponseWriter, r *http.Request) {
  id, err := getOrderIDFromPath(r)
  if err != nil {
    log.Printf("获取订单 ID 失败: %v", err)
    utils.SendError(w, http.StatusBadRequest, err.Error(), "application/json", nil)
    return
  }

  var updatedOrder struct {
    CustomerName string  `json:"customer_name"`
    TotalAmount  float64 `json:"total_amount"`
  }

  if err := json.NewDecoder(r.Body).Decode(&updatedOrder); err != nil {
    log.Printf("解析请求体失败: %v", err)
    utils.SendError(w, http.StatusBadRequest, "无效的请求体", "application/json", nil)
    return
  }

  order, err := h.Service.UpdateOrder(id, updatedOrder.CustomerName, updatedOrder.TotalAmount)
  if err != nil {
    log.Printf("更新订单失败: %v", err)
    utils.SendError(w, http.StatusInternalServerError, err.Error(), "application/json", nil)
    return
  }

  utils.SendResponse(w, http.StatusOK, order, "application/json", nil)
}

// 删除订单
func (h *OrderHandler) DeleteOrder(w http.ResponseWriter, r *http.Request) {
  id, err := getOrderIDFromPath(r)
  if err != nil {
    log.Printf("获取订单 ID 失败: %v", err)
    utils.SendError(w, http.StatusBadRequest, err.Error(), "application/json", nil)
    return
  }

  err = h.Service.DeleteOrder(id)
  if err != nil {
    log.Printf("删除订单失败: %v", err)
    response := map[string]interface{}{
      "message":  fmt.Sprintf("删除订单失败: %v", err),
      "order_id": id,
    }
    utils.SendResponse(w, http.StatusInternalServerError, response, "application/json", nil)
    return
  }

  response := map[string]interface{}{
    "message":  "success",
    "order_id": id,
  }
  utils.SendResponse(w, http.StatusOK, response, "application/json", nil)
}
