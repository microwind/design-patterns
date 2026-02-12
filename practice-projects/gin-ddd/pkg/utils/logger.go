package utils

import (
	"os"

	"go.uber.org/zap"
	"go.uber.org/zap/zapcore"
)

// Logger 日志工具
type Logger struct {
	logger *zap.SugaredLogger
}

var logger *Logger

// InitLogger 初始化日志
func InitLogger() {
	config := zap.NewProductionConfig()

	// 自定义输出格式
	config.EncoderConfig.EncodeTime = zapcore.ISO8601TimeEncoder
	config.EncoderConfig.EncodeLevel = zapcore.CapitalColorLevelEncoder

	// 开发环境配置
	config.OutputPaths = []string{"stdout"}
	config.ErrorOutputPaths = []string{"stderr"}

	// 根据环境变量设置日志级别
	if os.Getenv("GIN_MODE") == "debug" || os.Getenv("DEBUG") == "true" {
		config.Level = zap.NewAtomicLevelAt(zapcore.DebugLevel)
	} else {
		config.Level = zap.NewAtomicLevelAt(zapcore.InfoLevel)
	}

	zapLogger, err := config.Build()
	if err != nil {
		panic(err)
	}
	defer zapLogger.Sync()

	logger = &Logger{
		logger: zapLogger.Sugar(),
	}
}

// GetLogger 获取日志实例
func GetLogger() *Logger {
	if logger == nil {
		InitLogger()
	}
	return logger
}

// Info 信息日志
func (l *Logger) Info(format string, v ...interface{}) {
	l.logger.Infof(format, v...)
}

// Error 错误日志
func (l *Logger) Error(format string, v ...interface{}) {
	l.logger.Errorf(format, v...)
}

// Debug 调试日志
func (l *Logger) Debug(format string, v ...interface{}) {
	l.logger.Debugf(format, v...)
}

// Warn 警告日志
func (l *Logger) Warn(format string, v ...interface{}) {
	l.logger.Warnf(format, v...)
}

// Fatal 致命错误日志
func (l *Logger) Fatal(format string, v ...interface{}) {
	l.logger.Fatalf(format, v...)
}

// GetZapLogger 获取原始的zap logger用于高级使用
func (l *Logger) GetZapLogger() *zap.Logger {
	return l.logger.Desugar()
}
