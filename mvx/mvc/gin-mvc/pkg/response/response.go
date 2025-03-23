package pkg

import (
  "github.com/gin-gonic/gin"
)

// JSONResponse 统一的 JSON 响应
func JSONResponse(c *gin.Context, status int, data interface{}) {
  c.JSON(status, gin.H{"data": data})
}

// ErrorResponse 统一的错误响应
func ErrorResponse(c *gin.Context, status int, message string) {
  c.JSON(status, gin.H{"error": message})
}
