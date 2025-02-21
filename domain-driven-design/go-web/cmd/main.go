// 入口页面
package main

import (
  "go-order-system/internal/application/services"
  "go-order-system/internal/infrastructure/repository"
  "go-order-system/internal/interfaces/handlers"
  "go-order-system/internal/middleware"
  "log"
  "net/http"
)

// 默认欢迎页面
func defaultPage(w http.ResponseWriter, r *http.Request) {
  w.Header().Set("Content-Type", "text/html; charset=utf-8")
  w.WriteHeader(http.StatusOK)

  htmlResponse := `
  <h1>Welcome to DDD example.</h1>
  <pre>
    测试
    <code>
    创建：curl -X POST "http://localhost:8080/orders/create" -H "Content-Type: application/json" -d '{"customer_name": "齐天大圣", "total_amount": 99.99}'
    查询：curl -X GET "http://localhost:8080/orders/get?id=订单号"
    更新：curl -X PUT "http://localhost:8080/orders/update?id=订单号" -H "Content-Type: application/json" -d '{"customer_name": "孙悟空", "total_amount": 11.22}'
    删除：curl -X DELETE "http://localhost:8080/orders/delete?id=订单号"
    查询：curl -X GET "http://localhost:8080/orders/get?id=订单号"
    </code>
  </pre>
	`
  w.Write([]byte(htmlResponse))
}

func initRouter(orderHandler *handlers.OrderHandler) {
  // 配置路由并将中间件应用到每个处理器
  http.HandleFunc("/", middleware.LoggingMiddleware(defaultPage))                           // 欢迎页面
  http.HandleFunc("/orders/create", middleware.LoggingMiddleware(orderHandler.CreateOrder)) // 创建订单
  http.HandleFunc("/orders/get", middleware.LoggingMiddleware(orderHandler.GetOrder))       // 查询订单
  http.HandleFunc("/orders/update", middleware.LoggingMiddleware(orderHandler.UpdateOrder)) // 更新订单
  http.HandleFunc("/orders/delete", middleware.LoggingMiddleware(orderHandler.DeleteOrder)) // 删除订单
}

func main() {
  // 创建订单仓储实现
  orderRepo := repository.NewOrderRepositoryImpl()

  // 创建订单应用服务
  orderService := services.NewOrderService(orderRepo)

  // 创建 HTTP 处理器
  orderHandler := handlers.NewOrderHandler(orderService)

  initRouter(orderHandler)

  // 打印日志确认服务器启动
  log.Println("Starting server on :8080 successfully.")

  // 启动 HTTP 服务器
  if err := http.ListenAndServe(":8080", nil); err != nil {
    log.Fatalf("Server failed: %v", err)
  }
}

/*
// 在控制台运行项目
jarry@MacBook-Pro go-web % go run cmd/main.go

// 执行curl测试，可以得到结果，控制台输出如下
2023/04/21 15:43:45 Starting server on :8080 successfully.
2023/04/21 15:43:50 REQUEST: POST /orders/create took 147.375µs
2023/04/21 15:44:00 REQUEST: GET /orders/get took 152.833µs
2023/04/21 15:44:20 REQUEST: PUT /orders/update took 140µs
2023/04/21 15:44:29 REQUEST: GET /orders/get took 44µs
2023/04/21 15:44:41 REQUEST: DELETE /orders/delete took 116.167µs
2023/04/21 15:44:44 REQUEST: GET /orders/get took 13.166µs
*/
