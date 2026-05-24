// Package errors 定义领域错误类型。
//
// 与 errors.New("xxx") 字符串错误不同,DomainError 携带语义编码与消息,
// 上层(如 HTTP 适配器)可据此映射到合适的状态码或前端提示。
package errors

import (
	"errors"
	"fmt"
)

// Code 领域错误编码,语义化区分错误类型。
type Code string

const (
	CodeNotFound             Code = "ENTITY_NOT_FOUND"
	CodeUniquenessViolation  Code = "UNIQUENESS_VIOLATION"
	CodeInvalidState         Code = "INVALID_STATE"
	CodeInvalidArgument      Code = "INVALID_ARGUMENT"
)

// DomainError 领域错误。字段全部私有,创建后不可篡改,符合领域事件/异常的不可变约定。
type DomainError struct {
	code    Code
	message string
}

func (e *DomainError) Error() string {
	return e.message
}

// Code 返回错误编码。
func (e *DomainError) Code() Code {
	return e.code
}

// New 创建领域错误,通常由聚合根 / 领域服务使用。
func New(code Code, message string) *DomainError {
	return &DomainError{code: code, message: message}
}

// NewNotFound 构造"实体不存在"错误,key/value 用于拼接定位信息。
func NewNotFound(entity, key string, value any) *DomainError {
	return &DomainError{
		code:    CodeNotFound,
		message: fmt.Sprintf("%s不存在: %s=%v", entity, key, value),
	}
}

// NewUniquenessViolation 构造"唯一性约束违反"错误。
func NewUniquenessViolation(message string) *DomainError {
	return &DomainError{code: CodeUniquenessViolation, message: message}
}

// NewInvalidState 构造"状态不允许操作"错误,常用于聚合根行为方法守卫不变量失败。
func NewInvalidState(message string) *DomainError {
	return &DomainError{code: CodeInvalidState, message: message}
}

// NewInvalidArgument 构造"入参非法"错误,常用于工厂方法校验。
func NewInvalidArgument(message string) *DomainError {
	return &DomainError{code: CodeInvalidArgument, message: message}
}

// IsNotFound 判定错误是否为"实体不存在"。
func IsNotFound(err error) bool {
	var de *DomainError
	return errors.As(err, &de) && de.code == CodeNotFound
}

// IsUniquenessViolation 判定错误是否为"唯一性约束违反"。
func IsUniquenessViolation(err error) bool {
	var de *DomainError
	return errors.As(err, &de) && de.code == CodeUniquenessViolation
}

// As 暴露 errors.As 便于上层调用方按需要做类型断言。
func As(err error, target **DomainError) bool {
	return errors.As(err, target)
}
