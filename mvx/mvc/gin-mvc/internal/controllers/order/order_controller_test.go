package controllers

import (
  "bytes"
  "encoding/json"
  "net/http"
  "net/http/httptest"
  "testing"

  "github.com/gin-gonic/gin"
  "github.com/shopspring/decimal"
  "github.com/stretchr/testify/assert"
  "github.com/stretchr/testify/mock"

  // 使用指定的导入包方式
  models "gin-order/internal/models/order"
)

// MockOrderService 模拟 services.OrderService 接口
type MockOrderService struct {
  mock.Mock
}

// CreateOrder 模拟创建订单的服务方法，返回 *models.Order
func (m *MockOrderService) CreateOrder(o models.Order) (*models.Order, error) {
  args := m.Called(o)
  return args.Get(0).(*models.Order), args.Error(1)
}

// GetByOrderNo 模拟根据订单号获取订单的服务方法
func (m *MockOrderService) GetByOrderNo(orderNo string) (*models.Order, error) {
  args := m.Called(orderNo)
  return args.Get(0).(*models.Order), args.Error(1)
}

// GetAllOrders 模拟获取所有订单的服务方法
func (m *MockOrderService) GetAllOrders(page, pageSize int) ([]models.Order, int, error) {
  args := m.Called(page, pageSize)
  return args.Get(0).([]models.Order), args.Int(1), args.Error(2)
}

// GetOrdersByUserID 模拟根据用户ID获取订单的服务方法
func (m *MockOrderService) GetOrdersByUserID(userId string, page, pageSize int) ([]models.Order, int, error) {
  args := m.Called(userId, page, pageSize)
  return args.Get(0).([]models.Order), args.Int(1), args.Error(2)
}

// UpdateOrder 模拟更新订单的服务方法
func (m *MockOrderService) UpdateOrder(o models.Order) (*models.Order, error) {
  args := m.Called(o)
  return args.Get(0).(*models.Order), args.Error(1)
}

// UpdateOrderStatus 模拟更新订单状态的服务方法
func (m *MockOrderService) UpdateOrderStatus(orderNo, status string) (*models.Order, error) {
  args := m.Called(orderNo, status)
  return args.Get(0).(*models.Order), args.Error(1)
}

// DeleteOrder 模拟删除订单的服务方法
func (m *MockOrderService) DeleteOrder(orderNo string) error {
  args := m.Called(orderNo)
  return args.Error(0)
}

// TestCreateOrder 测试创建订单的逻辑
func TestCreateOrder(t *testing.T) {
  // 设置 Gin 为测试模式
  gin.SetMode(gin.TestMode)

  // 创建 MockOrderService 实例
  mockService := &MockOrderService{}
  // 创建 OrderController 实例，并注入 Mock 服务
  orderController := NewOrderController(mockService)

  // 构造一个 mock 订单，使用 models 包中的 Order 结构体
  expectedOrder := models.Order{
    OrderNo:   "ORD-12345",                 // 订单号
    OrderName: "Test Order",                // 订单名称
    UserID:    1,                           // 用户ID
    Amount:    decimal.NewFromFloat(100.0), // 使用 NewFromFloat 初始化 Decimal
  }

  // 使用自定义匹配器，忽略 Amount 内部表示的细微差异
  mockService.
    On("CreateOrder", mock.MatchedBy(func(o models.Order) bool {
      // 对比其他字段
      if o.OrderNo != expectedOrder.OrderNo ||
        o.OrderName != expectedOrder.OrderName ||
        o.UserID != expectedOrder.UserID {
        return false
      }
      // 使用 Decimal.Equal 比较金额数值是否一致
      return o.Amount.Equal(expectedOrder.Amount)
    })).
    Return(&expectedOrder, nil)

  // 模拟 HTTP 请求，调用 CreateOrder 接口
  resp := performRequest(orderController, "POST", "/api/orders", expectedOrder)

  // 验证响应状态码是否为 201 (Created)
  assert.Equal(t, http.StatusCreated, resp.Code)
  // 验证 Mock 服务方法是否按预期被调用
  mockService.AssertExpectations(t)
}

// performRequest 模拟执行 HTTP 请求的方法
func performRequest(controller *OrderController, method, path string, body interface{}) *httptest.ResponseRecorder {
  // 将 body 编码为 JSON 格式
  reqBody := new(bytes.Buffer)
  if err := json.NewEncoder(reqBody).Encode(body); err != nil {
    panic(err)
  }

  // 创建 HTTP 请求
  req, err := http.NewRequest(method, path, reqBody)
  if err != nil {
    panic(err)
  }

  // 创建 Gin 路由，并注册对应路由和处理函数
  router := gin.New()
  router.POST("/api/orders", controller.CreateOrder)

  // 创建响应记录器，并执行路由
  w := httptest.NewRecorder()
  router.ServeHTTP(w, req)

  return w
}
