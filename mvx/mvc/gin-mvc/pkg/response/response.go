package response

import (
  "github.com/gin-gonic/gin"
)

// StandardResponse 统一的 JSON 响应格式
func StandardResponse(c *gin.Context, status int, data interface{}, message string) {
  c.JSON(status, gin.H{
    "code":    status,
    "data":    data,
    "message": message,
  })
}

// SuccessResponse 统一的成功响应
func SuccessResponse(c *gin.Context, status int, data interface{}) {
  StandardResponse(c, status, data, "成功")
}

// ErrorResponse 统一的错误响应
func ErrorResponse(c *gin.Context, status int, message string) {
  StandardResponse(c, status, nil, message)
}
