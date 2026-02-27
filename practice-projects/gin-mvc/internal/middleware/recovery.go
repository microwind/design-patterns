package middleware

import (
	"fmt"

	"gin-mvc/pkg/logger"
	"gin-mvc/pkg/response"

	"github.com/gin-gonic/gin"
)

func Recovery() gin.HandlerFunc {
	return func(c *gin.Context) {
		defer func() {
			if rec := recover(); rec != nil {
				logger.L().Error("panic recovered", "request_id", c.GetString("request_id"), "panic", fmt.Sprint(rec))
				response.InternalServerError(c, "internal server error")
				c.Abort()
			}
		}()
		c.Next()
	}
}
