package middleware

import (
  "net/http"

  "github.com/gin-gonic/gin"
)

// AuthMiddleware 简单的身份验证中间件
func AuthMiddleware() gin.HandlerFunc {
  return func(c *gin.Context) {
    token := c.GetHeader("Authorization")
    if token != "valid-token" {
      c.JSON(http.StatusUnauthorized, gin.H{"error": "Unauthorized"})
      c.Abort()
      return
    }
    c.Next()
  }
}
