package common

import (
	"net/http"

	domainErrors "gin-ddd/internal/domain/errors"

	"github.com/gin-gonic/gin"
)

// Response 统一响应结构
type Response struct {
	Code    int         `json:"code"`
	Message string      `json:"message"`
	Data    interface{} `json:"data,omitempty"`
}

// Success 成功响应
func Success(c *gin.Context, data interface{}) {
	c.JSON(http.StatusOK, Response{
		Code:    0,
		Message: "success",
		Data:    data,
	})
}

// SuccessWithMessage 带消息的成功响应
func SuccessWithMessage(c *gin.Context, message string, data interface{}) {
	c.JSON(http.StatusOK, Response{
		Code:    0,
		Message: message,
		Data:    data,
	})
}

// Error 错误响应
func Error(c *gin.Context, code int, message string) {
	c.JSON(http.StatusOK, Response{
		Code:    code,
		Message: message,
	})
}

// ErrorWithData 带数据的错误响应
func ErrorWithData(c *gin.Context, code int, message string, data interface{}) {
	c.JSON(http.StatusOK, Response{
		Code:    code,
		Message: message,
		Data:    data,
	})
}

// BadRequest 请求参数错误
func BadRequest(c *gin.Context, message string) {
	c.JSON(http.StatusBadRequest, Response{
		Code:    http.StatusBadRequest,
		Message: message,
	})
}

// Unauthorized 未授权
func Unauthorized(c *gin.Context, message string) {
	c.JSON(http.StatusUnauthorized, Response{
		Code:    http.StatusUnauthorized,
		Message: message,
	})
}

// Forbidden 禁止访问
func Forbidden(c *gin.Context, message string) {
	c.JSON(http.StatusForbidden, Response{
		Code:    http.StatusForbidden,
		Message: message,
	})
}

// NotFound 资源不存在
func NotFound(c *gin.Context, message string) {
	c.JSON(http.StatusNotFound, Response{
		Code:    http.StatusNotFound,
		Message: message,
	})
}

// InternalServerError 服务器内部错误
func InternalServerError(c *gin.Context, message string) {
	c.JSON(http.StatusInternalServerError, Response{
		Code:    http.StatusInternalServerError,
		Message: message,
	})
}

// RespondError 根据 err 类型映射 HTTP 状态码与业务码,统一错误返回入口。
//
//   - *domainErrors.DomainError:按 Code 映射(NotFound→404, Uniqueness→409,
//     InvalidState/InvalidArgument→400),业务码使用 HTTP 状态码值。
//   - 其他 error:视为内部错误,返回 500。
//
// 调用方只需传入 service 返回的 err,无需关心错误分类。
func RespondError(c *gin.Context, err error) {
	if err == nil {
		return
	}

	var de *domainErrors.DomainError
	if domainErrors.As(err, &de) {
		status := domainErrorStatus(de.Code())
		c.JSON(status, Response{
			Code:    status,
			Message: de.Error(),
		})
		return
	}

	c.JSON(http.StatusInternalServerError, Response{
		Code:    http.StatusInternalServerError,
		Message: err.Error(),
	})
}

func domainErrorStatus(code domainErrors.Code) int {
	switch code {
	case domainErrors.CodeNotFound:
		return http.StatusNotFound
	case domainErrors.CodeUniquenessViolation:
		return http.StatusConflict
	case domainErrors.CodeInvalidState, domainErrors.CodeInvalidArgument:
		return http.StatusBadRequest
	default:
		return http.StatusBadRequest
	}
}
