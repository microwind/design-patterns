// 路由测试文件，go语言下一般与源码放在一起
package routes

import (
  "encoding/json"
  "go-web-order/internal/application/services"
  "go-web-order/internal/domain/order"
  "go-web-order/internal/interfaces/handlers"
  "net/http"
  "net/http/httptest"
  "strconv"
  "strings"
  "testing"
)

// mockOrderRepository 模拟订单仓储
type mockOrderRepository struct {
  orders map[int]*order.Order
}

// Save 保存订单
func (m *mockOrderRepository) Save(order *order.Order) error {
  m.orders[order.ID] = order
  return nil
}

// FindByID 根据 ID 查找订单
func (m *mockOrderRepository) FindByID(id int) (*order.Order, error) {
  order, exists := m.orders[id]
  if !exists {
    return nil, nil
  }
  return order, nil
}

// FindAll 查找所有订单
func (m *mockOrderRepository) FindAll() ([]*order.Order, error) {
  var result []*order.Order
  for _, order := range m.orders {
    result = append(result, order)
  }
  return result, nil
}

// FindByCustomerName 根据客户名称查找订单
func (m *mockOrderRepository) FindByCustomerName(customerName string) ([]*order.Order, error) {
  var result []*order.Order
  for _, order := range m.orders {
    if order.CustomerName == customerName {
      result = append(result, order)
    }
  }
  if len(result) == 0 {
    return nil, nil
  }
  return result, nil
}

// Delete 删除订单
func (m *mockOrderRepository) Delete(id int) error {
  delete(m.orders, id)
  return nil
}

// mockOrderService 模拟订单服务，直接使用 services.OrderService 类型
type mockOrderService struct {
  services.OrderService
}

