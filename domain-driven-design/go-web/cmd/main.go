// 入口页面
package main

import (
	"fmt"
	"go-web-order/internal/application/services"
	"go-web-order/internal/infrastructure/repository"
	"go-web-order/internal/interfaces/handlers"
	"go-web-order/internal/interfaces/routes"
	"net/http"
)

const port = 8080

// defaultPage 处理默认欢迎页面请求
func defaultPage(w http.ResponseWriter, r *http.Request) {
  w.Header().Set("Content-Type", "text/html; charset=utf-8")
  w.WriteHeader(http.StatusOK)

  htmlResponse := fmt.Sprintf(`
  <h1>Welcome to DDD example.</h1>
  <pre>
    测试
    <code>
    创建：curl -X POST "http://localhost:%d/api/orders" -H "Content-Type: application/json" -d '{"customer_name": "齐天大圣", "total_amount": 99.99}'
    查询：curl -X GET "http://localhost:%d/api/orders/订单号"
    更新：curl -X PUT "http://localhost:%d/api/orders/订单号" -H "Content-Type: application/json" -d '{"customer_name": "孙悟空", "total_amount": 11.22}'
    删除：curl -X DELETE "http://localhost:%d/api/orders/订单号"
    查询：curl -X GET "http://localhost:%d/api/orders/订单号"
    </code>
  </pre>
    `, port, port, port, port, port)
  w.Write([]byte(htmlResponse))
}

func main() {
  // 创建订单仓储实现
  orderRepo := repository.NewOrderRepositoryImpl()

  // 创建订单应用服务
  orderService := services.NewOrderService(orderRepo)

  // 创建 HTTP 处理器
  orderHandler := handlers.NewOrderHandler(orderService)

  // 创建路由管理器
  router := routes.NewRouter()

  // 设置订单路由
  routes.SetupOrderRoutes(router, orderHandler)

  // 创建 HTTP 多路复用器
  mux := http.NewServeMux()

  // 设置默认欢迎页面路由
  mux.HandleFunc("/", defaultPage)

  // 将自定义路由管理器挂载到 /api 路径下
  mux.Handle("/api/", http.StripPrefix("/api", router))

  // 启动 HTTP 服务器
  fmt.Printf("Starting server on :%d successfully.\n", port)
  if err := http.ListenAndServe(fmt.Sprintf(":%d", port), mux); err != nil {
    fmt.Printf("Server failed: %v\n", err)
  }
}

/*
// 在控制台运行项目
jarry@Mac go-web % go run cmd/main.go
Starting server on :8080 successfully.
// 执行curl测试用例
2022/02/22 14:20:37 REQUEST: POST /orders took 1.025708ms
2022/02/22 14:20:47 REQUEST: GET /orders/405234 took 161.334µs
订单 ID 405234 的客户名称已更新为: 孙悟空
订单 ID 405234 的金额已更新为: 11.22
2022/02/22 14:21:19 REQUEST: PUT /orders/405234 took 203.5µs
2022/02/22 14:21:22 REQUEST: GET /orders/405234 took 97.583µs
2022/02/22 14:21:39 REQUEST: DELETE /orders/405234 took 206µs
2022/02/22 14:21:45 REQUEST: GET /orders/405234 took 31.333µs
*/
