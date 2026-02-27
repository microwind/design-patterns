package home

import "github.com/gin-gonic/gin"

func RegisterRoutes(r *gin.Engine) {
	r.GET("", func(c *gin.Context) {
		c.JSON(200, gin.H{"status": "ok", "message": "Welcome to Gin MVC scaffold"})
	})
	r.GET("/health", func(c *gin.Context) {
		c.JSON(200, gin.H{"status": "ok", "message": "service is running"})
	})
}