func TestSetupOrderRoutes(t *testing.T) {
  // 创建模拟的 OrderRepository
  mockRepo := &mockOrderRepository{
    orders: make(map[int]*order.Order),
  }

  // 创建模拟的 OrderService
  mockService := &mockOrderService{
    OrderService: *services.NewOrderService(mockRepo),
  }

  // 创建路由管理器
  router := NewRouter()

  // 创建模拟的 OrderHandler
  orderHandler := handlers.NewOrderHandler(&mockService.OrderService)

  // 设置订单路由
  SetupOrderRoutes(router, orderHandler)

  // 测试创建订单路由
  createOrderReqBody := map[string]interface{}{
    "customer_name": "Test Customer",
    "total_amount":  100.0,
  }
  createOrderReqJSON, err := json.Marshal(createOrderReqBody)
  if err != nil {
    t.Fatalf("Failed to marshal create order request body: %v", err)
  }
  createOrderReq, err := http.NewRequest("POST", "/orders", strings.NewReader(string(createOrderReqJSON)))
  if err != nil {
    t.Fatalf("Failed to create create order request: %v", err)
  }
  createOrderReq.Header.Set("Content-Type", "application/json")
  createOrderRR := httptest.NewRecorder()
  router.ServeHTTP(createOrderRR, createOrderReq)
  if status := createOrderRR.Code; status != http.StatusCreated {
    t.Errorf("Create order handler returned wrong status code: got %v want %v",
      status, http.StatusCreated)
  }

  // 验证创建订单的响应内容
  var createOrderResp map[string]interface{}
  err = json.NewDecoder(createOrderRR.Body).Decode(&createOrderResp)
  if err != nil {
    t.Fatalf("Failed to decode create order response body: %v", err)
  }

  // 尝试获取订单 ID
  idValue, ok := createOrderResp["ID"]
  if !ok {
    t.Fatalf("Create order response does not contain 'ID' field")
  }
  orderID, ok := idValue.(float64)
  if !ok {
    t.Fatalf("Expected 'ID' to be a float64, but got %T", idValue)
  }
  orderIDInt := int(orderID)
  orderIDStr := strconv.Itoa(orderIDInt)

  if createOrderResp["ID"] == nil || createOrderResp["CustomerName"] == nil || createOrderResp["Amount"] == nil {
    t.Errorf("Create order response body is missing required fields")
  }

  // 测试获取订单路由
  getOrderPath := "/orders/" + orderIDStr
  getOrderReq, err := http.NewRequest("GET", getOrderPath, nil)
  if err != nil {
    t.Fatalf("Failed to create get order request: %v", err)
  }
  getOrderRR := httptest.NewRecorder()
  router.ServeHTTP(getOrderRR, getOrderReq)
  if status := getOrderRR.Code; status != http.StatusOK {
    t.Errorf("Get order handler returned wrong status code: got %v want %v",
      status, http.StatusOK)
  }

  // 验证获取订单的响应内容
  var getOrderResp map[string]interface{}
  err = json.NewDecoder(getOrderRR.Body).Decode(&getOrderResp)
  if err != nil {
    t.Fatalf("Failed to decode get order response body: %v", err)
  }
  if getOrderResp["ID"] == nil || getOrderResp["CustomerName"] == nil || getOrderResp["Amount"] == nil {
    t.Errorf("Get order response body is missing required fields")
  }

  // 测试更新订单路由
  updateOrderReqBody := map[string]interface{}{
    "customer_name": "Updated Customer",
    "total_amount":  200.0,
  }
  updateOrderReqJSON, err := json.Marshal(updateOrderReqBody)
  if err != nil {
    t.Fatalf("Failed to marshal update order request body: %v", err)
  }
  updateOrderPath := "/orders/" + orderIDStr
  updateOrderReq, err := http.NewRequest("PUT", updateOrderPath, strings.NewReader(string(updateOrderReqJSON)))
  if err != nil {
    t.Fatalf("Failed to create update order request: %v", err)
  }
  updateOrderReq.Header.Set("Content-Type", "application/json")
  updateOrderRR := httptest.NewRecorder()
  router.ServeHTTP(updateOrderRR, updateOrderReq)
  if status := updateOrderRR.Code; status != http.StatusOK {
    t.Errorf("Update order handler returned wrong status code: got %v want %v",
      status, http.StatusOK)
  }

  // 验证更新订单的响应内容
  var updateOrderResp map[string]interface{}
  err = json.NewDecoder(updateOrderRR.Body).Decode(&updateOrderResp)
  if err != nil {
    t.Fatalf("Failed to decode update order response body: %v", err)
  }
  if updateOrderResp["ID"] == nil || updateOrderResp["CustomerName"] == nil || updateOrderResp["Amount"] == nil {
    t.Errorf("Update order response body is missing required fields")
  }

  // 测试删除订单路由
  deleteOrderPath := "/orders/" + orderIDStr
  deleteOrderReq, err := http.NewRequest("DELETE", deleteOrderPath, nil)
  if err != nil {
    t.Fatalf("Failed to create delete order request: %v", err)
  }
  deleteOrderRR := httptest.NewRecorder()
  router.ServeHTTP(deleteOrderRR, deleteOrderReq)
  if status := deleteOrderRR.Code; status != http.StatusOK {
    t.Errorf("Delete order handler returned wrong status code: got %v want %v",
      status, http.StatusOK)
  }

  // 验证删除订单的响应内容
  var deleteOrderResp map[string]interface{}
  err = json.NewDecoder(deleteOrderRR.Body).Decode(&deleteOrderResp)
  if err != nil {
    t.Fatalf("Failed to decode delete order response body: %v", err)
  }
  if deleteOrderResp["message"] == nil {
    t.Errorf("Delete order response body is missing required field 'message'")
  }
}

/*
jarry@Mac go-web % cd internal/interfaces/routes
jarry@Mac routes % go test
2022/02/22 14:57:28 REQUEST: POST /orders took 202.042µs
2022/02/22 14:57:28 REQUEST: GET /orders/70639 took 2.875µs
订单 ID 70639 的客户名称已更新为: Updated Customer
订单 ID 70639 的金额已更新为: 200.00
2022/02/22 14:57:28 REQUEST: PUT /orders/70639 took 7.708µs
2022/02/22 14:57:28 REQUEST: DELETE /orders/70639 took 2.583µs
PASS
ok      go-web-order/internal/interfaces/routes      0.530s
*/
