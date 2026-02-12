package middleware

import (
	"gin-ddd/internal/infrastructure/common"
	"gin-ddd/pkg/utils"
	"time"

	"github.com/gin-gonic/gin"
)

// Logger 日志中间件
func Logger() gin.HandlerFunc {
	return func(c *gin.Context) {
		startTime := time.Now()

		// 处理请求
		c.Next()

		// 计算请求耗时
		latency := time.Since(startTime)

		// 获取状态码
		statusCode := c.Writer.Status()

		// 根据状态码选择日志级别
		if statusCode >= 400 {
			utils.GetLogger().Warn("HTTP请求: Method=%s, Path=%s, ClientIP=%s, StatusCode=%d, Latency=%v",
				c.Request.Method, c.Request.URL.Path, c.ClientIP(), statusCode, latency)
		} else {
			utils.GetLogger().Info("HTTP请求: Method=%s, Path=%s, ClientIP=%s, StatusCode=%d, Latency=%v",
				c.Request.Method, c.Request.URL.Path, c.ClientIP(), statusCode, latency)
		}
	}
}

// Recovery 恢复中间件（捕获 panic）
func Recovery() gin.HandlerFunc {
	return func(c *gin.Context) {
		defer func() {
			if err := recover(); err != nil {
				utils.GetLogger().Error("发生严重错误(Panic): %v, Method=%s, Path=%s, ClientIP=%s",
					err, c.Request.Method, c.Request.URL.Path, c.ClientIP())
				common.InternalServerError(c, "服务器内部错误")
				c.Abort()
			}
		}()
		c.Next()
	}
}

// CORS 跨域中间件
func CORS() gin.HandlerFunc {
	return func(c *gin.Context) {
		c.Writer.Header().Set("Access-Control-Allow-Origin", "*")
		c.Writer.Header().Set("Access-Control-Allow-Credentials", "true")
		c.Writer.Header().Set("Access-Control-Allow-Headers", "Content-Type, Content-Length, Accept-Encoding, X-CSRF-Token, Authorization, accept, origin, Cache-Control, X-Requested-With")
		c.Writer.Header().Set("Access-Control-Allow-Methods", "POST, OPTIONS, GET, PUT, DELETE, PATCH")

		if c.Request.Method == "OPTIONS" {
			utils.GetLogger().Debug("处理CORS OPTIONS预检请求: Path=%s, ClientIP=%s", c.Request.URL.Path, c.ClientIP())
			c.AbortWithStatus(204)
			return
		}

		c.Next()
	}
}
