// 接口层（Interfaces）：订单 HTTP 处理器
package handlers

import (
  "encoding/json"
  "go-order-system/internal/application"
  "math/rand"
  "net/http"
  "strconv"
)

// 订单 HTTP 处理器
type OrderHandler struct {
  Service *application.OrderService
}

// 构造函数
func NewOrderHandler(service *application.OrderService) *OrderHandler {
  return &OrderHandler{Service: service}
}

// 创建订单
func (h *OrderHandler) CreateOrder(w http.ResponseWriter, r *http.Request) {
  // 设置接收订单的结构体
  var orderRequest struct {
    CustomerName string  `json:"customer_name"`
    TotalAmount  float64 `json:"total_amount"`
  }

  // 解析请求体中的 JSON 数据
  if err := json.NewDecoder(r.Body).Decode(&orderRequest); err != nil {
    http.Error(w, "无效的请求体", http.StatusBadRequest)
    return
  }

  // 验证参数
  if orderRequest.CustomerName == "" {
    http.Error(w, "客户名称不能为空", http.StatusBadRequest)
    return
  }

  if orderRequest.TotalAmount <= 0 {
    http.Error(w, "订单金额必须大于 0", http.StatusBadRequest)
    return
  }

  // 创建订单
  orderID := rand.Intn(1000000)
  newOrder, err := h.Service.CreateOrder(orderID, orderRequest.CustomerName, orderRequest.TotalAmount)
  if err != nil {
    http.Error(w, err.Error(), http.StatusBadRequest)
    return
  }

  // 设置响应头和返回数据
  w.Header().Set("Content-Type", "application/json")
  w.WriteHeader(http.StatusCreated)
  json.NewEncoder(w).Encode(newOrder)
}

// 查询订单
func (h *OrderHandler) GetOrder(w http.ResponseWriter, r *http.Request) {
  idStr := r.URL.Query().Get("id")
  id, err := strconv.Atoi(idStr)
  if err != nil || id <= 0 {
    http.Error(w, "无效的订单ID", http.StatusBadRequest)
    return
  }

  order, err := h.Service.GetOrder(id)
  if err != nil {
    http.Error(w, err.Error(), http.StatusNotFound)
    return
  }

  w.Header().Set("Content-Type", "application/json")
  json.NewEncoder(w).Encode(order)
}

// 更新订单
func (h *OrderHandler) UpdateOrder(w http.ResponseWriter, r *http.Request) {
  idStr := r.URL.Query().Get("id")
  id, err := strconv.Atoi(idStr)
  if err != nil || id <= 0 {
    http.Error(w, "无效的订单ID", http.StatusBadRequest)
    return
  }

  var updatedOrder struct {
    CustomerName string  `json:"customer_name"`
    TotalAmount  float64 `json:"total_amount"`
  }

  if err := json.NewDecoder(r.Body).Decode(&updatedOrder); err != nil {
    http.Error(w, "无效的请求体", http.StatusBadRequest)
    return
  }

  order, err := h.Service.UpdateOrder(id, updatedOrder.CustomerName, updatedOrder.TotalAmount)
  if err != nil {
    http.Error(w, err.Error(), http.StatusInternalServerError)
    return
  }

  w.Header().Set("Content-Type", "application/json")
  json.NewEncoder(w).Encode(order)
}

// 删除订单
func (h *OrderHandler) DeleteOrder(w http.ResponseWriter, r *http.Request) {
  idStr := r.URL.Query().Get("id")
  id, err := strconv.Atoi(idStr)
  if err != nil || id <= 0 {
    http.Error(w, "无效的订单ID", http.StatusBadRequest)
    return
  }

  err = h.Service.DeleteOrder(id)
  if err != nil {
    http.Error(w, err.Error(), http.StatusInternalServerError)
    return
  }

  // 返回删除的订单ID
  response := map[string]int{
    "deleted_order_id": id,
  }
  w.Header().Set("Content-Type", "application/json")
  w.WriteHeader(http.StatusOK)
  json.NewEncoder(w).Encode(response)
}
