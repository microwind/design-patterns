package middleware

import (
  "log"
  "time"

  "github.com/gin-gonic/gin"
)

// LoggingMiddleware 记录请求日志
func LoggingMiddleware() gin.HandlerFunc {
  return func(c *gin.Context) {
    start := time.Now()
    c.Next()
    duration := time.Since(start)
    log.Printf("Request: %s %s | Status: %d | Duration: %v",
      c.Request.Method, c.Request.URL.Path, c.Writer.Status(), duration)
  }
}
