package controllers

import (
  "fmt"
  "net/http"

  "github.com/gin-gonic/gin"
)

// RegisterRoutes 注册 Order 相关路由
func RegisterRoutes(r *gin.Engine) {
  group := r.Group("/")
  {
    group.GET("/", GetIndex)        // 首页
    group.GET("/hello", GetWelcome) // 欢迎页
  }
}

func GetIndex(c *gin.Context) {
  // 根路径返回 HTML 页面
  port := 8080
  htmlContent := fmt.Sprintf(`
			<h1>Welcome to DDD example.</h1>
			<pre>
			测试
			<code>
			创建：curl -X POST "http://localhost:%d/api/orders" -H "Content-Type: application/json" -d '{"customer_name": "齐天大圣", "total_amount": 99.99}'
			查询：curl -X GET "http://localhost:%d/api/orders/订单号"
			更新：curl -X PUT "http://localhost:%d/api/orders/订单号" -H "Content-Type: application/json" -d '{"customer_name": "孙悟空", "total_amount": 11.22}'
			删除：curl -X DELETE "http://localhost:%d/api/orders/订单号"
			查询全部：curl -X GET "http://localhost:%d/api/orders"
			</code>
			详细：<a href="https://github.com/microwind/design-patterns/tree/main/domain-driven-design" target="_blank">GitHub 项目</a>
			</pre>
		`, port, port, port, port, port)

  c.Header("Content-Type", "text/html; charset=utf-8")
  c.String(http.StatusOK, htmlContent)
}

func GetWelcome(c *gin.Context) {
  user := c.Query("user")
  htmlContent := fmt.Sprintf(`hello %s, welcome to Go MVC example.`, user)
  c.Header("Content-Type", "text/html; charset=utf-8")
  c.String(http.StatusOK, htmlContent)
}
