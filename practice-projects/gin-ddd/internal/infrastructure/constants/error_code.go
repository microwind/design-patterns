package constants

// ErrorCode 错误码
const (
	// 通用错误码
	ErrCodeSuccess           = 0
	ErrCodeBadRequest        = 400
	ErrCodeUnauthorized      = 401
	ErrCodeForbidden         = 403
	ErrCodeNotFound          = 404
	ErrCodeInternalServer    = 500

	// 业务错误码 (1000+)
	ErrCodeUserNotFound      = 1001
	ErrCodeUserAlreadyExists = 1002
	ErrCodeInvalidPassword   = 1003
	ErrCodeUserBlocked       = 1004

	ErrCodeOrderNotFound     = 2001
	ErrCodeOrderStatusInvalid = 2002
	ErrCodeOrderCannotCancel = 2003
)

// ErrorMessage 错误消息
var ErrorMessage = map[int]string{
	ErrCodeSuccess:            "成功",
	ErrCodeBadRequest:         "请求参数错误",
	ErrCodeUnauthorized:       "未授权",
	ErrCodeForbidden:          "禁止访问",
	ErrCodeNotFound:           "资源不存在",
	ErrCodeInternalServer:     "服务器内部错误",

	ErrCodeUserNotFound:       "用户不存在",
	ErrCodeUserAlreadyExists:  "用户已存在",
	ErrCodeInvalidPassword:    "密码错误",
	ErrCodeUserBlocked:        "用户已被封禁",

	ErrCodeOrderNotFound:      "订单不存在",
	ErrCodeOrderStatusInvalid: "订单状态无效",
	ErrCodeOrderCannotCancel:  "订单无法取消",
}

// GetErrorMessage 获取错误消息
func GetErrorMessage(code int) string {
	if msg, ok := ErrorMessage[code]; ok {
		return msg
	}
	return "未知错误"
}
