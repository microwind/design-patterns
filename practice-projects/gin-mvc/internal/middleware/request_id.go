package middleware

import (
	"context"
	"fmt"
	"time"

	"github.com/gin-gonic/gin"
)

func RequestID() gin.HandlerFunc {
	return func(c *gin.Context) {
		requestID := fmt.Sprintf("req-%d", time.Now().UnixNano())
		c.Set("request_id", requestID)
		c.Request = c.Request.WithContext(context.WithValue(c.Request.Context(), requestIDContextKey, requestID))
		c.Request = c.Request.WithContext(context.WithValue(c.Request.Context(), "request_id", requestID))
		c.Writer.Header().Set("X-Request-ID", requestID)
		c.Next()
	}
}
