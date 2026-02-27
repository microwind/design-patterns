package response

import "github.com/gin-gonic/gin"

type Body struct {
	Code    int         `json:"code"`
	Message string      `json:"message"`
	Data    interface{} `json:"data,omitempty"`
}

func Success(c *gin.Context, data interface{}) {
	c.JSON(200, Body{Code: 0, Message: "success", Data: data})
}

func SuccessWithMessage(c *gin.Context, msg string, data interface{}) {
	c.JSON(200, Body{Code: 0, Message: msg, Data: data})
}

func Error(c *gin.Context, code int, msg string) {
	c.JSON(200, Body{Code: code, Message: msg})
}

func BadRequest(c *gin.Context, msg string) {
	c.JSON(400, Body{Code: 400, Message: msg})
}

func InternalServerError(c *gin.Context, msg string) {
	c.JSON(500, Body{Code: 500, Message: msg})
}
